package com.example.samsung.smartpcsys.packets;

import android.support.annotation.NonNull;

import java.net.InetAddress;

public class TIRMPacket {
    private int packetType;
    private InetAddress sourceIP;
    private String result;

    public TIRMPacket(int packetType, InetAddress sourceIP, String result) {
        this.packetType = packetType;
        this.sourceIP = sourceIP;
        this.result = result;
    }

    public int getPacketType() {
        return packetType;
    }

    public void setPacketType(int packetType) {
        this.packetType = packetType;
    }

    public InetAddress getSourceIP() {
        return sourceIP;
    }

    public void setSourceIP(InetAddress sourceIP) {
        this.sourceIP = sourceIP;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    @NonNull
    public String toString() {
        return (this.getPacketType() + "|" + this.getSourceIP() + "|" + this.getResult());
    }
}
