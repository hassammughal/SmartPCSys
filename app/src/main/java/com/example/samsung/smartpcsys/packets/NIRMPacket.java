package com.example.samsung.smartpcsys.packets;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.net.InetAddress;

public class NIRMPacket implements Serializable {
    private int packetType;
    private String sourceAddress;
    private InetAddress hostAddress;

    public int getPacketType() {
        return packetType;
    }

    public void setPacketType(int packetType) {
        this.packetType = packetType;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public InetAddress getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(InetAddress hostAddress) {
        this.hostAddress = hostAddress;
    }

    @Override
    @NonNull
    public String toString() {
        return (this.getPacketType() + "|" + this.getSourceAddress() + "|" + this.getHostAddress());
    }
}
