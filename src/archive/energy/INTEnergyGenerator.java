package com.yuyuto.no_title_mod.api.energy;

public interface INTEnergyGenerator {
    double getMaxOutputCurrent();
    void generateEnergy();
    NTEnergyNode getNode();
}
