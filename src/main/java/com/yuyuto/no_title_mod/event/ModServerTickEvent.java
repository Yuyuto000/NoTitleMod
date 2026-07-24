package com.yuyuto.no_title_mod.event;

import com.yuyuto.no_title_mod.api.energy.NTEnergyCircuitManager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class ModServerTickEvent {

    @SubscribeEvent
    public static void onServerTick(TickEvent.@NotNull ServerTickEvent event){
        if(event.phase == TickEvent.Phase.END){
            NTEnergyCircuitManager.tick();
        }

    }
}
