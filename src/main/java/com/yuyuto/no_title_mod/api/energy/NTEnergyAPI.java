package com.yuyuto.no_title_mod.api.energy;

/**
 * NoTitle Energy System API
 * <p>
 * FEを拡張し、
 * 正エネルギー(FE)と負エネルギー(機械負荷)
 * を管理するための計算API
 */
public class NTEnergyAPI {

    /**
     * 発電量変動計算
     * <p>
     * sin波による発電揺らぎ
     *
     * @param baseEnergy 基本発電量
     * @param tick 現在tick
     * @param variation 変動幅
     * @return 実際の発電量
     */
    public static double calculateGeneration(
            double baseEnergy,
            long tick,
            double variation
    ) {
        double x = tick * 0.05;

        // -1.5 <= x <= 1.5 の範囲に制限
        double wave = Math.sin(x);

        wave = Math.max(-1.5, Math.min(1.5, wave));

        return baseEnergy + (baseEnergy * variation * wave);
    }


    /**
     * 機械負荷から必要FEを計算
     * <p>
     * 負エネルギー + 15～20%の損失
     *
     * @param negativeEnergy 機械要求エネルギー
     * @param efficiencyLoss 損失率(0.15～0.20)
     * @return 必要FE
     */
    public static double calculateRequiredFE(
            double negativeEnergy,
            double efficiencyLoss
    ) {
        return negativeEnergy * (1.0 + efficiencyLoss);
    }


    /**
     * 正エネルギーと負エネルギーの差分
     *
     * @param positiveEnergy FE供給量
     * @param negativeEnergy 機械負荷
     * @return 残存エネルギー
     */
    public static double calculateBalance(
            double positiveEnergy,
            double negativeEnergy
    ) {
        return positiveEnergy - negativeEnergy;
    }


    /**
     * 小数第5位まで丸める
     */
    public static double normalizeEnergy(
            double energy
    ) {
        return Math.round(energy * 100000.0) / 100000.0;
    }


    /**
     * エネルギーが足りているか確認
     */
    public static boolean canOperate(
            double positiveEnergy,
            double negativeEnergy
    ) {
        return positiveEnergy >= negativeEnergy;
    }
}
