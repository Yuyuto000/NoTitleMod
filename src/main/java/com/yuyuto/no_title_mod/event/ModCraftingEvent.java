package com.yuyuto.no_title_mod.event;

import com.yuyuto.no_title_mod.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModCraftingEvent {
    @SubscribeEvent
    public static void onCraft(PlayerEvent.ItemCraftedEvent event){
        ItemStack hammer = ItemStack.EMPTY;

        for (int i = 0; i < event.getInventory().getContainerSize(); i++){

            ItemStack stack = event.getInventory().getItem(i);
            if (stack.is(ModItems.CRUSHER_HAMMER.get())){
                hammer = stack;
                break;
            }
        }

        if (!hammer.isEmpty()){
            hammer.hurtAndBreak(1, event.getEntity(), player -> {});
        }
    }
}
