package com.yuyuto.no_title_mod.industry.conveyor;

/**
 * コンベアの接続形状。
 *
 * <p>
 * 隣接するコンベアとの接続状態を表す。
 * RendererはこのShapeを参照して描画モデルを切り替える。
 * </p>
 */
public enum ConveyorShape {

    /**
     * 単体設置。
     */
    SINGLE,

    /**
     * 始端。
     */
    START,

    /**
     * 中間。
     */
    MIDDLE,

    /**
     * 終端。
     */
    END

}
