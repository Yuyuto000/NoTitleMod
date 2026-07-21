package com.yuyuto.no_title_mod.event;

import com.yuyuto.no_title_mod.api.energy.NTEnergyNetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

public class ModServerTickEvent {

    @SubscribeEvent
    public static void onServerTick(TickEvent.@NotNull ServerTickEvent event){
        if(event.phase == TickEvent.Phase.END){
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            for(ServerLevel level : server.getAllLevels()){
                NTEnergyNetworkManager.tick(level);

            }
        }
    }
}
