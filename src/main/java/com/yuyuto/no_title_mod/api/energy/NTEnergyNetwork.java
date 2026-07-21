package com.yuyuto.no_title_mod.api.energy;

import com.yuyuto.no_title_mod.NoTitleMod;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NTEnergyNetwork {

    private final List<NTEnergyNodePos> members = new ArrayList<>();
    private double totalPower;
    private double networkPower;

    public void addMember(NTEnergyNodePos pos){

        if(!members.contains(pos)){
            members.add(pos);
        }
    }

    public void clearNetwork(){
        members.clear();
    }

    public List<NTEnergyNodePos> getMembers(){
        return members;
    }

    public boolean contains(NTEnergyNodePos pos){
        return members.contains(pos);
    }

    public void tick(ServerLevel level){
        NoTitleMod.LOGGER.info("NETWORK TICK");

        generate(level);
        transfer(level);
        updateNodes(level);
    }

    private @Nullable BlockEntity getEntity(@NotNull ServerLevel level, @NotNull NTEnergyNodePos node){

        if(!level.dimension().equals(node.dimension())){
            return null;
        }
        return level.getBlockEntity(node.pos());
    }

    private void generate(ServerLevel level){
        NoTitleMod.LOGGER.info("Network generate start");
        totalPower = 0;
        for(NTEnergyNodePos node : members){
            BlockEntity entity = getEntity(level,node);
            if(entity instanceof INTEnergyGenerator generator){
                generator.generateEnergy();
                NoTitleMod.LOGGER.info("GENERATOR FOUND {}", node.pos());
                totalPower += generator.getNode().getPower();
            }
        }
    }


    private void transfer(ServerLevel level){

        double totalLoss = 0;
        for(NTEnergyNodePos node : members){
            BlockEntity entity = getEntity(level,node);
            if(entity instanceof INTEnergyConnector connector){
                totalLoss += connector.getResistance();
            }
        }
        networkPower = Math.max(totalPower - totalLoss,0);
        distributePower(level);
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
        if(consumerCount == 0){
            return;
        }
        double powerPerConsumer = networkPower / consumerCount;
        for(NTEnergyNodePos node : members){
            BlockEntity entity = getEntity(level,node);
            if(entity instanceof INTEnergyNodeManagements manager){
                switch(manager.getNode().getType()){
                    case CONNECTOR -> manager.getNode().setPower(networkPower);
                    case CONSUMER -> manager.getNode().setPower(powerPerConsumer);
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