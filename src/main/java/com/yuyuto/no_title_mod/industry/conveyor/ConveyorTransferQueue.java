package com.yuyuto.no_title_mod.industry.conveyor;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConveyorTransferQueue {

    private static final List<Request> REQUESTS = new ArrayList<>();


    public static void stage(ServerLevel level, BlockPos from, BlockPos target, @NotNull ItemStack stack, long executeTick){
        REQUESTS.add(new Request(level, from, target, stack.copy(), executeTick));
    }

    public static void commit(@NotNull ServerLevel level){

        long now = level.getGameTime();
        Iterator<Request> iterator = REQUESTS.iterator();
        while(iterator.hasNext()){
            Request request = iterator.next();
            if(request.level() != level)
                continue;
            if(request.executeTick() > now)
                continue;
            ConveyorTransferExecutor.execute(request);
            iterator.remove();
        }
    }

    public record Request(ServerLevel level, BlockPos from, BlockPos target, ItemStack stack, long executeTick){}
}