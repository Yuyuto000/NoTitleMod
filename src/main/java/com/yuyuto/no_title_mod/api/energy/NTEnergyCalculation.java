package com.yuyuto.no_title_mod.api.energy;

/**
 * 電気系の計算を行うメソッドクラス。
 * 将来的に使用。現状のシステムではリアルな電気計算を行わない。
 * 行う内容は
 * - 発電圧計算
 */

public class NTEnergyCalculation {
    public static double calculateGeneratedVoltage(double mechanicalPower, long gameTime) {
        // 発電するVoltageの量をsin波で動的化。
        double wave = Math.sin(gameTime * 0.05);
        return  mechanicalPower * (1.0 + wave * 0.1);
    }
}
