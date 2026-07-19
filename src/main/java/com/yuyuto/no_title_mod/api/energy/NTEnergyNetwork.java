package com.yuyuto.no_title_mod.api.energy;

import java.util.ArrayList;
import java.util.List;

public class NTEnergyNetwork {

    private final List<INTEnergyNodeManagements> members = new ArrayList<>();

    public void addMember(INTEnergyNodeManagements member){
        if (!members.contains(member)){
            members.add(member);
            member.connection(this);
        }
    }

    public void removeMember(INTEnergyNodeManagements member){
        if (members.remove(member))
            member.disconnect();
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

    public void checkNetwork(){
        if(!isValid()){
            clearNetwork();
        }
    }

    public boolean isValid(){
        boolean generator = false;
        boolean connector = false;
        boolean consumer = false;

        for (INTEnergyNodeManagements member : members){
            switch(member.getNode().getType()){
                case GENERATOR -> generator = true;
                case CONNECTOR -> connector = true;
                case CONSUMER -> consumer = true;
            }
        }

        return generator && connector && consumer;
    }
}
