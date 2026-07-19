package com.yuyuto.no_title_mod.registry;

import com.yuyuto.no_title_mod.NoTitleMod;
import com.yuyuto.no_title_mod.industry.crusher.CrusherBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    /**
     * MODのBlockのロジッククラスを登録する場所。
     * Block内部に自前ロジックを内蔵する際にゲームに登録を行う(負荷軽減のため)
     */

    //====================================ここは弄らない=====================================
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, NoTitleMod.MODID);
    //====================================================================================
    public static final RegistryObject<BlockEntityType<CrusherBlockEntity>> CRUSHER =
            BLOCK_ENTITIES.register("crusher", () ->
                    BlockEntityType.Builder.of(
                            CrusherBlockEntity::new,
                            ModBlocks.CRUSHER.get()
                    ).build(null)
            );
    //====================================ここは弄らない=====================================
    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
    //====================================================================================
}
