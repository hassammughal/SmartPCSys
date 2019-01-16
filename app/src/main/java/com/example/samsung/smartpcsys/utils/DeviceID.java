package com.example.samsung.smartpcsys.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class DeviceID {

    private final static AtomicInteger c = new AtomicInteger(0);

    public static int getID() {
        return c.incrementAndGet();
    }
}
