package com.yuyuto.no_title_mod.api.energy;

public interface INTEnergyNodeManagements {
    void connection(NTEnergyNetwork network);
    void disconnect();
    public NTEnergyNode getNode();
}
