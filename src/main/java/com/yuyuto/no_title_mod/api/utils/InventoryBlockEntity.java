package com.yuyuto.no_title_mod.api.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * インベントリを持つBlockEntityの基底クラス。
 *
 * <p>
 * NoTitleの物流システムで使用する基本クラス。
 * 機械・コンベア・ロボットアームなど
 * アイテムを保持するBlockEntityはこのクラスを継承することを推奨する。
 * </p>
 *
 * <h2>責務</h2>
 *
 * <ul>
 *     <li>ItemStackの保存</li>
 *     <li>NBT保存</li>
 *     <li>Capability公開</li>
 *     <li>インベントリ変更通知</li>
 * </ul>
 */
public abstract class InventoryBlockEntity extends BlockEntity {

    protected final ItemStackHandler inventory; //内部インベントリ。
    protected LazyOptional<IItemHandler> inventoryCapability = LazyOptional.empty(); // Forge Capability。

    /**
     * @param type BlockEntityType
     * @param pos BlockPos
     * @param state BlockState
     * @param slots スロット数
     */
    protected InventoryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int slots) {
        super(type, pos, state);
        inventory = new ItemStackHandler(slots) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }

    @Override
    public void onLoad() {
        super.onLoad();
        inventoryCapability = LazyOptional.of(() -> inventory);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventoryCapability.invalidate();
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {

        if(capability == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryCapability.cast();
        }
        return super.getCapability(capability, side);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", inventory.serializeNBT());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound("Inventory"));
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        handleUpdateTag(pkt.getTag());
    }

    /**
     * インベントリ取得。
     *
     * @return ItemStackHandler
     */
    public ItemStackHandler getInventory() {
        return inventory;
    }

    /**
     * 指定スロットのアイテム取得。
     *
     * @param slot スロット番号
     * @return ItemStack
     */
    public ItemStack getStack(int slot) {
        return inventory.getStackInSlot(slot);
    }

    /**
     * スロット数取得。
     *
     * @return スロット数
     */
    public int getSlotCount() {
        return inventory.getSlots();
    }

    /**
     * 指定スロットへItemStackを設定する。
     *
     * @param slot 設定先スロット
     * @param stack 設定するItemStack
     */
    public void setStack(int slot, ItemStack stack) {
        inventory.setStackInSlot(slot, stack);
        setChanged();
        if(level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }
}
