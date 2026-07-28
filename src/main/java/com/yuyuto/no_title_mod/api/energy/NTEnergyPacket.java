package com.yuyuto.no_title_mod.api.energy;

import net.minecraft.core.BlockPos;

import java.util.Set;

/**
 * Node間を送信する際に使う通信規格。
 */

public record NTEnergyPacket(double energy, Set<BlockPos> visited, long time) {
    /**
     * @param energy  エネルギー量
     * @param visited 発電元
     * @param time    発生時刻(GameTime)
     */
    public NTEnergyPacket{
        visited = Set.copyOf(visited);
    }
}
