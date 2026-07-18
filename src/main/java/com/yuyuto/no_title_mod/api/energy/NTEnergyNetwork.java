package com.yuyuto.no_title_mod.api.energy;

import java.util.ArrayList;
import java.util.List;

public class NTEnergyNetwork {

    private final List<NTEnergyNode> nodes = new ArrayList<>();

    public void addNode(NTEnergyNode node){
        if (!nodes.contains(node)){
            nodes.add(node);
        }
    }

    public void removeNode(NTEnergyNode node){
        nodes.remove(node);
    }

    public List<NTEnergyNode> getNodes(){
        return nodes;
    }
}
