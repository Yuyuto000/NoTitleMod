package com.yuyuto.no_title_mod.industry.crusher;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CrusherRecipeManager {

    private static final List<CrusherRecipe> RECIPES =
            new ArrayList<>();

    public static void addRecipe(CrusherRecipe recipe){
        RECIPES.add(recipe);
    }

    public static CrusherRecipe find(ItemStack input){

        for(CrusherRecipe recipe : RECIPES){
            if(recipe.matches(input)){
                return recipe;
            }
        }
        return null;
    }
}
