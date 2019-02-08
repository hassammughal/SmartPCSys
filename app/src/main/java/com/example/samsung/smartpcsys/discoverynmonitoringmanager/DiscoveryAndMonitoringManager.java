package com.example.samsung.smartpcsys.discoverynmonitoringmanager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.example.samsung.smartpcsys.Packets.NIMPacket;
import com.example.samsung.smartpcsys.Packets.NIRMPacket;
import com.example.samsung.smartpcsys.Packets.NIUMPacket;
import com.example.samsung.smartpcsys.adapters.RoutesAdapter;
import com.example.samsung.smartpcsys.communicationmanager.CommunicationManager;
import com.example.samsung.smartpcsys.resourcepool.Node;
import com.example.samsung.smartpcsys.resourcepool.RoutingTable;
import com.example.samsung.smartpcsys.taskqueue.TaskQueue;
import com.example.samsung.smartpcsys.utils.Global;
import com.example.samsung.smartpcsys.utils.SngltonClass;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.ACTIVITY_SERVICE;

public class DiscoveryAndMonitoringManager {
    private static String TAG = "DiscoveryandMonitoringManager";
    private static RoutesAdapter adapter;
    private static String myAddr;
    private RoutingTable rtEntry = null;
    private static CommunicationManager communicationManager;
    private static String androidId = null;
    private static NIMPacket nimPacket;
    private static NIRMPacket nirmPacket;
    private static NIUMPacket niumPacket;
    public static final ArrayList<Node> nodesList = new ArrayList<>();


    public void init() {
        androidId = Settings.Secure.getString(SngltonClass.get().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        communicationManager = new CommunicationManager();
        adapter = new RoutesAdapter(Global.rtEntry);
        myAddr = communicationManager.getLocalIpAddr();
        nimPacket = new NIMPacket();
        nirmPacket = new NIRMPacket();
        niumPacket = new NIUMPacket();
        rtEntry = new RoutingTable();
        rtEntry.setSourceAddress(myAddr);
        rtEntry.setInsertTime(getTime());
        rtEntry.setHostAddress(myAddr);
        Global.rtEntry.add(rtEntry);
        Log.e(TAG, "Routing Table Size: " + Global.rtEntry.size());
        getCpuInfo();
        getMemoryInfo();
        getInfo();
        getBatteryCapacity(SngltonClass.get().getApplicationContext());
        setNodeInfo();
        Log.e(TAG, "Total Battery: " + getTotalBatteryCapacity(SngltonClass.get().getApplicationContext()) + " mAh");
        Log.e(TAG, "Available Internal Memory: " + formatSize(getAvailableInternalMemorySize()));
        Log.e(TAG, "Total Internal Memory: " + formatSize(getTotalInternalMemorySize()));
        Log.e(TAG, "Total Internal Memory Used: " + formatSize(getInternalMemInfo()));
        Log.e(TAG, "Total RAM: " + formatSize(totalRamMemorySize()));
        Log.e(TAG, "Available RAM: " + formatSize((long) getAvailMemory()));
//          try {
//            fillNIMPacket(1,androidId,getCurrentCPUSpeed(),getMaxCPUSpeed(),communicationManager.getBroadcastAddress());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static String getMacAddress() {
        WifiManager wifiManager = (WifiManager) SngltonClass.get().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        return wInfo.getMacAddress();
    }

    public void setNodeInfo() {

        Node node = new Node(androidId, myAddr, getMacAddress(), TaskQueue.taskQueue.size(), getBatteryCapacity(SngltonClass.get().getApplicationContext()), getCurrentCPUSpeed(),
                formatSize((long) getAvailMemory()), formatSize(getAvailableInternalMemorySize()), getTotalBatteryCapacity(SngltonClass.get().getApplicationContext()), getMaxCPUSpeed(),
                formatSize(totalRamMemorySize()), formatSize(getTotalInternalMemorySize()));
        nodesList.add(node);
    }

    public static void updateTime(String hostAddr) {
        int index = getIndex(hostAddr);
        Log.e(TAG, "Index: " + index);
        Global.rtEntry.get(index).setInsertTime(getTime());
        adapter.notifyDataSetChanged();
    }

    public static void updateNodeInfo(String hostAddr, int queueSize, double currBattery, double currCPUSpeed, String availableRAM, String availableMemory) {
        int index = getNodeIndex(hostAddr);
        Log.e(TAG, "Node Index:" + index);
        nodesList.get(index).setQueueSize(queueSize);
        nodesList.get(index).setCurrentBattery(currBattery);
        nodesList.get(index).setCurrentCPUSpeed(currCPUSpeed);
        nodesList.get(index).setCurrentRAM(availableRAM);
        nodesList.get(index).setCurrentMemory(availableMemory);
    }

    public static void onNIMRcv(String host, InetAddress hostAddress) {

        RoutingTable rtEntry1 = new RoutingTable();
        rtEntry1.setSourceAddress(myAddr);
        rtEntry1.setInsertTime(getTime());
        rtEntry1.setHostAddress(host);
        adapter.insertRTEntry(rtEntry1);
        Log.e(TAG, "onNIMRcv: Route inserted to adapter");

        fillNIRMPacket(2, myAddr, hostAddress);
        Log.e(TAG, "onNIMRcv: NIRM Packet sent to Communication Manager, NIRM packet contains: " + nirmPacket.getPacketType());
        communicationManager.sendPacket(nirmPacket);
    }

    public static void onNIRMRcv(String hostAddr, InetAddress hostAddress) {

        RoutingTable rtEntry2 = new RoutingTable();
        rtEntry2.setSourceAddress(myAddr);
        rtEntry2.setInsertTime(getTime());
        rtEntry2.setHostAddress(hostAddr);
        adapter.insertRTEntry(rtEntry2);
        Log.e(TAG, "onNIRMRcv: Route inserted to adapter");
        fillNIUMPacket(3, androidId, hostAddress, getMacAddress(), TaskQueue.taskQueue.size(), getBatteryCapacity(SngltonClass.get().getApplicationContext()), getCurrentCPUSpeed(),
                formatSize((long) getAvailMemory()), formatSize(getAvailableInternalMemorySize()), getTotalBatteryCapacity(SngltonClass.get().getApplicationContext()), getMaxCPUSpeed(),
                formatSize(totalRamMemorySize()), formatSize(getTotalInternalMemorySize()));
        communicationManager.sendPacket(niumPacket);
        Log.e(TAG, "onNIRMRcv: NIUM Packet sent to Communication Manager, NIUM packet contains: " + niumPacket.getPacketType());
    }


    public static void onNIUMRcv(String nodeID, String hostAddress, String hostMACAddress, int queueSize, double currentBattery, double currentCPUSpeed, String currentRAM, String currentMemory,
                                 double totalBattery, double totalCPUSpeed, String totalRAM, String totalMemory) {
        Node node = new Node(nodeID, hostAddress, hostMACAddress, queueSize, currentBattery, currentCPUSpeed, currentRAM, currentMemory, totalBattery, totalCPUSpeed, totalRAM, totalMemory);
        nodesList.add(node);
    }

    public static boolean LookupNode(String hostAddress) {
        if (nodesList.size() > 0) {
            synchronized (nodesList) {
                for (Node aNodesList : nodesList) {
                    if (aNodesList.getIpAddress().equals(hostAddress)) {
                        Log.e(TAG, "NIUM Packet Received from same address");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean LookupRoute(String hostAddress) {
        if (Global.rtEntry.size() > 0) {
            synchronized (Global.rtEntry) {
                for (RoutingTable aRtEntry : Global.rtEntry) {
                    if (aRtEntry.getHostAddress().equals(hostAddress)) {
                        Log.e(TAG, "NIM Packet Received from same address");
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

    private static int getNodeIndex(String hostAddress) {
        int index = 0;
        for (int i = 1; i < nodesList.size(); i++) {
            if (nodesList.get(i).getIpAddress().equals(hostAddress)) {
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

    public static void fillNIMPacket(int type, String id, double cpi, double cct, InetAddress broadcastAddress) {
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

    public static void fillNIUMPacket(int type, String nodeID, InetAddress hostAddress, String hostMACAddress, int queueSize, double availableBattery, double currCPUSpeed, String currentRAM,
                                      String availableMemory, double totalBattery, double totalCPUSpeed, String totalRAM, String totalMemory) {
        niumPacket.setPacketType(type);
        niumPacket.setNodeID(nodeID);
        niumPacket.setHostAddress(hostAddress);
        niumPacket.setMacAddress(hostMACAddress);
        niumPacket.setQueueSize(queueSize);
        niumPacket.setAvailableBatteryPower(availableBattery);
        niumPacket.setCurrCPUSpeed(currCPUSpeed);
        niumPacket.setCurrentRAM(currentRAM);
        niumPacket.setAvailableMemory(availableMemory);
        niumPacket.setTotalBattery(totalBattery);
        niumPacket.setTotalCPUSpeed(totalCPUSpeed);
        niumPacket.setTotalRAM(totalRAM);
        niumPacket.setTotalMemory(totalMemory);

        Global.niumPackets.add(niumPacket);
    }

    /**
     * Methods for calculating memory
     *
     * @return
     */
    private static double getAvailMemory() {
        return (double) Runtime.getRuntime().freeMemory();
    }

    private long getRAMInfo() {
        long totalRamValue = totalRamMemorySize();
        long freeRamValue = freeRamMemorySize();
        long usedRamValue = totalRamValue - freeRamValue;
        return usedRamValue;
    }

    private long getInternalMemInfo() {
        long totalInternalValue = getTotalInternalMemorySize();
        long freeInternalValue = getAvailableInternalMemorySize();
        long usedInternalValue = totalInternalValue - freeInternalValue;
        return usedInternalValue;
    }

    private long getExternalMemInfo() {
        long totalExternalValue = getTotalExternalMemorySize();
        long freeExternalValue = getAvailableExternalMemorySize();
        long usedExternalValue = totalExternalValue - freeExternalValue;
        return usedExternalValue;
    }

    private long freeRamMemorySize() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) SngltonClass.get().getApplicationContext().getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long availableMegs = mi.availMem / 1048576L;

        return availableMegs;
    }

    private static long totalRamMemorySize() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) SngltonClass.get().getApplicationContext().getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long availableMegs = mi.totalMem;
        return availableMegs;
    }

    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return availableBlocks * blockSize;
    }

    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return totalBlocks * blockSize;
    }

    public static long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getBlockCountLong();
            return availableBlocks * blockSize;
        } else {
            return 0;
        }
    }

    public static long getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();
            return totalBlocks * blockSize;
        } else {
            return 0;
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

    /**
     * Methods for retrieving CPU information
     *
     * @return
     */

    public static double getCurrentCPUSpeed() {
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

    public static double getMaxCPUSpeed() {
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

    public void getCpuInfo() {
        try {
            Process proc = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStream is = proc.getInputStream();

            Log.e(TAG, "------ CpuInfo " + getStringFromInputStream(is));
        } catch (IOException e) {
            Log.e(TAG, "------ getCpuInfo " + e.getMessage());
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

    private static String exCommand(String comando) throws IOException {
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


    //Method for retrieving battery information
    public static double getBatteryCapacity(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        assert batteryStatus != null;
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        double batteryPct = (level / (float) scale) * 100;
        Log.e(TAG, "Available Battery: " + batteryPct + "%");
        return batteryPct;
    }

    public static double getTotalBatteryCapacity(Context context) {
        Object mPowerProfile;
        double batteryCapacity = 0;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(context);

            batteryCapacity = (double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getBatteryCapacity")
                    .invoke(mPowerProfile);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return batteryCapacity;
    }

    public static String formatSize(long bytes) {

        final int unit = 1024;
        if (bytes < unit)
            return bytes + " B";
        double result = bytes;
        final String unitsToUse = ("K") + "MGTPE";
        int i = 0;
        final int unitsCount = unitsToUse.length();
        while (true) {
            result /= unit;
            if (result < unit)
                break;
            // check if we can go further:
            if (i == unitsCount - 1)
                break;
            ++i;
        }
        final StringBuilder sb = new StringBuilder(9);
        sb.append(String.format("%.1f ", result));
        sb.append(unitsToUse.charAt(i));
        sb.append('B');
        final String resultStr = sb.toString();
        return resultStr;
    }

    private String returnToDecimalPlaces(long values) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(values);
    }
}
//    int unit = 1024;
//        if (bytes < unit) return bytes + " B";
//        int exp = (int) (Math.log(bytes) / Math.log(unit));
//        String pre = ("KMGTPE").charAt(exp-1) + ("");
//        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
//        String[] types = {"kb", "Mb", "GB", "TB", "PB", "EB"};
//        int unit = 1024;
//        if (bytes < unit) return bytes + "bytes";
//        int exp = (int) (Math.log(bytes) / Math.log(unit));
//        String result = String.format("%.1f ", bytes / Math.pow(unit, exp)) + types[exp - 1];
//        return  result;
//            if (size <= 0)
//                return "0";
//
//            final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
//            int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
//
//            return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
//        String suffix = null;
//
//        if (size >= 1024) {
//            suffix = " KB";
//            size /= 1024;
//            if (size >= 1024 * 1024) {
//                suffix = " MB";
//                size /= 1024 * 1024;
//                if (size >= 1024 * 1024 * 1024) {
//                    suffix = " GB";
//                    size /= 1024 * 1024 * 1024;
//                }
//            }
//        }
//
//        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));
//
//        int commaOffset = resultBuffer.length() - 3;
//        while (commaOffset > 0) {
//            resultBuffer.insert(commaOffset, ',');
//            commaOffset -= 3;
//        }
//        if (suffix != null) resultBuffer.append(suffix);
//        return resultBuffer.toString();
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

