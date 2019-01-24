package com.example.samsung.smartpcsys.Packets;

import java.net.InetAddress;
import java.util.Date;

public class NIUMPacket {
   private int messageType;
   private String nodeID;
   private Date queueWaitingTime;
   private double availableMemory;
   private double availableBatteryPower;
   private String destinationNodeID;
   private InetAddress broadcastAddress;

}
