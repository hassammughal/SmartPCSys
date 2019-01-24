package com.example.samsung.smartpcsys.Packets;

import android.support.annotation.NonNull;

import java.net.InetAddress;

public class NIRMPacket {
    private int packetType;
    private String sourceAddress;
    private InetAddress destinationAddress;

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

    public InetAddress getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(InetAddress destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    @Override
    @NonNull
    public String toString() {
        return ("PacketType: " + getPacketType() + ", Source Address: " + getSourceAddress() + ", Destination Address: " + getDestinationAddress());
    }
}
