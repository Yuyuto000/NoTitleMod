package com.yuyuto.no_title_mod.industry.material;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class FuelMaterials {
    private static final Map<Item, Integer> FUELS = Map.of(
            Items.COAL, 600,
            Items.CHARCOAL, 600,
            Items.COAL_BLOCK, 600
    );

    public static int getBurnTime(@NotNull ItemStack stack){
        return FUELS.getOrDefault(stack.getItem(), 0);
    }
}
