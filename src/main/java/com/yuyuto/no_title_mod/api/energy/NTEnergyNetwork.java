package com.yuyuto.no_title_mod.api.energy;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class NTEnergyNetwork {

    private final List<NTEnergyNodePos> members = new ArrayList<>();

    private double networkVoltage;
    private double networkCurrent;
    private double networkPower;

    public void addMember(NTEnergyNodePos pos){
        if(!members.contains(pos)){
            members.add(pos);
        }
    }

    public List<NTEnergyNodePos> getMembers(){
        return members;
    }

    public boolean contains(NTEnergyNodePos pos){
        return members.contains(pos);
    }

    public void tick(ServerLevel level){
        generate(level);
        transfer(level);
        distributePower(level);
        updateNodes(level);
    }

    private @Nullable BlockEntity getEntity(@NotNull ServerLevel level, @NotNull NTEnergyNodePos node){

        if(!level.dimension().equals(node.dimension())){
            return null;
        }
        return level.getBlockEntity(node.pos());
    }

    private void generate(ServerLevel level){

        networkVoltage = 0;
        double maxCurrent = 0;
        for(NTEnergyNodePos node : members){
            BlockEntity entity = getEntity(level,node);
            if(entity instanceof INTEnergyGenerator generator){
                generator.generateEnergy();
                networkVoltage = Math.max(networkVoltage, generator.getNode().getVoltage());
                maxCurrent += generator.getMaxOutputCurrent();
            }
        }
        networkCurrent = maxCurrent;
    }

    private void transfer(ServerLevel level){

        double totalResistance = 0;
        for(NTEnergyNodePos node : members){
            BlockEntity entity = getEntity(level,node);
            if(entity instanceof INTEnergyConnector connector){
                totalResistance += connector.getResistance();
            }
        }
        if(totalResistance <= 0){
            networkCurrent = 0;
            networkPower = 0;
            return;
        }
        double requiredCurrent = networkVoltage / totalResistance;
        networkCurrent = Math.min(requiredCurrent, networkCurrent);
        networkPower = networkVoltage * networkCurrent;
    }

    private void distributePower(ServerLevel level){

        int consumerCount = 0;
        for(NTEnergyNodePos node : members){
            BlockEntity entity = getEntity(level,node);
            if(entity instanceof INTEnergyNodeManagements manager){
                if(manager.getNode().getType() == NTEnergyNodeType.CONSUMER){
                    consumerCount++;
                }
            }
        }

        if(consumerCount <= 0){
            return;
        }
        double currentPerConsumer = networkCurrent / consumerCount;
        double powerPerConsumer = networkPower / consumerCount;
        for(NTEnergyNodePos node : members){
            BlockEntity entity = getEntity(level,node);
            if(!(entity instanceof INTEnergyNodeManagements manager)){
                continue;
            }
            switch(manager.getNode().getType()){
                case GENERATOR, CONNECTOR -> {
                    manager.getNode().setVoltage(networkVoltage);
                    manager.getNode().setCurrent(networkCurrent);
                    manager.getNode().setPower(networkPower);
                }
                case CONSUMER -> {
                    manager.getNode().setVoltage(networkVoltage);
                    manager.getNode().setCurrent(currentPerConsumer);
                    manager.getNode().setPower(powerPerConsumer);
                }
            }
        }
    }

    private void updateNodes(ServerLevel level){

        for(NTEnergyNodePos node : members){

            BlockEntity entity = getEntity(level,node);

            if(entity instanceof INTEnergyNodeManagements manager){

                manager.updateEnergyNode();
            }
        }
    }
}