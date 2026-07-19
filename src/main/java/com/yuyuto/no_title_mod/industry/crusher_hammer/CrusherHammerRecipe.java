package com.yuyuto.no_title_mod.industry.crusher_hammer;

import com.yuyuto.no_title_mod.registry.ModItems;
import com.yuyuto.no_title_mod.registry.ModRecipeSerializers;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import static com.yuyuto.no_title_mod.NoTitleMod.LOGGER;

public class CrusherHammerRecipe implements CraftingRecipe {

    private final ResourceLocation id;
    private final Ingredient input;
    private final ItemStack result;

    public CrusherHammerRecipe(ResourceLocation id, Ingredient input, ItemStack result) {
        this.id = id;
        this.input = input;
        this.result = result;
        LOGGER.info("CrusherHammer initialized");
    }

    public Ingredient getInput() {
        return input;
    }

    public ItemStack getResult() {
        return result;
    }

    @Override
    public boolean matches(@NotNull CraftingContainer container, @NotNull Level level) {

        int inputCount = 0;
        int hammerCount = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {

            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;
            if (input.test(stack)) {
                inputCount++;
                continue;
            }
            if (stack.is(ModItems.CRUSHER_HAMMER.get())) {
                hammerCount++;
                continue;
            }
            // それ以外のアイテムがあれば失敗
            return false;
        }
        LOGGER.info("matches");
        return inputCount == 1 && hammerCount == 1;
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingContainer container) {

        NonNullList<ItemStack> remaining = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        for(int i = 0; i < container.getContainerSize(); i++){
            ItemStack stack = container.getItem(i);
            if(stack.is(ModItems.CRUSHER_HAMMER.get())){
                ItemStack copy = stack.copy();
                copy.hurt(1, RandomSource.create(), null);
                if(copy.getDamageValue() >= copy.getMaxDamage()){
                    remaining.set(i, ItemStack.EMPTY);
                }
                else{
                    remaining.set(i, copy);
                }
            }
        }
        return remaining;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer container, @NotNull RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public @NotNull ResourceLocation getId(){
        return id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer(){
        return ModRecipeSerializers.CRUSHER_HAMMER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType(){
        return RecipeType.CRAFTING;
    }

    @Override
    public @NotNull CraftingBookCategory category(){
        return CraftingBookCategory.EQUIPMENT;
    }
}