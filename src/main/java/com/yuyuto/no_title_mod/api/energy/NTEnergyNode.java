package com.yuyuto.no_title_mod.api.energy;

import java.util.UUID;

public class NTEnergyNode {

    private double voltage;
    private double current;
    private double resistance;
    private double power;
    private double maxPower;
    private NTEnergyNodeType type;
    private final UUID id;

    public NTEnergyNode(){
        id = UUID.randomUUID();
    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public double getResistance() {
        return resistance;
    }

    public void setResistance(double resistance) {
        this.resistance = resistance;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public UUID getId() {
        return id;
    }

    public NTEnergyNodeType getType() {
        return type;
    }

    public void setType(NTEnergyNodeType type) {
        this.type = type;
    }

    public double getMaxPower() {
        return maxPower;
    }

    public void setMaxPower(double maxPower) {
        this.maxPower = maxPower;
    }
}