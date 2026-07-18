package com.yuyuto.no_title_mod;

import com.mojang.logging.LogUtils;
import com.yuyuto.no_title_mod.event.ModCraftingEvent;
import com.yuyuto.no_title_mod.registry.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

@Mod(NoTitleMod.MODID)
public class NoTitleMod {

    public static final String MODID = "notitlemod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NoTitleMod() {
        GeckoLib.initialize();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.register(modEventBus);
        NoTitleMod.LOGGER.info("[YUYUTO_TECHNOLOGIES][INFO] Item Loaded successful");
        ModBlocks.register(modEventBus);
        NoTitleMod.LOGGER.info("[YUYUTO_TECHNOLOGIES][INFO] Block Loaded successful");
        ModBlockEntities.register(modEventBus);
        NoTitleMod.LOGGER.info("[YUYUTO_TECHNOLOGIES][INFO] BlockEntity Loaded successful");
        ModCreativeTabs.register(modEventBus);
        NoTitleMod.LOGGER.info("[YUYUTO_TECHNOLOGIES][INFO] CreativeTab Loaded successful");
        ModRecipeSerializers.register(modEventBus);
        NoTitleMod.LOGGER.info("[YUYUTO_TECHNOLOGIES][INFO] RecipeSerializer Loaded successful");
        ModRecipeTypes.register(modEventBus);
        NoTitleMod.LOGGER.info("[YUYUTO_TECHNOLOGIES][INFO] RecipeType Loaded successful");
        MinecraftForge.EVENT_BUS.register(ModCraftingEvent.class);
        NoTitleMod.LOGGER.info("[YUYUTO_TECHNOLOGIES][INFO] NoTitleMod(NTMod) Loaded successful");
    }

}