ここでは、NoTitleModで作り上げた手順を記載し、「どのゲーム要素をどう追加したらいいのか」
「MODの具体的な作り方は何か」を事細かに記載していく。
バージョン:1.20.1-Forge:47.4.20

# 1.Item追加
ItemはMODの構築の中で切り外せないものとなっており、最重要追加事項である。\
この章では、基本的なアイテムらを追加するJava構文とJson、テクスチャの書き方など、Item関連の知見・情報を記載する\

## 1.Java構文
Itemを追加する際、Forge1.20.1ではDeferredRegistryと呼ばれるシステムを使用して登録をする。
クラス全体のコードは以下となる。
```Java
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

    // アイテム登録
    public static final RegistryObject<Item> MATERIAL= registryNormalMaterial("material");

    //============================この範囲は弄らない=============================
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
    //========================================================================

}
```

RgistryObjectのItem型定数を宣言することで、Itemを登録できる。\
基本的にItemの登録コードは以下となる。
```Java
public static final RegistryObject<Item> MATERIAL = 
    ITEMS.register("material", () ->
        new Item(new Item.Properties().stacksTo(64));
```
この時、Itemの詳細設定をしたい場合はPropertiesの後ろに設定メソッドの記述を羅列することで設定ができる。