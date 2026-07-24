package com.yuyuto.no_title_mod.api.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NTEnergyCircuitManager {

    private static final Map<ResourceKey<Level>, List<NTEnergyCircuit>> CIRCUITS = new HashMap<>();
    public static @NotNull NTEnergyCircuit getCircuit(@NotNull Level level, BlockPos pos){

        List<NTEnergyCircuit> circuits =
                CIRCUITS.computeIfAbsent(level.dimension(), key -> new ArrayList<>());
        for(NTEnergyCircuit circuit : circuits){
            if(circuit.contains(pos)){
                return circuit;
            }
        }
        NTEnergyCircuit circuit = NTEnergyCircuitBuilder.build(level, pos);
        circuits.add(circuit);
        return circuit;
    }

    public static void tick(){
        for(List<NTEnergyCircuit> circuits : CIRCUITS.values()){
            for(NTEnergyCircuit circuit : circuits){
                circuit.update();
            }
        }
    }

    public static void clear(){
        CIRCUITS.clear();
    }

}