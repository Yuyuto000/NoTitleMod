package com.yuyuto.no_title_mod.industry.energy_cable;

import com.yuyuto.no_title_mod.api.energy.INTEnergyNodeManagements;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;

public class EnergyCableBlock extends Block {
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

    private boolean canConnect(@NotNull LevelAccessor level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof INTEnergyNodeManagements;
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return defaultBlockState()
                .setValue(NORTH, canConnect(level, pos.north()))
                .setValue(SOUTH, canConnect(level, pos.south()))
                .setValue(EAST,  canConnect(level, pos.east()))
                .setValue(WEST,  canConnect(level, pos.west()))
                .setValue(UP,    canConnect(level, pos.above()))
                .setValue(DOWN,  canConnect(level, pos.below()));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {

        return switch (direction) {
            case NORTH -> state.setValue(NORTH, canConnect(level, neighborPos));
            case SOUTH -> state.setValue(SOUTH, canConnect(level, neighborPos));
            case EAST  -> state.setValue(EAST,  canConnect(level, neighborPos));
            case WEST  -> state.setValue(WEST,  canConnect(level, neighborPos));
            case UP    -> state.setValue(UP,    canConnect(level, neighborPos));
            case DOWN  -> state.setValue(DOWN,  canConnect(level, neighborPos));
        };
    }
}
