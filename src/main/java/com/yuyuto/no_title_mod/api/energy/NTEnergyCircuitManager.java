package com.yuyuto.no_title_mod.api.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NTEnergyCircuitManager {

    private static final List<NTEnergyCircuit> CIRCUITS = new ArrayList<>();
    public static @NotNull NTEnergyCircuit getCircuit(Level level, BlockPos pos){

        /*
         * 既存回路探索
         */
        for(NTEnergyCircuit circuit : CIRCUITS){
            if(circuit.contains(pos)){
                return circuit;
            }
        }

        /*
         * 新規作成
         */
        NTEnergyCircuit circuit = NTEnergyCircuitBuilder.build(level, pos);
        CIRCUITS.add(circuit);
        return circuit;
    }

    public static void tick(){
        for(NTEnergyCircuit circuit : CIRCUITS){
            circuit.update();
        }
    }

    public static void clear(){
        CIRCUITS.clear();
    }

}