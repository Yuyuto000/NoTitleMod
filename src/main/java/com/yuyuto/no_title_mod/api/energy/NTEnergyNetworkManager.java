package com.yuyuto.no_title_mod.api.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class NTEnergyNetworkManager {

    private static final Map<ResourceKey<Level>, List<NTEnergyNetwork>> networks = new HashMap<>();

    private static List<NTEnergyNetwork> getNetworks(@NotNull Level level){
        return networks.computeIfAbsent(level.dimension(), k -> new ArrayList<>());
    }

    public static @NotNull NTEnergyNetwork createNetwork(Level level, BlockPos startPos){

        List<NTEnergyNetwork> levelNetworks = getNetworks(level);
        NTEnergyNodePos target = new NTEnergyNodePos(level.dimension(), startPos);
        for(NTEnergyNetwork network : levelNetworks){
            if(network.contains(target)){
                return network;
            }
        }
        NTEnergyNetwork network = new NTEnergyNetwork();
        search(level, startPos, network, new HashSet<>());
        if(!network.getMembers().isEmpty()){
            levelNetworks.add(network);
        }
        return network;
    }

    private static void search(Level level, BlockPos pos, NTEnergyNetwork network, @NotNull Set<BlockPos> searched){

        if(!searched.add(pos)){
            return;
        }
        BlockEntity entity = level.getBlockEntity(pos);
        if(!(entity instanceof INTEnergyNodeManagements)){
            return;
        }
        network.addMember(new NTEnergyNodePos(level.dimension(), pos));
        for(Direction dir : Direction.values()){
            search(level, pos.relative(dir), network, searched);
        }
    }

    public static void updateAround(Level level, BlockPos startPos){

        List<NTEnergyNetwork> levelNetworks = getNetworks(level);
        NTEnergyNetwork targetNetwork = null;
        NTEnergyNodePos target = new NTEnergyNodePos(level.dimension(), startPos);
        for(NTEnergyNetwork network : levelNetworks){
            if(network.contains(target)){
                targetNetwork = network;
                break;
            }
        }
        if(targetNetwork != null){
            rebuildNetwork(level,targetNetwork);
        }
        else{
            createNetwork(level,startPos);
        }
    }

    public static void rebuildNetwork(Level level, NTEnergyNetwork oldNetwork){

        List<NTEnergyNodePos> nodes = new ArrayList<>(oldNetwork.getMembers());
        getNetworks(level).remove(oldNetwork);
        for(NTEnergyNodePos node : nodes){
            BlockEntity entity = level.getBlockEntity(node.pos());
            if(entity instanceof INTEnergyNodeManagements){
                createNetwork(level, node.pos());
            }
        }
    }

    public static void tick(ServerLevel level){

        List<NTEnergyNetwork> levelNetworks = getNetworks(level);
        for(NTEnergyNetwork network : levelNetworks){
            network.tick(level);
        }
    }
}