package com.yuyuto.no_title_mod.industry.energy_cable;

import com.yuyuto.no_title_mod.api.energy.INTEnergyNodeManagements;
import com.yuyuto.no_title_mod.api.energy.NTEnergyNetwork;
import com.yuyuto.no_title_mod.api.energy.NTEnergyNode;
import com.yuyuto.no_title_mod.api.energy.NTEnergyNodeType;
import com.yuyuto.no_title_mod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class EnergyCableBlockEntity extends BlockEntity implements INTEnergyNodeManagements {

    /**
     * ケーブル
     * 同線ケーブルと同格の能力を持たせる。
     * 仮に出力先が複数ある場合は等分して送信
     * 仮に入力元が複数ある場合は合計値を受理
     */

    private final NTEnergyNode energyNode = new NTEnergyNode();
    private NTEnergyNetwork network;
    private List<NTEnergyNode> connections;
    private final double resistance = 0.25218f;

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

    public EnergyCableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_CABLE.get(), pos, state);
        energyNode.setType(NTEnergyNodeType.CONNECTOR);
    }

    private @Nullable NTEnergyNode getNeighbourNode(Direction direction){
        BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(direction));
        if (blockEntity instanceof INTEnergyNodeManagements provider){
            return provider.getNode();
        }
        return null;
    }

    public void tick(){
        NTEnergyNode node = getNeighbourNode(Direction.NORTH);
        if (node == null){
            return;
        }
        double inputPower = node.getPower();
        energyNode.setPower(inputPower);
    }
    private void update(){
    }
}
