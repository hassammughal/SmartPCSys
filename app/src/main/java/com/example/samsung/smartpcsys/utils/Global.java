package com.example.samsung.smartpcsys.utils;

import com.example.samsung.smartpcsys.Packets.NIMPacket;
import com.example.samsung.smartpcsys.Packets.NIRMPacket;
import com.example.samsung.smartpcsys.Packets.NIUMPacket;
import com.example.samsung.smartpcsys.resourcepool.RoutingTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Global {
    public static List<RoutingTable> rtEntry = Collections.synchronizedList(new ArrayList<RoutingTable>());
    public static List<NIMPacket> nimPackets = Collections.synchronizedList(new ArrayList<NIMPacket>());
    public static List<NIRMPacket> nirmPackets = Collections.synchronizedList(new ArrayList<NIRMPacket>());
    public static List<NIUMPacket> niumPackets = Collections.synchronizedList(new ArrayList<NIUMPacket>());
}
