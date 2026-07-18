package com.yuyuto.no_title_mod.client.renderer;

import com.yuyuto.no_title_mod.client.model.WaterWheelModel;
import com.yuyuto.no_title_mod.industry.waterwheel.WaterWheelBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class WaterWheelRenderer extends GeoBlockRenderer<WaterWheelBlockEntity> {

    public WaterWheelRenderer(BlockEntityRendererProvider.Context context) {
        super(new WaterWheelModel());
    }

}