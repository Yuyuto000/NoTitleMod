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
     * 基本素材登録ヘルパーメソッド。
     * itemIdにはString型でIDを記述。
     * @param itemId　ItemのID。
     * @return スタック数64に設定済みのアイテム登録メソッド
     */
    private static RegistryObject<Item> registryNormalMaterial(String itemId){
        return ITEMS.register(itemId, () ->
                new Item(new Item.Properties().stacksTo(64)));
    }

    /**
     * ツール登録ヘルパーメソッド。
     * @param toolId 登録するItemのIDを記述
     * @param durability 耐久値
     * @return スタック不可能の任意の耐久値を持つアイテムの登録メソッド
     */
    private static RegistryObject<Item> registryNormalTool(String toolId, int durability){
        return ITEMS.register(toolId, () ->
                new Item(new Item.Properties().stacksTo(1).durability(durability)));
    }

    // 工業アイテム登録
    public static final RegistryObject<Item> RAW_ZINC = registryNormalMaterial("raw_zinc");
    public static final RegistryObject<Item> RAW_TIN = registryNormalMaterial("raw_tin");
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

    public static final RegistryObject<Item> ZINC_INGOT = registryNormalMaterial("zinc_ingot");
    public static final RegistryObject<Item> TIN_INGOT = registryNormalMaterial("tin_ingot");
    public static final RegistryObject<Item> LEAD_INGOT = registryNormalMaterial("lead_ingot");
    public static final RegistryObject<Item> SILVER_INGOT = registryNormalMaterial("silver_ingot");
    public static final RegistryObject<Item> ALUMINUM_INGOT = registryNormalMaterial("aluminum_ingot");
    public static final RegistryObject<Item> TITANIUM_INGOT = registryNormalMaterial("titanium_ingot");
    public static final RegistryObject<Item> NICKEL_INGOT = registryNormalMaterial("nickel_ingot");
    public static final RegistryObject<Item> CHROMIUM_INGOT = registryNormalMaterial("chromium_ingot");
    public static final RegistryObject<Item> COBALT_INGOT = registryNormalMaterial("cobalt_ingot");
    public static final RegistryObject<Item> MAGNESIUM_INGOT = registryNormalMaterial("magnesium_ingot");
    public static final RegistryObject<Item> LITHIUM_INGOT = registryNormalMaterial("lithium_ingot");
    public static final RegistryObject<Item> TUNGSTEN_INGOT = registryNormalMaterial("tungsten_ingot");

    public static final RegistryObject<Item> COPPER_DUST = registryNormalMaterial("copper_dust");
    public static final RegistryObject<Item> IRON_DUST = registryNormalMaterial("iron_dust");
    public static final RegistryObject<Item> ALUMINUM_DUST = registryNormalMaterial("aluminum_dust");
    public static final RegistryObject<Item> CHROMIUM_DUST = registryNormalMaterial("chromium_dust");
    public static final RegistryObject<Item> LEAD_DUST = registryNormalMaterial("lead_dust");
    public static final RegistryObject<Item> MAGNESIUM_DUST = registryNormalMaterial("magnesium_dust");
    public static final RegistryObject<Item> NICKEL_DUST = registryNormalMaterial("nickel_dust");
    public static final RegistryObject<Item> TIN_DUST = registryNormalMaterial("tin_dust");
    public static final RegistryObject<Item> ZINC_DUST = registryNormalMaterial("zinc_dust");

    public static final RegistryObject<Item> RAW_RUBBER = registryNormalMaterial("raw_rubber");
    public static final RegistryObject<Item> RUBBER = registryNormalMaterial("rubber");
    public static final RegistryObject<Item> LIMESTONE = registryNormalMaterial("limestone");
    public static final RegistryObject<Item> MAGNETITE = registryNormalMaterial("magnetite");
    //工業ツール系
    public static final RegistryObject<Item> CRUSHER_HAMMER = registryNormalTool("crusher_hammer", 50);
    //魔術系登録
    public static final RegistryObject<Item> RAW_MAGICANIUM = registryNormalMaterial("raw_magicanium");
    public static final RegistryObject<Item> MAGICANIUM_INGOT = registryNormalMaterial("magicanium_ingot");

    //============================この範囲は弄らない=============================
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
    //========================================================================

}
