package com.yuyuto.no_title_mod.tools;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ToolRecipeManager {
    private static final List<ToolRecipe> RECIPES =
            new ArrayList<>();

    public static void addRecipe(ToolRecipe recipe){
        RECIPES.add(recipe);
    }

    public static @Nullable ToolRecipe find(ItemStack input, ToolTypes types){

        for(ToolRecipe recipe : RECIPES){
            if(recipe.matches(input, types)){
                return recipe;
            }
        }
        return null;
    }
}
