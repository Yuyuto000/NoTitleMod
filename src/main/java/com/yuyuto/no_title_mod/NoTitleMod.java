package com.yuyuto.no_title_mod;

import com.yuyuto.no_title_mod.event.ModCraftingEvent;
import com.yuyuto.no_title_mod.registry.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.geckolib.GeckoLib;

@Mod(NoTitleMod.MODID)
public class NoTitleMod {

    public static final String MODID = "notitlemod";

    public NoTitleMod() {
        GeckoLib.initialize();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.register(modEventBus);
        System.out.println("[YUYUTO_TECHNOLOGIES][DEBUG] Item Loaded successful");
        ModBlocks.register(modEventBus);
        System.out.println("[YUYUTO_TECHNOLOGIES][DEBUG] Block Loaded successful");
        ModBlockEntities.register(modEventBus);
        System.out.println("[YUYUTO_TECHNOLOGIES][DEBUG] BlockEntity Loaded successful");
        ModCreativeTabs.register(modEventBus);
        System.out.println("[YUYUTO_TECHNOLOGIES][DEBUG] CreativeTab Loaded successful");
        ModRecipeSerializers.register(modEventBus);
        System.out.println("[YUYUTO_TECHNOLOGIES][DEBUG] RecipeSerializer Loaded successful");
        ModRecipeTypes.register(modEventBus);
        System.out.println("[YUYUTO_TECHNOLOGIES][DEBUG] RecipeType Loaded successful");
        MinecraftForge.EVENT_BUS.register(ModCraftingEvent.class);
        System.out.println("[YUYUTO_TECHNOLOGIES][DEBUG] NoTitleMod(NTMod) Loaded successful");
    }

}