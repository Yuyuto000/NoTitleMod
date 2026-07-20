package com.yuyuto.no_title_mod.api.utils;

import com.lowdragmc.lowdraglib.side.item.IItemTransfer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class ItemTransferWrapper implements IItemTransfer {

    private final ItemStackHandler inventory;

    public ItemTransferWrapper(ItemStackHandler inventory) {
        this.inventory = inventory;
    }

    @Override
    public int getSlots() {
        return inventory.getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return inventory.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate, boolean notifyChanges) {
        return inventory.insertItem(slot, stack, simulate);
    }


    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate, boolean notifyChanges) {
        return inventory.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return inventory.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return inventory.isItemValid(slot, stack);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public @NotNull Object createSnapshot() {
        return inventory.serializeNBT();
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void restoreFromSnapshot(Object snapshot) {
        if(snapshot instanceof CompoundTag tag){
            inventory.deserializeNBT(tag);
        }
    }
}