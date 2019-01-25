package com.example.samsung.smartpcsys.Packets;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.net.InetAddress;

public class NIUMPacket implements Serializable {
    private int packetType;
   private String nodeID;
    private double queueWaitingTime;
   private double availableMemory;
   private double availableBatteryPower;
    private InetAddress destAddress;

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

    public double getQueueWaitingTime() {
        return queueWaitingTime;
    }

    public void setQueueWaitingTime(double queueWaitingTime) {
        this.queueWaitingTime = queueWaitingTime;
    }

    public double getAvailableMemory() {
        return availableMemory;
    }

    public void setAvailableMemory(double availableMemory) {
        this.availableMemory = availableMemory;
    }

    public double getAvailableBatteryPower() {
        return availableBatteryPower;
    }

    public void setAvailableBatteryPower(double availableBatteryPower) {
        this.availableBatteryPower = availableBatteryPower;
    }

    public InetAddress getDestAddress() {
        return destAddress;
    }

    public void setDestAddress(InetAddress destAddress) {
        this.destAddress = destAddress;
    }

    @Override
    @NonNull
    public String toString() {
        return (this.getPacketType() + "|" + this.getNodeID() + "|" + Double.toString(this.getQueueWaitingTime()) + "|" + Double.toString(this.getAvailableMemory()) + "|" +
                Double.toString(this.getAvailableBatteryPower()) + "|" + this.getDestAddress());
    }
}
