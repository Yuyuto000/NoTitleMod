package com.yuyuto.no_title_mod.industry.conveyor;

import com.yuyuto.no_title_mod.NoTitleMod;
import com.yuyuto.no_title_mod.api.energy.INTEnergyConsumer;
import com.yuyuto.no_title_mod.api.utils.InventoryBlockEntity;
import com.yuyuto.no_title_mod.api.utils.InventoryTransferHelper;
import com.yuyuto.no_title_mod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConveyorBlockEntity extends InventoryBlockEntity implements INTEnergyConsumer {

    private static final float DEFAULT_SPEED = 0.05f;
    private float speed = DEFAULT_SPEED;
    private int transferCooldown;
    private float beltOffset = 0.0f;
    private float itemOffset = 0.0f;
    private boolean powered;
    private ConveyorShape shape = ConveyorShape.SINGLE;

    public ConveyorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CONVEYOR.get(), pos, state, 1);
    }

    @Contract(pure = true)
    public static void tick(@NotNull Level level, BlockPos pos, BlockState state, @NotNull ConveyorBlockEntity entity) {
        if (level.isClientSide) {
            entity.tickAnimation();
            return;
        }
        if (entity.powered) {
            entity.updateShape();
            entity.moveItems();
        }
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

        beltOffset += speed;
        if(beltOffset >= 1.0f)
            beltOffset -= 1.0f;
        itemOffset += speed;
        if(itemOffset >= 1.0f)
            itemOffset -= 1.0f;
    }

    private void moveItems(){

        if(transferCooldown > 0){
            transferCooldown--;
            return;
        }
        pickupItemEntity();
        transferToFront();
        transferCooldown = 5;
    }

    /**
     * コンベア上のItemEntityを吸収する。
     *
     * <p>
     * ベルト内部にアイテムが存在しない場合のみ実行される。
     * 吸収後はItemEntityを削除し、
     * 内部Inventoryへ格納する。
     * </p>
     */
    private void pickupItemEntity() {

        if(level == null)
            return;

        if(!getStack(0).isEmpty())
            return;
        AABB area = new AABB(worldPosition)
                .move(0,0.8,0)
                .inflate(0.25,0.1,0.25);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);
        if(items.isEmpty())
            return;
        ItemEntity entity = items.get(0);
        ItemStack source = entity.getItem();
        ItemStack insert = source.copy();
        insert.setCount(1);
        ItemStack remain = InventoryTransferHelper.insertItem(getInventory(), insert);
        if(remain.isEmpty()){
            source.shrink(1);
            if(source.isEmpty()){
                entity.discard();
            }
        }
    }

    private void transferToFront() {

        if (level == null)
            return;
        if (getStack(0).isEmpty())
            return;
        BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(getDirection()));
        if (blockEntity == null)
            return;
        LazyOptional<IItemHandler> capability = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, getDirection().getOpposite());
        if (!capability.isPresent())
            return;
        capability.ifPresent(handler -> InventoryTransferHelper.transfer(getInventory(), handler, 1));
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
    public float getBeltOffset() {
        return beltOffset;
    }
    public float getSpeed() {
        return speed;
    }
    @Override
    public double getEnergyDemand() {
        return 200;
    }
    @Override
    public boolean canWork() {
        return powered;
    }
    public boolean isPowered() {
        return powered;
    }
    public ConveyorShape getShape() {
        return shape;
    }
    public float getItemOffset(float partialTick){
        return itemOffset + speed * partialTick;
    }
    public boolean hasItem(){
        return !getStack(0).isEmpty();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag){
        super.saveAdditional(tag);
        tag.putFloat("Speed", speed);
    }
    @Override
    public void load(@NotNull CompoundTag tag){
        super.load(tag);
        speed = tag.getFloat("Speed");
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
    @Override
    public void setPowered(boolean value) {
        powered = value;
        // setChanged();
    }
}