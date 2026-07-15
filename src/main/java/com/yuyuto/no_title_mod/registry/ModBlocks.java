package com.yuyuto.no_title_mod.registry;

import com.yuyuto.no_title_mod.NoTitleMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {

    /**
     * ブロック登録クラス
     * 全ブロックの登録処理をここで行う
     * このクラスはメインクラスで呼び出される
     */

    //========================この範囲は弄らない=============================
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, NoTitleMod.MODID);

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block){
        RegistryObject<T> registeredBlock = BLOCKS.register(name, block);
        registerBlockItem(name, registeredBlock);
        return registeredBlock;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block){
        ModItems.ITEMS.register(name,() -> new BlockItem(block.get(), new Item.Properties().stacksTo(64)));
    }
    //===================================================================

    /**
     * 鉱石ブロック登録ヘルパー関数
     * @param blockId ブロックの一意のID
     * @return Stone型、ツール必須使用に設定済みのブロック登録コード
     */
    private static RegistryObject<Block> registryOre(String blockId){
        return registerBlock(blockId, () ->
                new Block(
                        BlockBehaviour.Properties.of()
                                .mapColor(MapColor.STONE)
                                .strength(3.0f, 3.0f)
                                .requiresCorrectToolForDrops()
                                .sound(SoundType.STONE)
                ));
    }

    //鉱石登録
    public static final RegistryObject<Block> ZINC_ORE = registryOre("zinc_ore");
    public static final RegistryObject<Block> TIN_ORE = registryOre("tin_ore");
    public static final RegistryObject<Block> MAGNETITE_ORE = registryOre("magnetite_ore");
    public static final RegistryObject<Block> LEAD_ORE = registryOre("lead_ore");
    public static final RegistryObject<Block> SILVER_ORE = registryOre("silver_ore");
    public static final RegistryObject<Block> ALUMINUM_ORE = registryOre("aluminum_ore");
    public static final RegistryObject<Block> TITANIUM_ORE = registryOre("titanium_ore");
    public static final RegistryObject<Block> NICKEL_ORE = registryOre("nickel_ore");
    public static final RegistryObject<Block> CHROMIUM_ORE = registryOre("chromium_ore");
    public static final RegistryObject<Block> COBALT_ORE = registryOre("cobalt_ore");
    public static final RegistryObject<Block> MAGNESIUM_ORE = registryOre("magnesium_ore");
    public static final RegistryObject<Block> LITHIUM_ORE = registryOre("lithium_ore");
    public static final RegistryObject<Block> TUNGSTEN_ORE = registryOre("tungsten_ore");

    //===========================弄らない==============================
    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
    //===============================================================

}
