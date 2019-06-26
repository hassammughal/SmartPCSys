package com.example.samsung.smartpcsys.packets;

import android.support.annotation.NonNull;

import java.net.InetAddress;

public class TIMPacket {
    private int packetType;
    private String data;
    private int taskID;
    private InetAddress destinationIP;
    private String filePath;

    public TIMPacket(int packetType, int taskID, String filePath, InetAddress destinationIP, String data) {
        this.packetType = packetType;
        this.data = data;
        this.taskID = taskID;
        this.destinationIP = destinationIP;
        this.filePath = filePath;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public InetAddress getDestinationIP() {
        return destinationIP;
    }

    public void setDestinationIP(InetAddress destinationIP) {
        this.destinationIP = destinationIP;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    @NonNull
    public String toString() {
        return (this.getPacketType() + "|" + this.getTaskID() + "|" + this.getFilePath()) + "|" + this.getDestinationIP() + "|" + this.getData(); //this.getNodeID() + "|"
    }
}
