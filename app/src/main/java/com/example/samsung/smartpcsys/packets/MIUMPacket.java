package com.example.samsung.smartpcsys.packets;

import java.net.InetAddress;
import java.util.Date;

public class MIUMPacket {
    private int messageType;
    private int sourceNodeID;
    private int memberID;
    private Date queueWaitingTime;
    private double availableMemory;
    private double availableBatteryPower;
    private int destinationNodeID;
    private InetAddress broadcastAddress;
}
