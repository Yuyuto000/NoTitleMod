package com.yuyuto.no_title_mod.industry.energy_genertator;

import com.yuyuto.no_title_mod.api.energy.*;
import com.yuyuto.no_title_mod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EnergyGeneratorBlockEntity extends BlockEntity implements INTEnergyNodeManagements, INTEnergyGenerator {

    private final NTEnergyNode energyNode = new NTEnergyNode();
    private NTEnergyNetwork network;

    public EnergyGeneratorBlockEntity(BlockPos pos, BlockState state){
        super(ModBlockEntities.ENERGY_GENERATOR.get(), pos, state);
        energyNode.setType(NTEnergyNodeType.GENERATOR);
    }

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
    public BlockPos getNodePosition() {
        return worldPosition;
    }

    @Override
    public void onLoad(){
        super.onLoad();
        NTEnergyNetworkManager.updateAround(level, worldPosition);
    }

    @Override
    public void updateEnergyNode(){
        energyNode.setPower(NTEnergyManager.calculatePower(energyNode.getVoltage(), energyNode.getCurrent()));
    }

    public void buildNetwork(){
        if (level == null){
            return;
        }
        network = NTEnergyNetworkManager.createNetwork(level, worldPosition);
    }

    @Override
    public void setRemoved(){
        if (network != null){
            NTEnergyNetworkManager.removeNetwork(network);
        }
        super.setRemoved();
    }

    @Override
    public void generateEnergy(){
        double maxVoltage = 120;
        if(energyNode.getVoltage() < maxVoltage){
            energyNode.setVoltage(energyNode.getVoltage()+1);
        }
        energyNode.setResistance(10);
        energyNode.setCurrent(NTEnergyManager.calculateCurrent(energyNode.getVoltage(), energyNode.getResistance()));
        energyNode.setPower(NTEnergyManager.calculatePower(energyNode.getVoltage(), energyNode.getCurrent()));
    }

    @Override
    public double getGeneratePower() {
        return 500;
    }
}