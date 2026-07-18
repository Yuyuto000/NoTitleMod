package com.yuyuto.no_title_mod.industry.waterwheel;

import com.yuyuto.no_title_mod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WaterWheelBlockEntity extends BlockEntity implements GeoBlockEntity {

    //　必要フィールド
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // コンストラクタ
    public WaterWheelBlockEntity(BlockPos pos, BlockState state){
        super(ModBlockEntities.WATER_WHEEL.get(), pos, state);
    }

    // ステータス
    private float rotation = 0.0f;
    private float rotationSpeed = 0.0f;

    //Getter(Rendererが読むため)
    public float getRotation() {
        return rotation;
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    // GeckoLibが必要としてるメソッドを実装
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache(){
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    //Tickメソッド(ここがロジック部分)
    public static void tick(Level level, BlockPos pos, BlockState state, WaterWheelBlockEntity entity){
        entity.rotation += entity.rotationSpeed;
    }
}
