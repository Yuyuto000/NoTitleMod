package com.yuyuto.no_title_mod.api.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Node間の電圧転送を行うメソッドクラス
 * <p>
 * このクラスでは探索のみ行う。
 */

public class NTEnergyTransfer {

    public static void transfer(Level level, BlockPos start, @NotNull NTEnergyPacket packet){
        Set<INTEnergyConsumer> consumers = bfs(level, start);
        for (INTEnergyConsumer consumer : consumers) {
            consumer.receivePacket(packet);
        }
    }

    public static @NotNull Set<INTEnergyConsumer> bfs(Level level, BlockPos start){
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        Set<INTEnergyConsumer> result = new HashSet<>();

        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty()){
            BlockPos current = queue.poll();
            for (Direction direction : Direction.values()) {
                BlockPos next = current.relative(direction);
                if (!visited.add(next)) continue;
                BlockEntity be = level.getBlockEntity(next);
                if (be == null) continue;
                if (be instanceof INTEnergyCable) queue.add(next);
                if (be instanceof INTEnergyConsumer consumer) result.add(consumer);
            }
        }

        return result;
    }
}
