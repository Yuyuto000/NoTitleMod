package com.yuyuto.no_title_mod.event;

import com.yuyuto.no_title_mod.tools.ICraftingTool;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModCraftingEvent {
    @SubscribeEvent
    public static void onCraft(PlayerEvent.ItemCraftedEvent event) {
        for (int i = 0; i < event.getInventory().getContainerSize(); i++) {
            ItemStack stack = event.getInventory().getItem(i);
            if (stack.getItem() instanceof ICraftingTool tool) {
                tool.onCraft(stack, event.getEntity());
            }
        }
    }
}
