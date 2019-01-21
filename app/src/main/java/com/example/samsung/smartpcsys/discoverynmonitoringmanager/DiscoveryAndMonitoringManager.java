package com.example.samsung.smartpcsys.discoverynmonitoringmanager;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.example.samsung.smartpcsys.adapters.RoutesAdapter;
import com.example.samsung.smartpcsys.communicationmanager.CommunicationManager;
import com.example.samsung.smartpcsys.resourcepool.RoutingTable;
import com.example.samsung.smartpcsys.utils.Global;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Iterator;

public class DiscoveryAndMonitoringManager {
    private static String TAG = "DiscoveryandMonitoringManager";
    private static RoutesAdapter adapter;
    private static String myAddr;
    private RoutingTable rtEntry = null;
    private static CommunicationManager communicationManager;

    public void init() {
        adapter = new RoutesAdapter(Global.rtEntry);
        communicationManager = new CommunicationManager();
        myAddr = communicationManager.getLocalIpAddr();
        rtEntry = new RoutingTable();
        rtEntry.setSourceAddress(myAddr);
        rtEntry.setInsertTime(getTime());
        rtEntry.setHostAddress(myAddr);
        Global.rtEntry.add(rtEntry);
        Log.e(TAG, "Routing Table Size: " + Global.rtEntry.size());
    }

    public static void updateTime(String hostAddr) {
        int index = getIndex(hostAddr);
        Log.e(TAG, "Index: " + index);
        Global.rtEntry.get(index).setInsertTime(getTime());
        adapter.notifyDataSetChanged();
    }

    public static void onDiscRcv(DatagramPacket packet) {
        String hostAddr = packet.getAddress().getHostAddress();
        InetAddress hostAddress = null;
        try {
            hostAddress = InetAddress.getByName(packet.getAddress().getHostAddress());
            Log.e(TAG, "Host Address: " + hostAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        RoutingTable rtEntry1 = new RoutingTable();
        rtEntry1.setSourceAddress(myAddr);
        rtEntry1.setInsertTime(getTime());
        rtEntry1.setHostAddress(hostAddr);
        adapter.insertRTEntry(rtEntry1);
        Log.e(TAG, "onDiscRcv: Route inserted to adapter");

        byte[] sendData = "DISCOVERY_RESPONSE".getBytes();
        //Send a response

        communicationManager.sendPacket(sendData, hostAddress);
    }

    public static void onDiscResRcv(DatagramPacket packet) {

        String hostAddr = packet.getAddress().getHostAddress();

        RoutingTable rtEntry2 = new RoutingTable();
        rtEntry2.setSourceAddress(myAddr);
        rtEntry2.setInsertTime(getTime());
        rtEntry2.setHostAddress(hostAddr);
        adapter.insertRTEntry(rtEntry2);
        Log.e(TAG, "onDiscResRcv: Route inserted to adapter");

    }

    public static boolean LookupRoute(String hostAddress) {
        if (Global.rtEntry.size() > 0) {
            synchronized (Global.rtEntry) {
                Iterator<RoutingTable> itr = Global.rtEntry.iterator();
                while (itr.hasNext()) {
                    if (itr.next().getHostAddress().equals(hostAddress)) {
                        Log.e(TAG, "Discovery Packet Received from same address");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void compareTime() {

        final Handler mHandler = new Handler(Looper.getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(15000);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (Global.rtEntry.size() > 1) {
                                    synchronized (Global.rtEntry) {
                                        for (int i = 1; i < Global.rtEntry.size(); i++) {
                                            String insertTime = Global.rtEntry.get(i).getInsertTime();
                                            String host = Global.rtEntry.get(i).getHostAddress();
                                            String currTime = getTime();
                                            Log.e(TAG, "Compare Time try block is called. Insert Time:" + insertTime + " Current Time: " + currTime);
                                            LocalTime t1 = LocalTime.parse(insertTime);
                                            LocalTime t2 = LocalTime.parse(currTime);
                                            Duration duration = Duration.between(t1, t2);
                                            int pos = getIndex(host);
                                            Log.e(TAG, "Duration Time: " + duration.getSeconds() + " Host Address: " + host);
                                            if (duration.getSeconds() > 15) {
                                                Log.e(TAG, "Index is: " + pos);
                                                adapter.removeRTEntry(pos);
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    private static int getIndex(String hostAddress) {
        int index = 0;
        for (int i = 1; i < Global.rtEntry.size(); i++) {
            if (Global.rtEntry.get(i).getHostAddress().equals(hostAddress)) {
                index = i;
                break;
            }
        }
        return index;
    }


    private static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    public static String getTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }
}
