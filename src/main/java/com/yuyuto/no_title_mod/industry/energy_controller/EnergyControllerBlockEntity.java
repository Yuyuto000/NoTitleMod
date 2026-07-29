package com.yuyuto.no_title_mod.industry.energy_controller;

import com.yuyuto.no_title_mod.api.energy.NTEnergyPacket;
import com.yuyuto.no_title_mod.api.energy.NTEnergyTransfer;
import com.yuyuto.no_title_mod.industry.energy_genertator.EnergyGeneratorBlockEntity;
import com.yuyuto.no_title_mod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EnergyControllerBlockEntity extends BlockEntity{

    private double energy;
    private long lastTransferTime;

    public EnergyControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_CONTROLLER.get(), pos, state);
    }

    @SuppressWarnings("unused")
    public static void tick(@NotNull Level level, BlockPos pos, BlockState state, @NotNull EnergyControllerBlockEntity entity){
        if(!(level instanceof ServerLevel server)) return;
        entity.energy = 0;
        List<EnergyGeneratorBlockEntity> generators = entity.GeneratorBFS(level, pos);
        for(EnergyGeneratorBlockEntity generator : generators){
           entity.energy += generator.getGeneratedEnergy();
        }
        if(entity.energy <= 0) return;
        NTEnergyTransfer.transfer(level, entity.worldPosition, new NTEnergyPacket(entity.energy, entity.worldPosition, level.getGameTime()));
        entity.playTransferEffect(server);
    }

    private @NotNull List<EnergyGeneratorBlockEntity> GeneratorBFS(Level level, BlockPos pos){
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        List<EnergyGeneratorBlockEntity> GeneratorList = new ArrayList<>();

        queue.add(pos);
        visited.add(pos);
        while(!queue.isEmpty()){
            BlockPos current = queue.poll();
            for (Direction direction : Direction.values()){
                BlockPos next = current.relative(direction);
                if (!visited.add(next)) continue;
                BlockEntity be =  level.getBlockEntity(next);
                if (be == null) continue;
                if (be instanceof EnergyGeneratorBlockEntity generator){
                    queue.add(next);
                    GeneratorList.add(generator);
                }
            }
        }

        return GeneratorList;
    }

    private void playTransferEffect(@NotNull ServerLevel server){
        if (level != null && level.getGameTime() - lastTransferTime >= 20) {
            server.sendParticles(ParticleTypes.END_ROD, worldPosition.getX()+0.5, worldPosition.getY()+1.2, worldPosition.getZ()+0.5, 20, 0.25, 0.5, 0.25, 0.05);
            level.playSound(null, worldPosition, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.5F, 1.3F);
            lastTransferTime = level.getGameTime();
        }
    }
}
