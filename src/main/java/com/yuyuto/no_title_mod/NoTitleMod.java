package com.yuyuto.no_title_mod;

import com.yuyuto.no_title_mod.event.ModCraftingEvent;
import com.yuyuto.no_title_mod.industry.crusher.CrusherRecipeLibrary;
import com.yuyuto.no_title_mod.registry.ModBlockEntities;
import com.yuyuto.no_title_mod.registry.ModBlocks;
import com.yuyuto.no_title_mod.registry.ModCreativeTabs;
import com.yuyuto.no_title_mod.registry.ModItems;
import com.yuyuto.no_title_mod.tools.ToolRecipeLibrary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.geckolib.GeckoLib;

@Mod(NoTitleMod.MODID)
public class NoTitleMod {

    public static final String MODID = "notitlemod";

    public NoTitleMod() {
        GeckoLib.initialize();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(ModCraftingEvent.class);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CrusherRecipeLibrary.register();
            ToolRecipeLibrary.register();
        });
    }
}