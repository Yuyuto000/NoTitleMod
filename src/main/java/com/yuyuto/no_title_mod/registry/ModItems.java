package com.yuyuto.no_title_mod.registry;

import com.yuyuto.no_title_mod.NoTitleMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    /**
     * アイテム登録クラス
     * 全アイテムの登録処理をここで行う
     * このクラスはメインクラスで利用される。
     */
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, NoTitleMod.MODID);

    public static final RegistryObject<Item> COPPER_DUST =
            ITEMS.register("copper_dust", () ->
                    new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> IRON_DUST =
            ITEMS.register("iron_dust", () ->
                    new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> ZINC_DUST =
            ITEMS.register("zinc_dust", () ->
                    new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> TIN_DUST =
            ITEMS.register("tin_dust", () ->
                    new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> RAW_RUBBER =
            ITEMS.register("raw_rubber", () ->
                    new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> RUBBER =
            ITEMS.register("rubber", () ->
                    new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> LIMESTONE =
            ITEMS.register("limestone", () ->
                    new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> MAGNETITE =
            ITEMS.register("magnetite", () ->
                    new Item(new Item.Properties().stacksTo(64)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
