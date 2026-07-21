package com.yuyuto.no_title_mod.api.energy;

import com.yuyuto.no_title_mod.industry.energy_genertator.EnergyGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NTEnergyNetworkManager {

    private static final List<NTEnergyNetwork> networks = new ArrayList<>();

    public static @NotNull NTEnergyNetwork createNetwork(@NotNull Level level, BlockPos startPos){

        NTEnergyNodePos target = new NTEnergyNodePos(level.dimension(), startPos);
        for(NTEnergyNetwork network : networks){
            if(network.contains(target)){
                return network;
            }
        }
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
        if(blockEntity instanceof INTEnergyNodeManagements){
            network.addMember(new NTEnergyNodePos(level.dimension(), pos));
            for(Direction direction : Direction.values()){
                search(level, pos.relative(direction), network, searched);
            }
        }
    }

    public static void rebuildNetwork(Level level, NTEnergyNetwork oldNetwork){

        if(oldNetwork == null){
            return;
        }
        List<NTEnergyNodePos> generators = new ArrayList<>();
        for(NTEnergyNodePos nodePos : oldNetwork.getMembers()){
            if(!nodePos.dimension().equals(level.dimension())){
                continue;
            }
            BlockEntity blockEntity = level.getBlockEntity(nodePos.pos());
            if(blockEntity == null){
                continue;
            }
            if(blockEntity instanceof INTEnergyNodeManagements manager){
                if(manager.getNode().getType() == NTEnergyNodeType.GENERATOR){
                    generators.add(nodePos);
                }
            }
        }
        oldNetwork.clearNetwork();
        networks.remove(oldNetwork);
        for(NTEnergyNodePos generator : generators){
            createNetwork(level, generator.pos());
        }
    }

    public static void updateAround(Level level, BlockPos startPos){

        Set<BlockPos> searched = new HashSet<>();
        searchGenerator(level, startPos, searched);
    }

    public static void tick(ServerLevel level){

        for(NTEnergyNetwork network : networks){
            network.tick(level);
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
        if(blockEntity instanceof EnergyGeneratorBlockEntity){
            createNetwork(level,pos);
            return;
        }
        if(blockEntity instanceof INTEnergyNodeManagements){
            for(Direction direction : Direction.values()){
                searchGenerator(level, pos.relative(direction), searched);
            }
        }
    }
}