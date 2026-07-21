package com.yuyuto.no_title_mod.api.energy;

import java.util.ArrayList;
import java.util.List;

public class NTEnergyNetwork {

    private final List<INTEnergyNodeManagements> members = new ArrayList<>();
    private double totalPower;
    private double networkPower;

    public void addMember(INTEnergyNodeManagements member){
        if(!members.contains(member)){
            members.add(member);
            member.connection(this);
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
        totalPower = 0;
        for (INTEnergyNodeManagements member : members){
            if (member instanceof INTEnergyGenerator generator){
                generator.generateEnergy();
                totalPower += generator.getNode().getPower();
            }
        }
    }

    private void transfer(){
        double totalLoss = 0;
        for (INTEnergyNodeManagements member : members){
            if (member instanceof INTEnergyConnector connector){
                totalLoss += connector.getResistance();
            }
        }
        networkPower = Math.max(totalPower - totalLoss, 0);
        distributePower();
    }

    private void distributePower(){
        int consumerCount = 0;
        for (INTEnergyNodeManagements member : members){
            if (member.getNode().getType() == NTEnergyNodeType.CONSUMER){
                consumerCount++;
            }
        }
        if (consumerCount == 0){
            return;
        }
        double powerPerConsumer = networkPower / consumerCount;
        for (INTEnergyNodeManagements member : members){
            switch (member.getNode().getType()){
                case CONNECTOR -> member.getNode().setPower(networkPower);
                case CONSUMER -> member.getNode().setPower(powerPerConsumer);
            }
        }
    }

    private void updateNodes(){
        for (INTEnergyNodeManagements member : members){
            member.updateEnergyNode();
        }
    }

}
