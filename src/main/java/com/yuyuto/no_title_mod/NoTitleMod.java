package com.yuyuto.no_title_mod;

import com.yuyuto.no_title_mod.registry.ModCreativeTabs;
import com.yuyuto.no_title_mod.registry.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(NoTitleMod.MODID)
public class NoTitleMod {

    public static final String MODID = "notitlemod";

    public NoTitleMod() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
    }
}