package com.yuyuto.no_title_mod.api.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class NTEnergyCircuitManager {

    private static final Map<String,NTEnergyCircuit> CIRCUITS = new HashMap<>();

    public static NTEnergyCircuit getCircuit(Level level, BlockPos pos){
        String key = createKey(level,pos);
        if(!CIRCUITS.containsKey(key)){
            NTEnergyCircuit circuit = NTEnergyCircuitBuilder.build(level, pos);
            CIRCUITS.put(key, circuit);
        }
        return CIRCUITS.get(key);

    }

    private static @NotNull String createKey(@NotNull Level level, BlockPos pos){
        return level.dimension().location() +"_" +pos;
    }

    public static void clear(){
        CIRCUITS.clear();
    }

}