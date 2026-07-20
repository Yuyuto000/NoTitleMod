package com.yuyuto.no_title_mod.api.energy;

import com.yuyuto.no_title_mod.industry.energy_genertator.EnergyGeneratorBlockEntity;
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

    public static void rebuildNetwork(Level level, NTEnergyNetwork oldNetwork) {

        if (oldNetwork == null) {
            return;
        }
        List<BlockPos> generators = new ArrayList<>();
        for (INTEnergyNodeManagements member : oldNetwork.getMembers()) {
            if (member.getNode().getType() == NTEnergyNodeType.GENERATOR) {
                generators.add(member.getNodePosition());
            }
        }
        oldNetwork.clearNetwork();
        networks.remove(oldNetwork);
        for (BlockPos pos : generators) {
            if (level.getBlockEntity(pos) instanceof EnergyGeneratorBlockEntity) {
                createNetwork(level, pos);
            }
        }
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