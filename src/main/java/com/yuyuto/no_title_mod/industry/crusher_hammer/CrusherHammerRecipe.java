package com.yuyuto.no_title_mod.industry.crusher_hammer;

import com.yuyuto.no_title_mod.registry.ModItems;
import com.yuyuto.no_title_mod.registry.ModRecipeSerializers;
import com.yuyuto.no_title_mod.registry.ModRecipeTypes;
import com.yuyuto.no_title_mod.tools.ToolRecipe;
import com.yuyuto.no_title_mod.tools.ToolRecipeManager;
import com.yuyuto.no_title_mod.tools.ToolTypes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CrusherHammerRecipe implements CraftingRecipe {

    private final ResourceLocation id;

    public CrusherHammerRecipe(ResourceLocation id, CraftingBookCategory craftingBookCategory) {
        this.id = id;

    }

    @Override
    public boolean matches(@NotNull CraftingContainer container, @NotNull Level level){

        boolean hasInput = false;
        boolean hasTool = false;

        for(int i = 0; i < container.getContainerSize(); i++){

            ItemStack stack = container.getItem(i);
            ToolRecipe recipe = ToolRecipeManager.find(stack, ToolTypes.CRUSHER);
            if (recipe != null) {
                hasInput = true;
            }

            if(stack.is(ModItems.CRUSHER_HAMMER.get())){
                hasTool = true;
            }
        }

        return hasInput && hasTool;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer container, @NotNull RegistryAccess registryAccess) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            ToolRecipe recipe = ToolRecipeManager.find(stack, ToolTypes.CRUSHER);
            if (recipe != null) {
                return recipe.createResult();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess){
        return new ItemStack(ModItems.IRON_DUST.get());
    }

    @Override
    public @NotNull ResourceLocation getId(){
        return id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer(){
        return ModRecipeSerializers.CRUSHER_HAMMER;
    }

    @Override
    public @NotNull RecipeType<?> getType(){
        return ModRecipeTypes.CRUSHER_HAMMER;
    }

    @Override
    public @NotNull CraftingBookCategory category(){
        return CraftingBookCategory.EQUIPMENT;
    }
}