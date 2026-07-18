package com.yuyuto.no_title_mod.tools;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record ToolRecipe(Item input, Item output, ToolTypes toolTypes) {

    public boolean matches(@NotNull ItemStack stack, ToolTypes types){
        return types == toolTypes && stack.is(input);
    }

    @Contract(" -> new")
    public @NotNull ItemStack createResult(){
        return new ItemStack(output);
    }
}
