package com.example.samsung.smartpcsys.packets;

import android.support.annotation.NonNull;

import java.net.InetAddress;
import java.util.Arrays;

public class TIMPacket {
    private int packetType;
    private byte[] data;
    private int taskID;
    private InetAddress destinationIP;

    public TIMPacket(int packetType, int taskID, byte[] data, InetAddress destinationIP) {
        this.packetType = packetType;
        this.data = data;
        this.taskID = taskID;
        this.destinationIP = destinationIP;
    }

    public int getPacketType() {
        return packetType;
    }

    public void setPacketType(int packetType) {
        this.packetType = packetType;
    }


    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public InetAddress getDestinationIP() {
        return destinationIP;
    }

    public void setDestinationIP(InetAddress destinationIP) {
        this.destinationIP = destinationIP;
    }

    @Override
    @NonNull
    public String toString() {
        return (this.getPacketType() + "|" + this.getTaskID() + "|" + Arrays.toString(this.getData()) + "|" + this.getDestinationIP()); //this.getNodeID() + "|"
    }
}
