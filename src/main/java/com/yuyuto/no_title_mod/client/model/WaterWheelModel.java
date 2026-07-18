package com.yuyuto.no_title_mod.client.model;

import com.yuyuto.no_title_mod.NoTitleMod;
import com.yuyuto.no_title_mod.industry.waterwheel.WaterWheelBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * GeckoLibで水車のモデルを回す
 */

public class WaterWheelModel extends GeoModel<WaterWheelBlockEntity> {

    @Override
    public ResourceLocation getModelResource(WaterWheelBlockEntity animatable){
        return ResourceLocation.fromNamespaceAndPath(
                NoTitleMod.MODID,
                "geo/water_wheel.geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(WaterWheelBlockEntity animatable){
        return ResourceLocation.fromNamespaceAndPath(
                NoTitleMod.MODID,
                "textures/block/water_wheel.png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(WaterWheelBlockEntity animatable) {
        return null;
    }

}
