package com.yuyuto.no_title_mod.industry.crusher;

import com.yuyuto.no_title_mod.registry.ModItems;
import net.minecraft.world.item.Items;

public class CrusherRecipeLibrary {

    public static void register(){
        CrusherRecipeManager.addRecipe(new CrusherRecipe(Items.RAW_IRON, ModItems.IRON_DUST.get()));
        CrusherRecipeManager.addRecipe(new CrusherRecipe(ModItems.RAW_ALUMINUM.get(), ModItems.ALUMINUM_DUST.get()));
        CrusherRecipeManager.addRecipe(new CrusherRecipe(Items.RAW_COPPER, ModItems.COPPER_DUST.get()));
        CrusherRecipeManager.addRecipe(new CrusherRecipe(ModItems.RAW_CHROMIUM.get(), ModItems.CHROMIUM_DUST.get()));
        CrusherRecipeManager.addRecipe(new CrusherRecipe(ModItems.RAW_MAGNESIUM.get(), ModItems.MAGNESIUM_DUST.get()));
        CrusherRecipeManager.addRecipe(new CrusherRecipe(ModItems.RAW_LEAD.get(), ModItems.LEAD_DUST.get()));
        CrusherRecipeManager.addRecipe(new CrusherRecipe(ModItems.RAW_NICKEL.get(), ModItems.NICKEL_DUST.get()));
        CrusherRecipeManager.addRecipe(new CrusherRecipe(ModItems.RAW_TIN.get(), ModItems.TIN_DUST.get()));
        CrusherRecipeManager.addRecipe(new CrusherRecipe(ModItems.RAW_ZINC.get(), ModItems.ZINC_DUST.get()));
    }
}
