package com.yuyuto.no_title_mod.registry;

import com.yuyuto.no_title_mod.NoTitleMod;
import com.yuyuto.no_title_mod.industry.crusher_hammer.CrusherHammerRecipeSerializer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, NoTitleMod.MODID);

    public static final RegistryObject<RecipeSerializer<?>> CRUSHER_HAMMER =
            SERIALIZERS.register("crusher_hammer", CrusherHammerRecipeSerializer::new);

    public static void register(IEventBus bus){
        SERIALIZERS.register(bus);
    }
}