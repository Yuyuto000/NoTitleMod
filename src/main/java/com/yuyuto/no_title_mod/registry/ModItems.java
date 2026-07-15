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
     * このクラスはメインクラスで呼び出される。
     */

    //============================この範囲は弄らない=============================
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, NoTitleMod.MODID);
    //========================================================================
    /**
     * 基本素材登録メソッド。
     * itemIdにはString型でIDを記述。
     * @param itemId　ItemのID。
     * @return スタック数64に設定済みのアイテム登録メソッド
     */
    private static RegistryObject<Item> registryNormalMaterial(String itemId){
        return ITEMS.register(itemId, () ->
                new Item(new Item.Properties().stacksTo(64)));
    }

    // 工業アイテム登録
    public static final RegistryObject<Item> RAW_ZINC = registryNormalMaterial("raw_zinc");
    public static final RegistryObject<Item> RAW_TIN = registryNormalMaterial("raw_tin");
    public static final RegistryObject<Item> RAW_MAGNETITE = registryNormalMaterial("raw_magnetite");
    public static final RegistryObject<Item> RAW_LEAD = registryNormalMaterial("raw_lead");
    public static final RegistryObject<Item> RAW_SILVER = registryNormalMaterial("raw_silver");
    public static final RegistryObject<Item> RAW_ALUMINUM = registryNormalMaterial("raw_aluminum");
    public static final RegistryObject<Item> RAW_TITANIUM = registryNormalMaterial("raw_titanium");
    public static final RegistryObject<Item> RAW_NICKEL = registryNormalMaterial("raw_nickel");
    public static final RegistryObject<Item> RAW_CHROMIUM = registryNormalMaterial("raw_chromium");
    public static final RegistryObject<Item> RAW_COBALT = registryNormalMaterial("raw_cobalt");
    public static final RegistryObject<Item> RAW_MAGNESIUM = registryNormalMaterial("raw_magnesium");
    public static final RegistryObject<Item> RAW_LITHIUM = registryNormalMaterial("raw_lithium");
    public static final RegistryObject<Item> RAW_TUNGSTEN = registryNormalMaterial("raw_tungsten");
    public static final RegistryObject<Item> COPPER_DUST = registryNormalMaterial("copper_dust");
    public static final RegistryObject<Item> IRON_DUST = registryNormalMaterial("iron_dust");
    public static final RegistryObject<Item> ZINC_DUST = registryNormalMaterial("zinc_dust");
    public static final RegistryObject<Item> TIN_DUST = registryNormalMaterial("tin_dust");
    public static final RegistryObject<Item> RAW_RUBBER = registryNormalMaterial("raw_rubber");
    public static final RegistryObject<Item> RUBBER = registryNormalMaterial("rubber");
    public static final RegistryObject<Item> LIMESTONE = registryNormalMaterial("limestone");
    public static final RegistryObject<Item> MAGNETITE = registryNormalMaterial("magnetite");

    //============================この範囲は弄らない=============================
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
    //========================================================================

}
