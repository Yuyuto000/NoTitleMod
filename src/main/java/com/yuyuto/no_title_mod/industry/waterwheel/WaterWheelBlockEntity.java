package com.yuyuto.no_title_mod.industry.waterwheel;

import com.yuyuto.no_title_mod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WaterWheelBlockEntity extends BlockEntity {

    public WaterWheelBlockEntity(BlockPos pos, BlockState state){
        super(ModBlockEntities.WATER_WHEEL.get(), pos, state);
    }
}
