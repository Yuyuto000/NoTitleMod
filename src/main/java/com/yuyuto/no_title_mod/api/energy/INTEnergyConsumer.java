package com.yuyuto.no_title_mod.api.energy;

@SuppressWarnings("unused")
public interface INTEnergyConsumer extends INTEnergyNode {
    double getEnergyDemand();
    boolean canWork();
    void setPowered(boolean value);
}
