package com.yuyuto.no_title_mod.industry.crusher;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record CrusherRecipe(Item input, Item output) {
    public boolean matches(@NotNull ItemStack stack){
        return  stack.is(input);
    }

    @Contract(" -> new")
    public @NotNull ItemStack createResult(){
        return new ItemStack(output);
    }
}