package com.yuyuto.no_title_mod.industry.energy_genertator;

import com.yuyuto.no_title_mod.api.energy.INTEnergyNodeManagements;
import com.yuyuto.no_title_mod.api.energy.NTEnergyNetwork;
import com.yuyuto.no_title_mod.api.energy.NTEnergyNode;
import com.yuyuto.no_title_mod.api.energy.NTEnergyNodeType;
import com.yuyuto.no_title_mod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class EnergyGeneratorBlockEntity extends BlockEntity implements INTEnergyNodeManagements {

    private final NTEnergyNode energyNode = new NTEnergyNode();
    private NTEnergyNetwork network;

    @Override
    public void connection(NTEnergyNetwork network) {
        this.network = network;
    }

    @Override
    public void disconnect() {
        this.network = null;
    }

    @Override
    public NTEnergyNode getNode() {
        return energyNode;
    }

    @Override
    public void setRemoved(){
        disconnectNetwork();
        super.setRemoved();
    }

    public void disconnectNetwork(){
        if (network != null){
            NTEnergyNetwork oldNetwork = network;
            oldNetwork.removeMember(this);
            oldNetwork.checkNetwork();
            network = null;
        }
    }

    public EnergyGeneratorBlockEntity(BlockPos pos, BlockState state){
        super(ModBlockEntities.ENERGY_GENERATOR.get(), pos, state);
        energyNode.setType(NTEnergyNodeType.GENERATOR);
    }

    public void buildNetwork(){
        if(network != null){
            network.clearNetwork();
        }
        this.network = new NTEnergyNetwork();
        Set<BlockPos> searched = new HashSet<>();
        search(worldPosition, network, searched);
    }

    private void search(BlockPos pos, NTEnergyNetwork network, @NotNull Set<BlockPos> searched){
        if (searched.contains(pos)){
            return;
        }
        searched.add(pos);

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null){
            return;
        }
        if (blockEntity instanceof INTEnergyNodeManagements manager){
            network.addMember(manager);
            for (Direction direction : Direction.values()){
                BlockPos next = pos.relative(direction);
                search(next, network, searched);
            }
        }
    }

}
