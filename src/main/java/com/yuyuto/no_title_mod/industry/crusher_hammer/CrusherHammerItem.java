package com.yuyuto.no_title_mod.industry.crusher_hammer;

import com.yuyuto.no_title_mod.tools.ICraftingTool;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CrusherHammerItem extends Item implements ICraftingTool {
    public CrusherHammerItem(Properties properties) {
        super(properties);
    }

    @Override
    public void onCraft(ItemStack stack, Player player) {
        stack.hurtAndBreak(1, player, p ->
                p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
    }
}
