package com.yuyuto.no_title_mod.api.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Node間を送信する際に使う通信規格。
 */

public class NTEnergyPacket {
    private double energy;
    private final Set<BlockPos> sources;
    private final long time;

    /**
     * @param energy エネルギー量
     * @param sources 発電元
     * @param time 発生時刻(GameTime)
     */
    public NTEnergyPacket(double energy, Set<BlockPos> sources, long time) {
        this.energy = energy;
        this.sources = new HashSet<>(sources);
        this.time = time;
    }

    public double energy() {
        return energy;
    }

    public Set<BlockPos> sources() {
        return sources;
    }

    public long time() {
        return time;
    }

    public void consume(double amount) {
        energy -= amount;
        if (energy < 0) {
            energy = 0;
        }
    }

    public CompoundTag save(){
        CompoundTag tag = new CompoundTag();
        tag.putDouble("energy", energy);
        tag.putLong("time", time);
        ListTag sourceList = new ListTag();
        for (BlockPos pos : sources) {
            CompoundTag posTag = new CompoundTag();
            posTag.putDouble("x", pos.getX());
            posTag.putDouble("y", pos.getY());
            posTag.putDouble("z", pos.getZ());
            sourceList.add(posTag);
        }
        tag.put("sources", sourceList);
        return tag;
    }

    @Contract("_ -> new")
    public static @NotNull NTEnergyPacket load(CompoundTag tag){
        double energy = tag.getDouble("energy");
        long time = tag.getLong("time");
        Set<BlockPos> sources = new HashSet<>();

        ListTag sourceList = tag.getList("sources", Tag.TAG_COMPOUND);
        for (Tag t :  sourceList){
            CompoundTag posTag = (CompoundTag)t;
            sources.add(new BlockPos(posTag.getInt("x"),posTag.getInt("y"),posTag.getInt("z")));
        }
        return new NTEnergyPacket(energy,sources,time);
    }
}
