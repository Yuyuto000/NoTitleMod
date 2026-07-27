package com.yuyuto.no_title_mod.api.energy;

/**
 * NodeのTypeを識別する列挙クラス。
 * <p>
 * GENERATOR:発電機系Nodeであることを示す。
 * CABLE : 輸送系Nodeであることを示す。
 * CONSUMER:電気を消費するNodeであることを示す。
 */

public enum NTEnergyNodeType {
    GENERATOR,
    CABLE,
    CONSUMER
}
