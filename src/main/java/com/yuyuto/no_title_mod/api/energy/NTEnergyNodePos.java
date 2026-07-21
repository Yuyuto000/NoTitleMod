package com.yuyuto.no_title_mod.api.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record NTEnergyNodePos(ResourceKey<Level> dimension, BlockPos pos) {
}
