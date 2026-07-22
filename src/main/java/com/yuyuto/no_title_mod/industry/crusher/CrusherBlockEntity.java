package com.yuyuto.no_title_mod.industry.crusher;

import com.yuyuto.no_title_mod.api.energy.*;
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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CrusherBlockEntity extends BlockEntity implements INTEnergyNodeManagements {

    private int progress;
    private final ItemStackHandler inventory = new ItemStackHandler(2);
    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> inventory);
    private final NTEnergyNode energyNode = new NTEnergyNode();
    private final double REQUIRED_POWER = 1000.0;

    // ============================弄らない==============================
    public CrusherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRUSHER.get(), pos, state);
        energyNode.setType(NTEnergyNodeType.CONSUMER);
    }

    @Override
    public NTEnergyNode getNode(){
        return energyNode;
    }

    @Override
    public void updateEnergyNode() {
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level == null || level.isClientSide) {
            return;
        }
        NTEnergyNetworkManager.updateAround(level, worldPosition);
    }

    // =======================NBT系は触れたらダメ=========================
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag){
        tag.put("inventory", inventory.serializeNBT());
        tag.put("EnergyNode", energyNode.saveNBT());
        super.saveAdditional(tag);
    }

    @Override
    public void load(@NotNull CompoundTag tag){
        super.load(tag);
        energyNode.loadNBT(tag.getCompound("EnergyNode"));
        inventory.deserializeNBT(tag.getCompound("inventory"));
    }

    //=================================================================

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability){
        if (capability == ForgeCapabilities.ITEM_HANDLER){
            return itemHandler.cast();
        }
        return super.getCapability(capability);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
    }

    private void pullItem(@NotNull BlockPos pos){

        BlockEntity target = Objects.requireNonNull(level).getBlockEntity(pos.relative(Direction.EAST));

        if(target == null) return;
        target.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            ItemStack item = handler.extractItem(0,1,false);

            if(!item.isEmpty()){
                inventory.insertItem(0, item, false);
            }
        });
    }

    private void pushItem(@NotNull BlockPos pos){
        BlockEntity target = Objects.requireNonNull(level).getBlockEntity(pos.relative(Direction.WEST));

        if (target == null) return;
        target.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            ItemStack output = inventory.getStackInSlot(1);
            ItemStack remain = handler.insertItem(0, output, false);
            inventory.setStackInSlot(1, remain);
        });
    }

    @SuppressWarnings("unused")
    public static void tick(Level level, BlockPos pos, BlockState state, @NotNull CrusherBlockEntity entity) {
        if(!NTEnergyManager.canOperate(entity.energyNode.getPower(), entity.REQUIRED_POWER)){
            entity.progress = 0;
            return;
        }
        entity.pullItem(pos);
        entity.pushItem(pos);
        entity.progress++;
        if (entity.progress >= 100) {
            entity.process();
            entity.progress = 0;
        }
        if (entity.progress % 5 == 0){
            if(level instanceof ServerLevel serverLevel){
             serverLevel.sendParticles(ParticleTypes.POOF, pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5, 2, 0.2, 0.1, 0.2, 0.01);
            }
        }
        if(entity.progress % 20 == 0){
            level.playSound(null, pos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 0.5F, 1.0F);
        }
    }

    private void process() {
        ItemStack input = inventory.getStackInSlot(0);
        ItemStack output = inventory.getStackInSlot(1);
        double requiredPower = 100;
        CrusherRecipe recipe = CrusherRecipeManager.find(input);

        if (!NTEnergyManager.canOperate(energyNode.getPower(), requiredPower)){
            return;
        }

        if (recipe != null && output.isEmpty()) {
            input.shrink(1);
            inventory.setStackInSlot(1, recipe.createResult());
            energyNode.setPower(
                    NTEnergyManager.decreasePower(
                            energyNode.getPower(),
                            requiredPower
                    )
            );

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, worldPosition.getX() + 0.5, worldPosition.getY() + 0.8, worldPosition.getZ() + 0.5, 12, 0.3, 0.2, 0.3, 0.1);
                serverLevel.sendParticles(ParticleTypes.POOF, worldPosition.getX() + 0.5, worldPosition.getY() + 0.8, worldPosition.getZ() + 0.5, 12, 0.3, 0.2, 0.3, 0.1);
                serverLevel.sendParticles(ParticleTypes.CRIT, worldPosition.getX() + 0.5, worldPosition.getY() + 0.8, worldPosition.getZ() + 0.5, 12, 0.3, 0.2, 0.3, 0.1);
            }
            if (level != null) {
                level.playSound(null, worldPosition, SoundEvents.ANVIL_HIT, SoundSource.BLOCKS, 0.3F, 1.0F);
                level.playSound(null, worldPosition, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 0.8F, 1.0F);
            }
        }
    }
}