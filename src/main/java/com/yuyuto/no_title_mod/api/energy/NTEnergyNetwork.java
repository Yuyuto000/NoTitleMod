package com.yuyuto.no_title_mod.api.energy;

import java.util.ArrayList;
import java.util.List;

public class NTEnergyNetwork {

    private final List<INTEnergyNodeManagements> members = new ArrayList<>();

    public List<INTEnergyGenerator> getGenerators(){
        List<INTEnergyGenerator> generators = new ArrayList<>();
        for(INTEnergyNodeManagements member : members){
            if(member instanceof INTEnergyGenerator generator){
                generators.add(generator);
            }
        }
        return generators;
    }

    public void addMember(INTEnergyNodeManagements member){
        if(!members.contains(member)){
            members.add(member);
            member.connection(this);
        }
    }

    public void removeMember(INTEnergyNodeManagements member) {
        if (members.remove(member)) {
            member.disconnect();
        }
    }

    public void clearNetwork(){
        for(INTEnergyNodeManagements member : members){
            member.disconnect();
        }
        members.clear();
    }

    public List<INTEnergyNodeManagements> getMembers(){
        return members;
    }

    public void tick(){
        generate();
        transfer();
        updateNodes();
    }

    private void generate(){
        for (INTEnergyNodeManagements member : members){
            if (member instanceof INTEnergyGenerator generator){
                generator.generateEnergy();
            }
        }
    }

    private void transfer(){
        for (INTEnergyNodeManagements member : members){
            if (member instanceof INTEnergyConnector connector){
                connector.transferEnergy();
            }
        }
    }

    private void updateNodes(){
        for (INTEnergyNodeManagements member : members){
            member.updateEnergyNode();
        }
    }

}
