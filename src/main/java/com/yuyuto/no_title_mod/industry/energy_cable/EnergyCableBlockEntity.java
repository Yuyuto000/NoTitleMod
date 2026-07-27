package com.yuyuto.no_title_mod.industry.energy_cable;

import com.yuyuto.no_title_mod.api.energy.*;
import com.yuyuto.no_title_mod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class EnergyCableBlockEntity extends BlockEntity implements INTEnergyNode {

    private NTEnergyPacket packet;

    public EnergyCableBlockEntity(BlockPos pos, BlockState state){
        super(ModBlockEntities.ENERGY_CABLE.get(), pos, state);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction side){
        return super.getCapability(capability, side);
    }

    @Override
    public NTEnergyNodeType getNodeType() {
        return NTEnergyNodeType.CABLE;
    }

    // NBT
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag){
        if(packet != null) tag.put("Packet", packet.save());
        super.saveAdditional(tag);
    }

    @Override
    public void load(@NotNull CompoundTag tag){
        super.load(tag);
        if(tag.contains("Packet")) packet = NTEnergyPacket.load(tag.getCompound("Packet"));
    }

    @Override
    public NTEnergyPacket getPacket() {
        return packet;
    }

    @Override
    public BlockPos getPos() {
        return worldPosition;
    }

    @Override
    public void receivePacket(NTEnergyPacket packet) {
        this.packet = packet;
        setChanged();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, @NotNull EnergyCableBlockEntity entity){
        if (entity.packet != null){
            NTEnergyTransfer.transfer(level, entity.worldPosition, new HashSet<>(), entity.packet);
            entity.packet = null;
        }
    }
}