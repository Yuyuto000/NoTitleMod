package com.yuyuto.no_title_mod.industry.crusher;

import com.yuyuto.no_title_mod.NoTitleMod;
import com.yuyuto.no_title_mod.api.energy.INTEnergyNode;
import com.yuyuto.no_title_mod.api.energy.NTEnergyNodeType;
import com.yuyuto.no_title_mod.api.energy.NTEnergyPacket;
import com.yuyuto.no_title_mod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class CrusherBlockEntity extends BlockEntity implements INTEnergyNode {

    private int progress;
    // 0=input,1=output
    private final ItemStackHandler inventory = new ItemStackHandler(2);
    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> inventory);
    private NTEnergyPacket packet;
    private double energy = packet.energy();
    private static final double REQUIRED_ENERGY = 2000;
    private static final double ENERGY_USAGE = 1;
    public CrusherBlockEntity(BlockPos pos, BlockState state){
        super(ModBlockEntities.CRUSHER.get(), pos, state);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag){
        tag.put("inventory", inventory.serializeNBT());
        if(packet != null) tag.put("Packet", packet.save());
        super.saveAdditional(tag);
    }

    @Override
    public void load(@NotNull CompoundTag tag){
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound("inventory"));
        if(tag.contains("Packet"))  packet = NTEnergyPacket.load(tag.getCompound("Packet"));
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction side){

        if(capability == ForgeCapabilities.ITEM_HANDLER){
            return itemHandler.cast();
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void invalidateCaps(){
        super.invalidateCaps();
        itemHandler.invalidate();
    }

    // tick
    @SuppressWarnings("unused")
    public static void tick(Level level, BlockPos pos, BlockState state, @NotNull CrusherBlockEntity entity){

        // Packetなし
        if (entity.packet == null){
            entity.progress = 0;
            return;
        }
        //電力不足
        if (entity.packet.time() != level.getGameTime()) return;
        if(entity.energy <= REQUIRED_ENERGY){
            entity.progress = 0;
            entity.energy = entity.packet.energy();
            return;
        }
        //　消費
        entity.packet.consume(ENERGY_USAGE);
        // 稼働
        entity.pullItem(pos);
        entity.pushItem(pos);
        entity.progress++;
        if (entity.progress >= 100) {
            entity.process();
            entity.progress = 0;

        }
        // 演出
        if (entity.progress % 5 == 0 && level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.POOF, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 2, 0.2, 0.1, 0.2, 0.01);
        }
        entity.energy = 0;

    }

    private void pullItem(@NotNull BlockPos pos){

        BlockEntity target = null;
        if (level != null) {
            target = level.getBlockEntity(pos.relative(Direction.EAST));
        }
        if(target == null)return;
        target.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            ItemStack item = handler.extractItem(0, 1, false);
            if(!item.isEmpty()){
                inventory.insertItem(0, item, false);
            }
        });
    }

    private void pushItem(@NotNull BlockPos pos){
        BlockEntity target = null;
        if (level != null) {
            target = level.getBlockEntity(pos.relative(Direction.WEST));
        }
        if(target == null)return;
        target.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            ItemStack output = inventory.getStackInSlot(1);
            inventory.setStackInSlot(1, handler.insertItem(0, output, false));
        });
    }

    private void process(){
        ItemStack input = inventory.getStackInSlot(0);
        ItemStack output = inventory.getStackInSlot(1);
        CrusherRecipe recipe = CrusherRecipeManager.find(input);
        if(recipe == null)return;
        if(!output.isEmpty())return;
        input.shrink(1);
        inventory.setStackInSlot(1, recipe.createResult());
        if(level instanceof ServerLevel server){
            server.sendParticles(ParticleTypes.CRIT, worldPosition.getX()+0.5, worldPosition.getY()+1, worldPosition.getZ()+0.5, 10, 0.2, 0.2, 0.2, 0.1);
        }
        if (level != null) {
            level.playSound(null, worldPosition, SoundEvents.ANVIL_HIT, SoundSource.BLOCKS, 0.5F, 1.0F);
        }
    }

    @Override
    public NTEnergyNodeType getNodeType() {
        return NTEnergyNodeType.CONSUMER;
    }

    @Override
    public NTEnergyPacket getPacket() {
        return packet;
    }

    @Override
    public BlockPos getPos() {
        return worldPosition;
    }

    @Override
    public void receivePacket(NTEnergyPacket packet) {
        if(level != null && level.getGameTime() % 20 == 0) NoTitleMod.LOGGER.info("[Crusher Receive] pos={} energy={} time={}", worldPosition, packet.energy(), packet.time());
       if (packet.time() == level.getGameTime()){
           this.packet = packet;
       }
        setChanged();
    }
}