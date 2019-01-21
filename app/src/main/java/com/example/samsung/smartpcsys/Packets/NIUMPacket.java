package com.example.samsung.smartpcsys.Packets;

import java.net.InetAddress;
import java.util.Date;

public class NIUMPacket {
   private int messageType;
   private int nodeID;
   private Date queueWaitingTime;
   private double availableMemory;
   private double availableBatteryPower;
   private int destinationNodeID;
   private InetAddress broadcastAddress;

}
