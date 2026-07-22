package com.yuyuto.no_title_mod.api.energy;

import com.yuyuto.no_title_mod.NoTitleMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class NTEnergyNetwork {

    private final List<NTEnergyNodePos> members = new ArrayList<>();
    // Consumerごとの経路抵抗
    private final Map<NTEnergyNodePos, Double> consumerResistance = new HashMap<>();
    // Consumerごとの供給電力
    private final Map<NTEnergyNodePos, Double> consumerPower = new HashMap<>();
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
        calculateConsumerResistance(level);
        transfer();
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

    private void calculateConsumerResistance(ServerLevel level){
        consumerResistance.clear();
        for(NTEnergyNodePos node : members){
            BlockEntity entity = getEntity(level,node);
            if(entity instanceof INTEnergyNodeManagements manager){
                if(manager.getNode().getType() == NTEnergyNodeType.CONSUMER){
                    double resistance = searchResistance(level, node.pos(), new HashSet<>(), 0);
                    consumerResistance.put(node,resistance);
                    NoTitleMod.LOGGER.info(
                            "Consumer {} resistance {}",
                            node.pos(),
                            resistance
                    );
                }
            }
        }
    }

    private double searchResistance(ServerLevel level, BlockPos pos, @NotNull Set<BlockPos> visited, double resistance){

        if(!visited.add(pos)){
            return Double.MAX_VALUE;
        }
        BlockEntity entity = level.getBlockEntity(pos);
        if(entity instanceof INTEnergyNodeManagements manager){
            if(manager.getNode().getType() == NTEnergyNodeType.GENERATOR){
                return resistance;
            }
        }
        double min = Double.MAX_VALUE;
        for(Direction dir : Direction.values()){
            BlockPos next = pos.relative(dir);
            BlockEntity nextEntity = level.getBlockEntity(next);
            if(!(nextEntity instanceof INTEnergyNodeManagements)){
                continue;
            }
            double nextResistance = resistance;
            if(nextEntity instanceof INTEnergyConnector connector){
                nextResistance += connector.getResistance();
            }
            Set<BlockPos> copy = new HashSet<>(visited);
            min = Math.min(min, searchResistance(level, next, copy, nextResistance));
        }
        return min;
    }

    private void transfer(){

        consumerPower.clear();
        double ratio = getRatio();
        double actualCurrent = 0;
        for(Map.Entry<NTEnergyNodePos,Double> entry : consumerResistance.entrySet()){
            double current = calculateCurrent(entry.getValue());
            current *= ratio;
            double power = networkVoltage * current;
            consumerPower.put(entry.getKey(), power);
            actualCurrent += current;
        }
        networkCurrent = actualCurrent;
        networkPower = networkVoltage * networkCurrent;
    }

    private double getRatio() {
        double requiredCurrent = 0;
        for(Map.Entry<NTEnergyNodePos,Double> entry : consumerResistance.entrySet()){
            double resistance = entry.getValue();
            if(resistance == Double.MAX_VALUE){
                continue;
            }
            double current = calculateCurrent(resistance);
            requiredCurrent += current;
        }

        // 発電能力以上なら制限
        double ratio = 1;
        if(requiredCurrent > networkCurrent){
            ratio = networkCurrent / requiredCurrent;
        }
        return ratio;
    }

    private double calculateCurrent(double resistance){
        if(resistance <= 0 || resistance == Double.MAX_VALUE){
            return 0;
        }
        return networkVoltage / resistance;
    }

    private void distributePower(ServerLevel level){

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
                    double power = consumerPower.getOrDefault(node,0D);
                    double current = power / networkVoltage;
                    manager.getNode().setVoltage(networkVoltage);
                    manager.getNode().setCurrent(current);
                    manager.getNode().setPower(power);
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