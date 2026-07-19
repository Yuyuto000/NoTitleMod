package com.yuyuto.no_title_mod.industry.crusher_hammer;

import com.google.gson.JsonObject;
import com.yuyuto.no_title_mod.NoTitleMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrusherHammerRecipeSerializer implements RecipeSerializer<CrusherHammerRecipe> {

    @Override
    public @NotNull CrusherHammerRecipe fromJson(@NotNull ResourceLocation id,
                                                 @NotNull JsonObject json) {
        NoTitleMod.LOGGER.info("Loading Crusher Recipe: {}", id);

        Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));

        ItemStack result = net.minecraft.world.item.crafting.ShapedRecipe
                .itemStackFromJson(json.getAsJsonObject("result"));

        NoTitleMod.LOGGER.info("Ingredient={} Result={}", ingredient, result);
        return new CrusherHammerRecipe(id, ingredient, result);
    }

    @Override
    public @Nullable CrusherHammerRecipe fromNetwork(@NotNull ResourceLocation id,
                                                     @NotNull FriendlyByteBuf buf) {

        Ingredient ingredient = Ingredient.fromNetwork(buf);
        ItemStack result = buf.readItem();

        return new CrusherHammerRecipe(id, ingredient, result);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buf,
                          @NotNull CrusherHammerRecipe recipe) {

        recipe.getInput().toNetwork(buf);
        buf.writeItem(recipe.getResult());
    }
}
