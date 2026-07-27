package com.yuyuto.no_title_mod.event;

import com.yuyuto.no_title_mod.industry.conveyor.ConveyorTransferQueue;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class ModServerTickEvent {

    @SubscribeEvent
    public static void onServerTick(TickEvent.@NotNull ServerTickEvent event){

        if(event.phase != TickEvent.Phase.END) return;
        MinecraftServer server = event.getServer();
        for(ServerLevel level : server.getAllLevels()){
            ConveyorTransferQueue.commit(level);
        }
    }
}