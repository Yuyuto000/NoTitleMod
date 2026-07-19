package com.yuyuto.no_title_mod.api.energy;

import net.minecraft.core.BlockPos;

public interface INTEnergyNodeManagements {
    void connection(NTEnergyNetwork network);
    void disconnect();
    NTEnergyNode getNode();
    BlockPos getNodePosition();
    void updateEnergyNode();
}
