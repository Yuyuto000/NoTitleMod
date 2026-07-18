package com.yuyuto.no_title_mod.tools;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ICraftingTool {
    void onCraft(ItemStack stack, Player player);
}
