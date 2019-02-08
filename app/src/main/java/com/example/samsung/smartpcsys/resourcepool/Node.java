package com.example.samsung.smartpcsys.resourcepool;

import java.io.Serializable;

public class Node implements Serializable {
    private String nodeID;
    private String ipAddress;
    private String macAddress;
    private int queueSize;
    private double currentBattery;
    private double currentCPUSpeed;
    private String currentRAM;
    private String currentMemory;
    private double totalBattery;
    private double totalCPUSpeed;
    private String totalRAM;
    private String totalMemory;

    public Node() {
    }

    public Node(String nodeID, String ipAddress, String macAddress, int queueSize, double currentBattery, double currentCPUSpeed, String currentRAM, String currentMemory, double totalBattery,
                double totalCPUSpeed, String totalRAM, String totalMemory) {
        this.nodeID = nodeID;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.queueSize = queueSize;
        this.currentBattery = currentBattery;
        this.currentCPUSpeed = currentCPUSpeed;
        this.currentRAM = currentRAM;
        this.currentMemory = currentMemory;
        this.totalBattery = totalBattery;
        this.totalCPUSpeed = totalCPUSpeed;
        this.totalRAM = totalRAM;
        this.totalMemory = totalMemory;
    }

    public String getNodeID() {
        return nodeID;
    }

    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public double getCurrentBattery() {
        return currentBattery;
    }

    public void setCurrentBattery(double currentBattery) {
        this.currentBattery = currentBattery;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public double getCurrentCPUSpeed() {
        return currentCPUSpeed;
    }

    public void setCurrentCPUSpeed(double currentCPUSpeed) {
        this.currentCPUSpeed = currentCPUSpeed;
    }

    public String getCurrentRAM() {
        return currentRAM;
    }

    public void setCurrentRAM(String currentRAM) {
        this.currentRAM = currentRAM;
    }

    public String getCurrentMemory() {
        return currentMemory;
    }

    public void setCurrentMemory(String currentMemory) {
        this.currentMemory = currentMemory;
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

}
