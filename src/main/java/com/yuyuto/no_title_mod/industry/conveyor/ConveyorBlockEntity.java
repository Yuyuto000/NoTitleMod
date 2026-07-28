package com.yuyuto.no_title_mod.industry.conveyor;

import com.yuyuto.no_title_mod.NoTitleMod;
import com.yuyuto.no_title_mod.api.energy.INTEnergyNode;
import com.yuyuto.no_title_mod.api.energy.NTEnergyNodeType;
import com.yuyuto.no_title_mod.api.energy.NTEnergyPacket;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConveyorBlockEntity extends InventoryBlockEntity implements INTEnergyNode {

    private static final float DEFAULT_SPEED = 0.05f;
    private float speed = DEFAULT_SPEED;
    private float beltOffset = 0.0f;
    private static final double REQUIRED_ENERGY = 200;
    private double energyBuffer;
    private long itemStartTick;
    private long lastReceiveTick;
    private ConveyorShape shape = ConveyorShape.SINGLE;

    public ConveyorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CONVEYOR.get(), pos, state, 1);
    }

    @Contract(pure = true)
    public static void tick(@NotNull Level level, BlockPos pos, BlockState state, @NotNull ConveyorBlockEntity entity) {

        if(level.isClientSide){
            entity.tickAnimation();
            return;
        }
        if(!entity.isPowered()) {
            return;
        }
        if(entity.energyBuffer <= REQUIRED_ENERGY) return;
        entity.tickServer();
        entity.energyBuffer = 0;
    }

    private void tickServer() {
        if (!isPowered()) return;
        updateShape();
        moveItems();
    }

    private void updateShape() {

        boolean front = hasFrontConveyor();
        boolean back = hasBackConveyor();

        if(front && back) {
            shape = ConveyorShape.MIDDLE;
        } else if(front) {
            shape = ConveyorShape.START;
        } else if(back) {
            shape = ConveyorShape.END;
        } else {
            shape = ConveyorShape.SINGLE;
        }
    }

    private void tickAnimation(){

        if(!isPowered()) return;
        beltOffset += speed;
        if(beltOffset >= 1F)
            beltOffset -= 0.1F;
    }

    private void moveItems(){

        pickupItemEntity();
        if(hasItem()) return;
        if(!hasArrived()) return;
        stageTransfer();
    }

    private void stageTransfer(){

        if(!(level instanceof ServerLevel serverLevel))
            return;
        BlockPos target = worldPosition.relative(getDirection());
        ConveyorTransferQueue.stage(serverLevel, worldPosition, target, getDirection(), getStack(0), level.getGameTime()+1);
    }

    private void pickupItemEntity() {

        if(level == null) return;
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
            source.shrink(1);
            if(source.isEmpty()){
                entity.discard();
            }
        }
    }

    public void removeItem(){

        getInventory().extractItem(0, 1, false);
        if (level != null) {
            itemStartTick = level.getGameTime();
        }
        setChanged();
    }

    public void dropItem() {

        if(level == null) return;
        ItemStack drop = getInventory().extractItem(0, 1, false);
        if(drop.isEmpty()) return;
        itemStartTick = level.getGameTime();
        ItemEntity entity = new ItemEntity(level, worldPosition.getX() + 0.5 + getDirection().getStepX() * 0.7, worldPosition.getY() + 0.8, worldPosition.getZ() + 0.5 + getDirection().getStepZ() * 0.7, drop);
        entity.setDeltaMovement(getDirection().getStepX() * 0.08, 0, getDirection().getStepZ() * 0.08);
        level.addFreshEntity(entity);
    }

    public @NotNull Direction getDirection(){
        return getBlockState().getValue(ConveyorBlock.FACING);
    }
    public boolean hasFrontConveyor(){

        if(level == null)
            return false;
        BlockEntity entity = level.getBlockEntity(worldPosition.relative(getDirection()));
        if(!(entity instanceof ConveyorBlockEntity conveyor))
            return false;
        return conveyor.getDirection() == getDirection();
    }
    public boolean hasBackConveyor() {

        if(level == null)
            return false;
        BlockEntity entity = level.getBlockEntity(worldPosition.relative(getDirection().getOpposite()));
        if(!(entity instanceof ConveyorBlockEntity conveyor))
            return false;
        return conveyor.getDirection() == getDirection();
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(){
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }
    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        load(tag);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
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

        if(level == null)
            return 0;
        if(!isPowered()) return beltOffset;
        return (float)(((level.getGameTime() + partialTick) * speed) % 1.0);
    }

    private boolean hasArrived(){
        if(level == null) return false;
        return (level.getGameTime() - itemStartTick) * speed >= 1F;
    }

    public float getSpeed() {
        return speed;
    }
    public ConveyorShape getShape() {
        return shape;
    }

    public boolean hasItem(){
        return !getStack(0).isEmpty();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag){
        super.saveAdditional(tag);
        tag.putFloat("Speed", speed);
        tag.putLong("ItemStartTick", itemStartTick);
        tag.putDouble("Energy", energyBuffer);
        tag.putLong("LastReceiveTick", lastReceiveTick);
    }
    @Override
    public void load(@NotNull CompoundTag tag){
        super.load(tag);
        speed = tag.getFloat("Speed");
        itemStartTick = tag.getLong("ItemStartTick");
        energyBuffer = tag.getDouble("Energy");
        lastReceiveTick = tag.getLong("LastReceiveTick");
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public NTEnergyNodeType getNodeType() {
        return NTEnergyNodeType.CONSUMER;
    }

    @Override
    public BlockPos getPos() {
        return worldPosition;
    }

    @Override
    public void receivePacket(NTEnergyPacket packet) {
        this.energyBuffer = packet.energy();
        if (level != null) {
            this.lastReceiveTick = level.getGameTime();
        }

        if (level instanceof ServerLevel server) {
            setChanged();
            server.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public boolean isPowered(){
        if (level == null) return false;
        NoTitleMod.LOGGER.info("[ConveyorBlockEntity] isPowered: {}", level.getGameTime() - this.lastReceiveTick <=2 && energyBuffer > 0);
        NoTitleMod.LOGGER.info("[ConveyorBlockEntity] Monitoring: pos={}, {}-{}<=2 && {} > 0", getPos(), level.getGameTime(), this.lastReceiveTick, energyBuffer);
        return  level.getGameTime() - this.lastReceiveTick <=2 && energyBuffer > 0;
    }
}