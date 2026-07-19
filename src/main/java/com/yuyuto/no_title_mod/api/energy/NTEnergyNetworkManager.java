package com.yuyuto.no_title_mod.api.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NTEnergyNetworkManager {

    private static final List<NTEnergyNetwork> networks = new ArrayList<>();
    public static @NotNull NTEnergyNetwork createNetwork(Level level, BlockPos startPos){
        NTEnergyNetwork network = new NTEnergyNetwork();
        Set<BlockPos> searched = new HashSet<>();
        search(level, startPos, network, searched);
        networks.add(network);
        return network;
    }

    private static void search(Level level, BlockPos pos, NTEnergyNetwork network, @NotNull Set<BlockPos> searched){

        if(searched.contains(pos)){
            return;
        }
        searched.add(pos);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity == null){
            return;
        }
        if(blockEntity instanceof INTEnergyNodeManagements manager){
            network.addMember(manager);
            for(Direction direction : Direction.values()){
                search(level, pos.relative(direction), network, searched);
            }
        }
    }

    public static void removeNetwork(NTEnergyNetwork network){

        if(network == null){
            return;
        }
        network.clearNetwork();
        networks.remove(network);
    }

    public static void rebuildNetwork(Level level, NTEnergyNetwork oldNetwork){

        if(oldNetwork == null){
            return;
        }
        INTEnergyNodeManagements generator = null;
        for(INTEnergyNodeManagements member : oldNetwork.getMembers()){
            if(member.getNode().getType() == NTEnergyNodeType.GENERATOR){
                generator = member;
                break;
            }
        }
        if(generator == null){
            oldNetwork.clearNetwork();
            return;
        }
        BlockPos generatorPos = generator.getNodePosition();
        oldNetwork.clearNetwork();
        createNetwork(level, generatorPos);
    }

    public static void updateAround(Level level, BlockPos startPos){
        Set<BlockPos> searched = new HashSet<>();
        searchGenerator(level, startPos, searched);
    }

    public static void tick(){

        for(NTEnergyNetwork network : networks){
            network.tick();
        }

    }

    private static void searchGenerator(Level level, BlockPos pos, @NotNull Set<BlockPos> searched){
        if(searched.contains(pos)){
            return;
        }
        searched.add(pos);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity == null){
            return;
        }
        if (blockEntity instanceof EnergyGeneratorBlockEntity generator) {
            generator.buildNetwork();
            return;
        }
        if(blockEntity instanceof INTEnergyNodeManagements){
            for(Direction direction : Direction.values()){
                searchGenerator(level, pos.relative(direction), searched);
            }
        }
    }
}