package com.yuyuto.no_title_mod.api.energy;

import net.minecraft.core.BlockPos;

/**
 * Node間を送信する際に使う通信規格。
 * @param energy  エネルギー量
 * @param source 発電元
 * @param time    発生時刻(GameTime)
 */

public record NTEnergyPacket(double energy, BlockPos source, long time) {
}
