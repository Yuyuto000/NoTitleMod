package com.yuyuto.no_title_mod.industry.energy_cable;

import com.yuyuto.no_title_mod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

public class EnergyCableBlockEntity extends BlockEntity {


    public EnergyCableBlockEntity(BlockPos pos, BlockState state){
        super(ModBlockEntities.ENERGY_CABLE.get(), pos, state);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction side){
        return super.getCapability(capability, side);

    }

}