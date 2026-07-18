package com.yuyuto.no_title_mod.registry;

import com.yuyuto.no_title_mod.NoTitleMod;
import com.yuyuto.no_title_mod.industry.crusher_hammer.CrusherHammerRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, NoTitleMod.MODID);

    public static final RegistryObject<RecipeType<CrusherHammerRecipe>> CRUSHER_HAMMER =
            RECIPE_TYPES.register("crusher_hammer", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return NoTitleMod.MODID + ":crusher_hammer";
                }
            });

    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
    }
}
