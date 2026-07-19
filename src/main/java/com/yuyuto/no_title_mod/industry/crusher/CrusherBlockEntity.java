package com.yuyuto.no_title_mod.industry.crusher;

import com.yuyuto.no_title_mod.api.energy.*;
import com.yuyuto.no_title_mod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
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

    // ============================弄らない==============================
    public CrusherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRUSHER.get(), pos, state);
        energyNode.setType(NTEnergyNodeType.CONSUMER);
    }

    @Override
    public void connection(NTEnergyNetwork network) {
    }

    @Override
    public void disconnect() {
    }

    @Override
    public NTEnergyNode getNode(){
        return energyNode;
    }

    @Override
    public BlockPos getNodePosition() {
        return worldPosition;
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

    @Override
    public void setRemoved() {
        if (network != null) {
            NTEnergyNetworkManager.rebuildNetwork(level, network);
        }
        super.setRemoved();
    }

    // =======================NBT系は触れたらダメ=========================
    @Override
    protected void saveAdditional(CompoundTag tag){
        tag.put("inventory", inventory.serializeNBT());
        super.saveAdditional(tag);
    }

    @Override
    public void load(@NotNull CompoundTag tag){
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound("inventory"));
    }
    //===================================================================

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

        BlockEntity target = Objects.requireNonNull(level).getBlockEntity(pos.relative(Direction.UP));

        if(target == null) return;
        target.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            ItemStack item = handler.extractItem(0,1,false);

            if(!item.isEmpty()){
                inventory.insertItem(0, item, false);
            }
        });
    }

    private void pushItem(@NotNull BlockPos pos){
        BlockEntity target = Objects.requireNonNull(level).getBlockEntity(pos.relative(Direction.DOWN));

        if (target == null) return;
        target.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            ItemStack output = inventory.getStackInSlot(1);
            ItemStack remain = handler.insertItem(0, output, false);
            inventory.setStackInSlot(1, remain);
        });
    }

    public static void tick(BlockPos pos, @NotNull CrusherBlockEntity entity) {

        entity.pullItem(pos);
        entity.pushItem(pos);
        entity.progress++;
        if (entity.progress >= 100) {
            entity.process();
            entity.progress = 0;
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

        if (recipe != null && output.isEmpty()){
            input.shrink(1);
            inventory.setStackInSlot(1, recipe.createResult());

            energyNode.setPower(NTEnergyManager.decreasePower(
                    energyNode.getPower(),
                    requiredPower
            ));
        }
    }
}