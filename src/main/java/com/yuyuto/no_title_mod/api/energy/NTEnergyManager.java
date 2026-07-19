package com.yuyuto.no_title_mod.api.energy;

/**
 * NTEnergyを持っているクラスの電気系計算を行うクラス。
 * 電気系計算以外は行わない
 * 主な計算(上から)
 * ==============
 * 電力計算
 *  P = V * I
 * ==============
 * 電圧計算
 * V = I * R
 * ==============
 * 電流計算
 * I = V / R
 * ==============
 */
public class NTEnergyManager {

    public static double calculatePower(double voltage, double current){
        return voltage * current;
    }

    public static double calculateVoltage(double current, double resistance){
        return current * resistance;
    }

    public static double calculateCurrent(double voltage, double resistance){
        if (resistance == 0) return 0;
        return voltage / resistance;
    }

    //機械が動ける電力かを確認する
    public static boolean canOperate(double availablePower, double requiredPower){
        return availablePower >= requiredPower;
    }

    //消費後の残電力計算
    public static double decreasePower(double availablePower, double consumePower){
        double result = availablePower - consumePower;
        return Math.max(result, 0);
    }
}
