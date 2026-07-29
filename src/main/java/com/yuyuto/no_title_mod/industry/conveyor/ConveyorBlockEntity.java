package com.yuyuto.no_title_mod.industry.conveyor;

import com.yuyuto.no_title_mod.api.utils.InventoryBlockEntity;
import com.yuyuto.no_title_mod.api.utils.InventoryTransferHelper;
import com.yuyuto.no_title_mod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConveyorBlockEntity extends InventoryBlockEntity {

    private static final float DEFAULT_SPEED = 0.05f;
    private float speed = DEFAULT_SPEED;
    private long itemStartTick;
    private long nextPickupTick;

    public ConveyorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CONVEYOR.get(), pos, state, 1);
    }

    @Contract(pure = true)
    public static void tick(@NotNull Level level, BlockPos pos, BlockState state, @NotNull ConveyorBlockEntity entity) {
        if (level.isClientSide) {
            return;
        }
        entity.tickServer();
    }

    private void tickServer() {

        pickupItemEntity();
        if (getStack(0).isEmpty()) return;
        assert level != null;
        float progress = (level.getGameTime() - itemStartTick) * speed;
        if (progress >= 1){
            transferItem();
        }
    }

    private void transferItem(){
        if(!(level instanceof ServerLevel server)) return;
        ItemStack stack = getInventory().extractItem(0, 1, false);
        if (stack.isEmpty()) return;
        BlockPos target = worldPosition.relative(getDirection());

        ConveyorTransferQueue.stage(server, target, stack, level.getGameTime()+1);
    }

    private void pickupItemEntity() {

        if(level == null) return;
        if(level.getGameTime() < nextPickupTick) return;
        if(!getStack(0).isEmpty()) return;
        AABB area = new AABB(worldPosition)
                .move(0,0.8,0)
                .inflate(0.25,0.1,0.25);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);
        if(items.isEmpty()) return;
        ItemEntity entity = items.get(0);
        ItemStack source = entity.getItem();
        ItemStack insert = source.copy();
        insert.setCount(1);
        ItemStack remain = InventoryTransferHelper.insertItem(getInventory(), insert);
        if(remain.isEmpty()){
            itemStartTick = level.getGameTime();
            nextPickupTick = level.getGameTime() + 10;
            source.shrink(1);
            if(source.isEmpty()){
                entity.discard();
            }
        }
    }

    public @NotNull Direction getDirection(){
        return getBlockState().getValue(ConveyorBlock.FACING);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        load(tag);
    }

    @Override
    public void onDataPacket(Connection net, @NotNull ClientboundBlockEntityDataPacket pkt) {
        if (pkt.getTag() != null) {
            load(pkt.getTag());
        }
    }

    public float getRenderItemOffset(float partialTick){

        if(level == null) return 0F;
        if(hasItem()) return 0F;
        float offset = ((level.getGameTime()-itemStartTick)+partialTick)*speed;
        return Math.min(offset,1F);
    }
    public float getRenderBeltOffset(float partialTick){
        if(level == null) return 0;
        return (float)(((level.getGameTime() + partialTick) * speed) % 1.0);
    }

    public boolean hasItem(){
        return !getStack(0).isEmpty();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag){
        super.saveAdditional(tag);
        tag.putFloat("Speed", speed);
        tag.putLong("ItemStartTick", itemStartTick);
    }
    @Override
    public void load(@NotNull CompoundTag tag){
        super.load(tag);
        speed = tag.getFloat("Speed");
        itemStartTick = tag.getLong("ItemStartTick");
    }
}