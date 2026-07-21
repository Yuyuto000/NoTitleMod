package com.yuyuto.no_title_mod.industry.energy_cable;

import com.yuyuto.no_title_mod.api.energy.*;
import com.yuyuto.no_title_mod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * ケーブル
 * 同線ケーブルと同格の能力を持たせる。
 * 仮に出力先が複数ある場合は等分して送信
 * 仮に入力元が複数ある場合は合計値を受理
 */

public class EnergyCableBlockEntity extends BlockEntity implements INTEnergyNodeManagements, INTEnergyConnector {

    private final NTEnergyNode energyNode = new NTEnergyNode();
    private NTEnergyNetwork network;

    public EnergyCableBlockEntity(BlockPos pos, BlockState state){
        super(ModBlockEntities.ENERGY_CABLE.get(), pos, state);
        energyNode.setType(NTEnergyNodeType.CONNECTOR);
        energyNode.setResistance(0.25218);
    }

    @Override
    public void connection(NTEnergyNetwork network){
        this.network = network;
    }

    @Override
    public void disconnect(){
        this.network = null;
    }

    @Override
    public NTEnergyNode getNode(){
        return energyNode;
    }

    @Override
    public BlockPos getNodePosition() {
        return worldPosition;
    }

    @Override
    public void updateEnergyNode(){
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level == null || level.isClientSide) {
            return;
        }
        NTEnergyNetworkManager.updateAround(level, worldPosition);
    }

    @Override
    public void setRemoved(){
        if(network != null){
            NTEnergyNetworkManager.rebuildNetwork(level, network);
        }
        super.setRemoved();
    }

    @Override
    public double getResistance() {
        return energyNode.getResistance();
    }
}