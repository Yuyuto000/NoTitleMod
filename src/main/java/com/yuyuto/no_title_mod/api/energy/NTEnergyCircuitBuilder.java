package com.yuyuto.no_title_mod.api.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class NTEnergyCircuitBuilder {

    public static @NotNull NTEnergyCircuit build(@NotNull Level level, BlockPos start){
        NTEnergyCircuit circuit = new NTEnergyCircuit(level.dimension());
        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(start);
        while(!queue.isEmpty()){
            BlockPos pos = queue.poll();
            if(!visited.add(pos)) continue;
            BlockEntity be = level.getBlockEntity(pos);
            if(!(be instanceof INTEnergyNode node)) continue;
            circuit.addNode(pos, node.getNodeType());
            addNeighbor(queue,pos);
        }
        return circuit;
    }

    private static void addNeighbor(Queue<BlockPos> queue, BlockPos pos){
        for(Direction dir : Direction.values()){
            queue.add(pos.relative(dir));
        }
    }
}