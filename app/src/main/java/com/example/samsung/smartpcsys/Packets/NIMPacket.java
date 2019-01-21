package com.example.samsung.smartpcsys.Packets;

import android.support.annotation.NonNull;

public class NIMPacket {
    private int messageType;
    private int nodeID;
    private double CPI;
    private double CCT;
    private String broadcastAddress;

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public int getNodeID() {
        return nodeID;
    }

    public void setNodeID(int nodeID) {
        this.nodeID = nodeID;
    }

    public double getCPI() {
        return CPI;
    }

    public void setCPI(double CPI) {
        this.CPI = CPI;
    }

    public double getCCT() {
        return CCT;
    }

    public void setCCT(double CCT) {
        this.CCT = CCT;
    }

    public String getBroadcastAddress() {
        return broadcastAddress;
    }

    public void setBroadcastAddress(String broadcastAddress) {
        this.broadcastAddress = broadcastAddress;
    }

    @Override
    @NonNull
    public String toString() {
        return ("Packet Type: " + this.getMessageType() + ", Node ID: " + this.getNodeID() + ", CPI: " + this.getCPI() + ", CTT:" + this.getCCT() + ", Broadcast Address: " + this.broadcastAddress);
    }
}
