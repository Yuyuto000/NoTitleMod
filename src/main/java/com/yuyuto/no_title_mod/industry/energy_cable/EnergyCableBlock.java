package com.yuyuto.no_title_mod.industry.energy_cable;

import com.yuyuto.no_title_mod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnergyCableBlock extends BaseEntityBlock {
    private static final VoxelShape CENTER_VOXEL = Block.box(6,6,6,10,10,10);
    private static final VoxelShape NORTH_VOXEL = Block.box(6,6,0,10,10,6);
    private static final VoxelShape SOUTH_VOXEL = Block.box(6,6,10,10,10,16);
    private static final VoxelShape EAST_VOXEL = Block.box(10,6,6,16,10,10);
    private static final VoxelShape WEST_VOXEL = Block.box(0,6,6,6,10,10);
    private static final VoxelShape UP_VOXEL = Block.box(6,10,6,10,16,10);
    private static final VoxelShape DOWN_VOXEL = Block.box(6,0,6,10,6,10);

    public EnergyCableBlock(Properties properties) {
        super(properties);
        registerDefaultState(
                stateDefinition.any()
                        .setValue(NORTH, false)
                        .setValue(SOUTH, false)
                        .setValue(EAST, false)
                        .setValue(WEST, false)
                        .setValue(UP, false)
                        .setValue(DOWN, false)
        );
    }

    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty EAST  = BlockStateProperties.EAST;
    public static final BooleanProperty WEST  = BlockStateProperties.WEST;
    public static final BooleanProperty UP    = BlockStateProperties.UP;
    public static final BooleanProperty DOWN  = BlockStateProperties.DOWN;

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    private boolean canConnectEnergy(@NotNull LevelAccessor level, BlockPos pos){

        BlockState state = level.getBlockState(pos);
        // ケーブル同士
        if(state.getBlock() instanceof EnergyCableBlock){
            return true;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity == null){
            return false;
        }
        // FE対応機械
        return blockEntity.getCapability(ForgeCapabilities.ENERGY).isPresent();
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return defaultBlockState()
                .setValue(NORTH, canConnectEnergy(level, pos.north()))
                .setValue(SOUTH, canConnectEnergy(level, pos.south()))
                .setValue(EAST,  canConnectEnergy(level, pos.east()))
                .setValue(WEST,  canConnectEnergy(level, pos.west()))
                .setValue(UP,    canConnectEnergy(level, pos.above()))
                .setValue(DOWN,  canConnectEnergy(level, pos.below()));
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type){
        return createTickerHelper(type, ModBlockEntities.ENERGY_CABLE.get(), (level1, pos, state1, entity) ->{
            if (!level1.isClientSide){
                EnergyCableBlockEntity.tick(level, pos, state, entity);
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {

        return switch (direction) {
            case NORTH -> state.setValue(NORTH, canConnectEnergy(level, neighborPos));
            case SOUTH -> state.setValue(SOUTH, canConnectEnergy(level, neighborPos));
            case EAST  -> state.setValue(EAST,  canConnectEnergy(level, neighborPos));
            case WEST  -> state.setValue(WEST,  canConnectEnergy(level, neighborPos));
            case UP    -> state.setValue(UP,    canConnectEnergy(level, neighborPos));
            case DOWN  -> state.setValue(DOWN,  canConnectEnergy(level, neighborPos));
        };
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new EnergyCableBlockEntity(pos, state);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {

        VoxelShape shape = CENTER_VOXEL;

        if (state.getValue(NORTH)) {
            shape = Shapes.or(shape, NORTH_VOXEL);
        }
        if (state.getValue(SOUTH)) {
            shape = Shapes.or(shape, SOUTH_VOXEL);
        }
        if (state.getValue(EAST)) {
            shape = Shapes.or(shape, EAST_VOXEL);
        }
        if (state.getValue(WEST)) {
            shape = Shapes.or(shape, WEST_VOXEL);
        }
        if (state.getValue(UP)) {
            shape = Shapes.or(shape, UP_VOXEL);
        }
        if (state.getValue(DOWN)) {
            shape = Shapes.or(shape, DOWN_VOXEL);
        }

        return shape;
    }
}
