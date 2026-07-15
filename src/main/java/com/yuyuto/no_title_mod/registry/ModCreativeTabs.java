package com.yuyuto.no_title_mod.registry;

import com.yuyuto.no_title_mod.NoTitleMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NoTitleMod.MODID);

    public static final RegistryObject <CreativeModeTab> NO_TITLE_INDUSTRY_TAB =
            CREATIVE_MODE_TAB.register("no_title_industry_tab", () ->
                    CreativeModeTab.builder()
                            .title(Component.translatable("creativetab.no_title_industry_tab"))
                            .icon(() -> new ItemStack(ModItems.COPPER_DUST.get()))
                            .displayItems((parameters, output) -> {
                                ModItems.ITEMS.getEntries().forEach(item ->
                                        output.accept(item.get())
                                );
                            })
                            .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
