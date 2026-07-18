package com.yuyuto.no_title_mod.registry;

import com.yuyuto.no_title_mod.industry.crusher_hammer.CrusherHammerRecipe;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public class ModRecipeSerializers {

    public static final SimpleCraftingRecipeSerializer<CrusherHammerRecipe> CRUSHER_HAMMER =
            new SimpleCraftingRecipeSerializer<>(CrusherHammerRecipe::new);
}