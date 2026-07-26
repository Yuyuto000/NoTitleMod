package com.yuyuto.no_title_mod.api.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NTEnergyCircuitManager {

    private static final Map<ResourceKey<Level>, List<NTEnergyCircuit>> CIRCUITS = new HashMap<>();

    /*
     * Tick処理
     */
    public static void tick(@NotNull Level level){

        List<NTEnergyCircuit> circuits = CIRCUITS.get(level.dimension());
        if(circuits == null)
            return;
        for(NTEnergyCircuit circuit : circuits){
            circuit.update(level);
        }
    }

    /*
     * 再構築
     */
    public static void rebuild(@NotNull Level level, BlockPos changedPos){

        removeAffectedCircuit(level, changedPos);
        NTEnergyCircuit newCircuit = NTEnergyCircuitBuilder.build(level, changedPos);
        addCircuit(level, newCircuit);
    }

    /*
     * 影響Circuit削除
     */
    private static void removeAffectedCircuit(@NotNull Level level, BlockPos pos){

        List<NTEnergyCircuit> circuits = CIRCUITS.computeIfAbsent(level.dimension(), key -> new ArrayList<>());
        circuits.removeIf(circuit -> circuit.contains(pos));
    }

    /*
     * Circuit追加
     */
    private static void addCircuit(@NotNull Level level, NTEnergyCircuit circuit){
        CIRCUITS.computeIfAbsent(level.dimension(), key -> new ArrayList<>()).add(circuit);
    }

    /*
     * 位置からCircuit取得
     */
    public static @Nullable NTEnergyCircuit getCircuit(@NotNull Level level, BlockPos pos){

        List<NTEnergyCircuit> circuits = CIRCUITS.get(level.dimension());
        if(circuits == null) return null;
        for(NTEnergyCircuit circuit : circuits){
            if(circuit.contains(pos)){
                return circuit;
            }
        }
        return null;
    }

    public static void markDirty(@NotNull Level level, BlockPos pos){

        List<NTEnergyCircuit> circuits = CIRCUITS.get(level.dimension());
        if(circuits == null) return;
        circuits.removeIf(circuit -> circuit.contains(pos));
    }

    /*
     * Dimension単位削除
     */
    public static void clear(@NotNull Level level){
        CIRCUITS.remove(level.dimension());
    }

    /*
     * 全削除
     */
    public static void clear(){
        CIRCUITS.clear();
    }
}