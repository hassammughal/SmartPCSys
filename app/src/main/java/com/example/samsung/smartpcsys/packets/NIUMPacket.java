package com.example.samsung.smartpcsys.packets;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.net.InetAddress;

public class NIUMPacket implements Serializable {
    private int packetType;
    private String nodeID;
    private String macAddress;
    private InetAddress hostAddress;
    private int queueSize;
    private double currCPUSpeed;
    private String availableMemory;
    private double availableBatteryPower;
    private String currentRAM;
    private double totalBattery;
    private double totalCPUSpeed;
    private String totalRAM;
    private String totalMemory;

    public int getPacketType() {
        return packetType;
    }

    public void setPacketType(int packetType) {
        this.packetType = packetType;
    }

    public String getNodeID() {
        return nodeID;
    }

    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }

    public String getAvailableMemory() {
        return availableMemory;
    }

    public void setAvailableMemory(String availableMemory) {
        this.availableMemory = availableMemory;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public InetAddress getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(InetAddress hostAddress) {
        this.hostAddress = hostAddress;
    }

    public double getAvailableBatteryPower() {
        return availableBatteryPower;
    }

    public void setAvailableBatteryPower(double availableBatteryPower) {
        this.availableBatteryPower = availableBatteryPower;
    }

    public double getCurrCPUSpeed() {
        return currCPUSpeed;
    }

    public void setCurrCPUSpeed(double currCPUSpeed) {
        this.currCPUSpeed = currCPUSpeed;
    }

    public String getCurrentRAM() {
        return currentRAM;
    }

    public void setCurrentRAM(String currentRAM) {
        this.currentRAM = currentRAM;
    }

    public double getTotalBattery() {
        return totalBattery;
    }

    public void setTotalBattery(double totalBattery) {
        this.totalBattery = totalBattery;
    }

    public double getTotalCPUSpeed() {
        return totalCPUSpeed;
    }

    public void setTotalCPUSpeed(double totalCPUSpeed) {
        this.totalCPUSpeed = totalCPUSpeed;
    }

    public String getTotalRAM() {
        return totalRAM;
    }

    public void setTotalRAM(String totalRAM) {
        this.totalRAM = totalRAM;
    }

    public String getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(String totalMemory) {
        this.totalMemory = totalMemory;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    @Override
    @NonNull
    public String toString() {
        return (this.getPacketType() + "|" + this.getNodeID() + "|" + this.getHostAddress() + "|" + this.getMacAddress() + "|" + this.getQueueSize() + "|" + Double.toString(this.getAvailableBatteryPower())
                + "|" + Double.toString(this.getCurrCPUSpeed()) + "|" + this.getCurrentRAM() + "|" + this.getAvailableMemory() + "|" + Double.toString(this.getTotalBattery()) + "|" +
                Double.toString(this.getTotalCPUSpeed()) + "|" + this.getTotalRAM() + "|" + this.getTotalMemory());
    }
}
