package com.yuyuto.no_title_mod.client;

import com.yuyuto.no_title_mod.NoTitleMod;
import com.yuyuto.no_title_mod.client.renderer.ConveyorRenderer;
import com.yuyuto.no_title_mod.registry.ModBlockEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

/**
 * クライアントにレンダリング登録する場所。
 * 独自アニメーションを作る際は必ずここで登録処理をすること。
 */
@Mod.EventBusSubscriber(modid = NoTitleMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.@NotNull RegisterRenderers event){
        event.registerBlockEntityRenderer(ModBlockEntities.CONVEYOR.get(), ConveyorRenderer::new);
    }
}
