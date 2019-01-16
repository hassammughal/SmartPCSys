package com.example.samsung.smartpcsys.interfaces;

import com.example.samsung.smartpcsys.resourcepool.RoutingTable;

import java.util.List;

public interface AsyncResult {
    void asyncFinished(List<RoutingTable> results);
}
