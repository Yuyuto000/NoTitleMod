package com.yuyuto.no_title_mod.api.energy;

import com.yuyuto.no_title_mod.NoTitleMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.*;

public class NTEnergyCircuit {

    private final Set<BlockEntity> generators = new HashSet<>();
    private final Set<BlockEntity> consumers = new HashSet<>();
    private final Set<BlockPos> positions = new HashSet<>();
    private final Level level;
    private double totalGeneration;
    private double totalDemand;

    public NTEnergyCircuit(Level level) {
        this.level = level;
    }

    /*
     * Node登録
     */
    public void addGenerator(BlockEntity generator){
        generators.add(generator);
    }

    public void addConsumer(BlockEntity consumer){
        consumers.add(consumer);
    }

    /*
     * Circuit計算
     */
    public void calculate(){

        totalGeneration = 0;
        totalDemand = 0;
        for(BlockEntity generator : generators){
            if(generator instanceof INTEnergyGenerator energyGenerator){
                totalGeneration += energyGenerator.getGeneratedEnergy();
            }
        }

        for(BlockEntity consumer : consumers){
            if(consumer instanceof INTEnergyConsumer energyConsumer){
                totalDemand += energyConsumer.getEnergyDemand();
            }
        }
        NoTitleMod.LOGGER.info("[Circuit] Generator={} Consumer={} Generation={} Demand={}", generators.size(), consumers.size(), totalGeneration, totalDemand);
    }

    /*
     * 電力配分
     */
    public void distribute(){

        if(consumers.isEmpty()){
            return;
        }
        boolean enough = totalGeneration >= totalDemand;
        for(BlockEntity consumer : consumers){
            if(consumer instanceof INTEnergyConsumer energyConsumer){
                energyConsumer.setPowered(enough);
                NoTitleMod.LOGGER.info("[Circuit] {} powered={}", consumer.getClass().getSimpleName(), enough);
            }
        }
    }

    public void update(){
        calculate();
        distribute();
    }

    /*
     * Getter
     */
    public void addPosition(BlockPos pos){
        positions.add(pos);
    }

    public Set<BlockEntity> getGenerators(){
        return generators;
    }

    public Set<BlockEntity> getConsumers(){
        return consumers;
    }

    public boolean contains(BlockPos pos) {
        return positions.contains(pos);
    }

    public Level getLevel() {
        return level;
    }
}