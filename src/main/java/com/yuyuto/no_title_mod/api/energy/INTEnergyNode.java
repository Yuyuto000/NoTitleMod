package com.yuyuto.no_title_mod.api.energy;

import net.minecraft.core.BlockPos;

/**
 *Nodeの要求されるメソッドを記述。
 */
public interface INTEnergyNode {
    NTEnergyNodeType getNodeType();
    NTEnergyPacket getPacket();
    BlockPos getPos();
    void receivePacket(NTEnergyPacket packet);
}
