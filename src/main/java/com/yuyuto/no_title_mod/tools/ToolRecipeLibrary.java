package com.yuyuto.no_title_mod.tools;

import com.yuyuto.no_title_mod.registry.ModItems;
import net.minecraft.world.item.Items;

public class ToolRecipeLibrary {
    public static void register(){
        ToolRecipeManager.addRecipe(new ToolRecipe(Items.RAW_IRON, ModItems.IRON_DUST.get(), ToolTypes.CRUSHER));
        ToolRecipeManager.addRecipe(new ToolRecipe(ModItems.RAW_ALUMINUM.get(), ModItems.ALUMINUM_DUST.get(), ToolTypes.CRUSHER));
        ToolRecipeManager.addRecipe(new ToolRecipe(Items.RAW_COPPER, ModItems.COPPER_DUST.get(), ToolTypes.CRUSHER));
        ToolRecipeManager.addRecipe(new ToolRecipe(ModItems.RAW_CHROMIUM.get(), ModItems.CHROMIUM_DUST.get(), ToolTypes.CRUSHER));
        ToolRecipeManager.addRecipe(new ToolRecipe(ModItems.RAW_MAGNESIUM.get(), ModItems.MAGNESIUM_DUST.get(), ToolTypes.CRUSHER));
        ToolRecipeManager.addRecipe(new ToolRecipe(ModItems.RAW_NICKEL.get(), ModItems.NICKEL_DUST.get(), ToolTypes.CRUSHER));
        ToolRecipeManager.addRecipe(new ToolRecipe(ModItems.RAW_TIN.get(), ModItems.TIN_DUST.get(), ToolTypes.CRUSHER));
        ToolRecipeManager.addRecipe(new ToolRecipe(ModItems.RAW_ZINC.get(), ModItems.ZINC_DUST.get(), ToolTypes.CRUSHER));
    }
}
