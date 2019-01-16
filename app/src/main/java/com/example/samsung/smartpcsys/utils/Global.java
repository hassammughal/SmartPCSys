package com.example.samsung.smartpcsys.utils;

import com.example.samsung.smartpcsys.resourcepool.RoutingTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Global {
    public static List<RoutingTable> rtEntry = Collections.synchronizedList(new ArrayList<RoutingTable>());
}
