package com.yuyuto.no_title_mod.industry.energy_genertator;

import com.lowdragmc.lowdraglib.gui.factory.BlockEntityUIFactory;
import com.yuyuto.no_title_mod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnergyGeneratorBlock extends BaseEntityBlock {
    public EnergyGeneratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new  EnergyGeneratorBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type){
        return createTickerHelper(type, ModBlockEntities.ENERGY_GENERATOR.get(), (level1, pos, state1, entity) -> {
            if (!level1.isClientSide) {
                EnergyGeneratorBlockEntity.tick(level, pos, state, entity);
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit){
        if(level.isClientSide){
            return InteractionResult.SUCCESS;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof EnergyGeneratorBlockEntity generator){
            if(player instanceof ServerPlayer serverPlayer){
                BlockEntityUIFactory.INSTANCE.openUI(generator, serverPlayer);
            }
        }
        return InteractionResult.CONSUME;
    }
}
