package com.yuyuto.no_title_mod.api.energy;

import net.minecraftforge.energy.EnergyStorage;

public class NTEnergyStorage extends EnergyStorage {

    public NTEnergyStorage(int capacity, int maxReceive, int maxExtract){
        super(capacity, maxReceive, maxExtract);
    }

    public void addEnergy(int amount){
        this.energy = Math.min(this.capacity, this.energy + amount);
    }
}
