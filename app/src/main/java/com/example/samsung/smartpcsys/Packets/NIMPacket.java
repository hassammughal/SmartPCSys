package com.example.samsung.smartpcsys.Packets;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.net.InetAddress;

public class NIMPacket implements Serializable {
    private int packetType;
    private String nodeID;
    private double CPI;
    private double CCT;
    private InetAddress broadcastAddress;

//    public NIMPacket(int packetType, String nodeID, double CPI, double CCT, InetAddress broadcastAddress) {
//        this.packetType = packetType;
//        this.nodeID = nodeID;
//        this.CPI = CPI;
//        this.CCT = CCT;
//        this.broadcastAddress = broadcastAddress;
//    }

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

    public InetAddress getBroadcastAddress() {
        return broadcastAddress;
    }

    public void setBroadcastAddress(InetAddress broadcastAddress) {
        this.broadcastAddress = broadcastAddress;
    }

    @Override
    @NonNull
    public String toString() {
        return (this.getPacketType() + "|" + this.getNodeID() + "|" + Double.toString(this.getCPI()) + "|" + Double.toString(this.getCCT()) + "|" + this.getBroadcastAddress());
    }
}
