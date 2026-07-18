package com.yuyuto.no_title_mod.industry.waterwheel;

import com.yuyuto.no_title_mod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class WaterWheelBlock extends BaseEntityBlock {

    // コンストラクタ
    public WaterWheelBlock(Properties properties){
        super(properties);
    }

    // オリジナルBlockEntity生成メソッド
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state){
        return new WaterWheelBlockEntity(pos, state);
    }

    // レンダリングのためのGetter
    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state){
        return RenderShape.MODEL;
    }

    // 実際のTick処理定義メソッド
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type){
        return createTickerHelper(type, ModBlockEntities.WATER_WHEEL.get(), WaterWheelBlockEntity::tick);
    }
}
