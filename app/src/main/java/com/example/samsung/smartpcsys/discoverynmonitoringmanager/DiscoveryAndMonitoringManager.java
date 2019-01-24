package com.example.samsung.smartpcsys.discoverynmonitoringmanager;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.example.samsung.smartpcsys.Packets.NIMPacket;
import com.example.samsung.smartpcsys.Packets.NIRMPacket;
import com.example.samsung.smartpcsys.adapters.RoutesAdapter;
import com.example.samsung.smartpcsys.communicationmanager.CommunicationManager;
import com.example.samsung.smartpcsys.resourcepool.RoutingTable;
import com.example.samsung.smartpcsys.utils.Global;
import com.example.samsung.smartpcsys.utils.SngltonClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private static String androidId = null;
    private static NIMPacket nimPacket;
    private static NIRMPacket nirmPacket;

    public void init() {
        androidId = Settings.Secure.getString(SngltonClass.get().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        communicationManager = new CommunicationManager();
        adapter = new RoutesAdapter(Global.rtEntry);
        myAddr = communicationManager.getLocalIpAddr();
        nimPacket = new NIMPacket();
        nirmPacket = new NIRMPacket();
        rtEntry = new RoutingTable();
        rtEntry.setSourceAddress(myAddr);
        rtEntry.setInsertTime(getTime());
        rtEntry.setHostAddress(myAddr);
        Global.rtEntry.add(rtEntry);
        //fillNIMPacket(1, androidId,);
        Log.e(TAG, "Routing Table Size: " + Global.rtEntry.size());
        getCpuInfo();
        getMemoryInfo();
        getInfo();
        //discoveryPacket();
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
        try {
            fillNIMPacket(1, androidId, nimPacket.getCPI(), nimPacket.getCCT(), communicationManager.getBroadcastAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "onDiscRcv: Route inserted to adapter");

        //byte[] sendData = "DISCOVERY_RESPONSE".getBytes();
        //Send a response

//        nirmPacket.setPacketType(2);
//        nirmPacket.setSourceAddress(myAddr);
//        nirmPacket.setDestinationAddress(hostAddress);
        fillNIRMPacket(2, myAddr, hostAddress);
        communicationManager.sendPacket(nirmPacket);
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

//    private static void discoveryPacket() {
//
//        String msg = "DiscoveryPacket";
//        byte[] uf = msg.getBytes();
//        communicationManager.sendDiscoveryPacket(uf);
//
//    }

//    private class SendDiscoveryPacketThread extends Thread {
//        @Override
//        public void run() {
//            while (true) {
//                try {
//                    String msg = "DiscoveryPacket";
//                    byte[] uf = msg.getBytes();
//                    DatagramPacket pkt = new DatagramPacket(uf, uf.length);
//                    pkt.setAddress(communicationManager.getBroadcastAddress());
//                    pkt.setPort(8888);
//                    communicationManager.send(pkt);
//                    Thread.sleep(5000); // period time for sending, 5sec
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    break;
//                }
//            }
//        }
//    }


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

    public void getCpuInfo() {
        try {
            Process proc = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStream is = proc.getInputStream();

            Log.e(TAG, "------ CpuInfo " + getStringFromInputStream(is));
        } catch (IOException e) {
            Log.e(TAG, "------ getCpuInfo " + e.getMessage());
        }
    }

    public void getMemoryInfo() {
        try {
            Process proc = Runtime.getRuntime().exec("cat /proc/meminfo");
            InputStream is = proc.getInputStream();
            Log.e(TAG, "------ MemoryInfo " + getStringFromInputStream(is));
        } catch (IOException e) {
            Log.e(TAG, "------ getMemoryInfo " + e.getMessage());
        }
    }


    private static String getStringFromInputStream(InputStream is) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;

        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            Log.e(TAG, "------ getStringFromInputStream " + e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    Log.e(TAG, "------ getStringFromInputStream " + e.getMessage());
                }
            }
        }

        return sb.toString();
    }

    public static void fillNIMPacket(int type, String id, double cpi, double cct, InetAddress broadcastAddress) {
        //
        nimPacket.setNodeID(id);
        nimPacket.setPacketType(type);
        nimPacket.setCCT(cct);
        nimPacket.setCPI(cpi);
        nimPacket.setBroadcastAddress(broadcastAddress);
        Global.nimPackets.add(nimPacket);
    }

    public static void fillNIRMPacket(int type, String sourceAddress, InetAddress destAddress) {
        nirmPacket.setPacketType(type);
        nirmPacket.setSourceAddress(sourceAddress);
        nirmPacket.setDestinationAddress(destAddress);
        Global.nirmPackets.add(nirmPacket);
    }

    public double getCurrentCPUSpeed() {
        String currSpeed = null;
        try {
            currSpeed = exCommand("cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert currSpeed != null;
        double currentSpeed = Double.parseDouble(currSpeed);
        currentSpeed = currentSpeed / 1000000;
        return currentSpeed;
    }

    public double getAvailMemory() {
        double availMem = Runtime.getRuntime().freeMemory();
        return availMem;
    }

    public double getMaxCPUSpeed() {
        String maxSpeed = null;
        try {
            maxSpeed = exCommand("cat /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert maxSpeed != null;
        double maximumSpeed = Double.parseDouble(maxSpeed);
        maximumSpeed = maximumSpeed / 1000000;
        return maximumSpeed;
    }


    private void getInfo() {
        int cores = Runtime.getRuntime().availableProcessors();
//        for (int i = 0; i < cores; i++) {
        String curSpeed = null;
        String maxSpeed = null;
        String minSpeed = null;

        try {
//                curSpeed = exCommand("cat /sys/devices/system/cpu/cpu" + i + "/cpufreq/scaling_cur_freq");
//                minSpeed = exCommand("cat /sys/devices/system/cpu/cpu" + i + "/cpufreq/cpuinfo_min_freq");
//                maxSpeed = exCommand("cat /sys/devices/system/cpu/cpu" + i + "/cpufreq/cpuinfo_max_freq");
            curSpeed = exCommand("cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
            minSpeed = exCommand("cat /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq");
            maxSpeed = exCommand("cat /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert curSpeed != null;
        double currentSpeed = Double.parseDouble(curSpeed);
        assert minSpeed != null;
        double minimumSpeed = Double.parseDouble(minSpeed);
        assert maxSpeed != null;
        double maximumSpeed = Double.parseDouble(maxSpeed);
        currentSpeed = currentSpeed / 1000000;
        minimumSpeed = minimumSpeed / 1000000;
        maximumSpeed = maximumSpeed / 1000000;

        // NIMPacket nimPacket = new NIMPacket(1, DeviceID.getID(),vel,)
        Log.e(TAG, "CPU Current Speed: " + currentSpeed + "GHz");
        Log.e(TAG, "CPU Maximum Speed: " + maximumSpeed + "GHz");
        Log.e(TAG, "CPU Minimum Speed: " + minimumSpeed + "GHz");

//        }
    }

    private String exCommand(String comando) throws IOException {
        try {
            Process process = Runtime.getRuntime().exec(comando);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuilder output = new StringBuilder();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();
            process.waitFor();
            return output.toString();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

//    private String getInfo() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("abi: ").append(Build.SUPPORTED_ABIS).append("\n");
//        if (new File("/proc/cpuinfo").exists()) {
//            try {
//                BufferedReader br = new BufferedReader(new FileReader(new File("/proc/cpuinfo")));
//                String aLine;
//                while ((aLine = br.readLine()) != null) {
//                    sb.append(aLine).append("\n");
//                }
//                if (br != null) {
//                    br.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return String.valueOf(Log.e(TAG,"CPU INFO: "+sb.toString()));
//    }

}
