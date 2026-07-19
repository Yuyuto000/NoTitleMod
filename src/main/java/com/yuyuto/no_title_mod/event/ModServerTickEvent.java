package com.yuyuto.no_title_mod.event;

import com.yuyuto.no_title_mod.api.energy.NTEnergyNetworkManager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModServerTickEvent {

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){

        if (event.phase == TickEvent.Phase.END){
            NTEnergyNetworkManager.tick();
        }
    }
}
