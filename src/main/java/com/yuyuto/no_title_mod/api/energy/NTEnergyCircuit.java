package com.yuyuto.no_title_mod.api.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.*;

public class NTEnergyCircuit {

    /*
     * 所属Dimension
     */
    private final ResourceKey<Level> dimension;
    /*
     * 回路参加Node
     *
     * Key:
     * 位置
     *
     * Value:
     * Node種類
     */
    private final Map<BlockPos, NTEnergyNodeType> nodes = new HashMap<>();
    /*
     * 計算結果
     */
    private double totalGeneration;
    private double totalDemand;
    private boolean powered;

    public NTEnergyCircuit(ResourceKey<Level> dimension) {
        this.dimension = dimension;
    }

    /*
     * =========================
     * Node登録
     * =========================
     */
    public void addNode(BlockPos pos, NTEnergyNodeType type){
        nodes.put(pos,type);
    }

    /*
     * =========================
     * Circuit計算
     * =========================
     */
    public void calculate(Level level){
        totalGeneration = 0;
        totalDemand = 0;
        for(Map.Entry<BlockPos,NTEnergyNodeType> entry : nodes.entrySet()){
            BlockPos pos = entry.getKey();
            NTEnergyNodeType type = entry.getValue();
            BlockEntity be = level.getBlockEntity(pos);
            if(be == null) continue;
            switch(type){
                case GENERATOR -> {
                    if(be instanceof INTEnergyGenerator generator){
                        totalGeneration += generator.getGeneratedEnergy();
                    }
                }
                case CONSUMER -> {
                    if(be instanceof INTEnergyConsumer consumer){
                        if(consumer.canWork()){
                            totalDemand += consumer.getEnergyDemand();
                        }
                    }
                }
                case CABLE -> {
                    // 計算不要
                }
            }
        }
    }

    /*
     * =========================
     * Power配布
     * =========================
     */

    public void distribute(Level level){
        powered = totalGeneration >= totalDemand;
        for(Map.Entry<BlockPos,NTEnergyNodeType> entry : nodes.entrySet()){
            if(entry.getValue() != NTEnergyNodeType.CONSUMER)
                continue;
            BlockEntity be = level.getBlockEntity(entry.getKey());
            if(be instanceof INTEnergyConsumer consumer){
                consumer.setPowered(powered);
            }
        }
    }

    public void update(Level level){
        calculate(level);
        distribute(level);
    }

    /*
     * =========================
     * Getter
     * =========================
     */
    public boolean contains(BlockPos pos){
        return nodes.containsKey(pos);
    }
    public ResourceKey<Level> getDimension(){
        return dimension;
    }
    public Map<BlockPos,NTEnergyNodeType> getNodes(){
        return nodes;
    }
    public double getTotalGeneration(){
        return totalGeneration;
    }
    public double getTotalDemand(){
        return totalDemand;
    }
    public boolean isPowered(){
        return powered;
    }
}