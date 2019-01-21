package com.example.samsung.smartpcsys.resourcepool;

import android.net.MacAddress;

import java.net.InetAddress;

public class Nodes {
    private InetAddress ipAddress;
    private MacAddress macAddress;
    private int id;
    private String modelNo;
    private double currentBattery;
    private double currentCPUspeed;
    private double currentRAM;
    private double currentMemory;
    private double currentExtMemory;
    private double totalCPUspeed;
    private double totalRAM;
    private double totalMemory;
    private double totalExtMemory;

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public MacAddress getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(MacAddress macAddress) {
        this.macAddress = macAddress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModelNo() {
        return modelNo;
    }

    public void setModelNo(String modelNo) {
        this.modelNo = modelNo;
    }

    public double getCurrentBattery() {
        return currentBattery;
    }

    public void setCurrentBattery(double currentBattery) {
        this.currentBattery = currentBattery;
    }

    public double getCurrentCPUspeed() {
        return currentCPUspeed;
    }

    public void setCurrentCPUspeed(double currentCPUspeed) {
        this.currentCPUspeed = currentCPUspeed;
    }

    public double getCurrentRAM() {
        return currentRAM;
    }

    public void setCurrentRAM(double currentRAM) {
        this.currentRAM = currentRAM;
    }

    public double getCurrentMemory() {
        return currentMemory;
    }

    public void setCurrentMemory(double currentMemory) {
        this.currentMemory = currentMemory;
    }

    public double getCurrentExtMemory() {
        return currentExtMemory;
    }

    public void setCurrentExtMemory(double currentExtMemory) {
        this.currentExtMemory = currentExtMemory;
    }

    public double getTotalCPUspeed() {
        return totalCPUspeed;
    }

    public void setTotalCPUspeed(double totalCPUspeed) {
        this.totalCPUspeed = totalCPUspeed;
    }

    public double getTotalRAM() {
        return totalRAM;
    }

    public void setTotalRAM(double totalRAM) {
        this.totalRAM = totalRAM;
    }

    public double getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(double totalMemory) {
        this.totalMemory = totalMemory;
    }

    public double getTotalExtMemory() {
        return totalExtMemory;
    }

    public void setTotalExtMemory(double totalExtMemory) {
        this.totalExtMemory = totalExtMemory;
    }
}
