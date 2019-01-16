package com.example.samsung.smartpcsys.discoverynmonitoringmanager;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.example.samsung.smartpcsys.adapters.RoutesAdapter;
import com.example.samsung.smartpcsys.resourcepool.RoutingTable;
import com.example.samsung.smartpcsys.utils.Global;
import com.example.samsung.smartpcsys.utils.SngltonClass;
import com.example.samsung.smartpcsys.viewmodels.RTViewModel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Hassam Mughal on 2019-01-08.
 */

public class DiscoveryThread implements Runnable {

    private String TAG = "DiscoveryThread";
    private DatagramSocket socket, skt;
    private RoutesAdapter adapter;
    private String myAddr;
    private RoutingTable rtEntry = null;
    private RTViewModel rtViewModel;
    // private NodesAdapter nodesAdapter;

    private DiscoveryThread() {
    }

    public DiscoveryThread(RoutesAdapter adapter) {

        this.adapter = adapter;
    }

    /**
     * init() method
     * Creates socket object and set setting
     * finds private IP address from AP
     */
    private void init() {
        try {
            socket = new DatagramSocket(8888, InetAddress.getByName("0.0.0.0"));
            socket.setReuseAddress(true);
            socket.setBroadcast(true);
            myAddr = getLocalIpAddr();
            // rtViewModel = new RTViewModel();
            adapter = new RoutesAdapter(Global.rtEntry);
            //nodesAdapter = new NodesAdapter();
            rtEntry = new RoutingTable();
            rtEntry.setSourceAddress(myAddr);
            rtEntry.setInsertTime(getTime());
            rtEntry.setHostAddress(myAddr);
            Global.rtEntry.add(rtEntry);
            //adapter.insertRTEntry(rtEntry);
            Log.e(TAG, "Routing Table Size: " + Global.rtEntry.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * recv() method
     * receives data from packet
     * displays Log message
     */

    private void recvPacket() {

        while (true) {
            try {
                Log.e(TAG, "My IP Address: " + myAddr);
                Log.e(TAG, ">>>Ready to receive broadcast/discovery packets!");

                //Receive a packet
                byte[] recvBuf = new byte[1500];
                final DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);

                String host = packet.getAddress().getHostAddress();

                Log.e(TAG, ">>>packet received from: " + host);

                if (!host.equals(myAddr)) {
                    Log.e(TAG, ">>>Packet received; Data: " + new String(packet.getData()).trim());
                    //See if the packet holds the right command (message)
                    String message = new String(packet.getData()).trim();
                    if (Global.rtEntry.size() > 1) {
                        if (message.equals("DiscoveryPacket") && !LookupRoute(host)) {  //&& !rtEntry.getHostAddress().equals(host)
                            onDiscRcv(packet);
                        } else if (message.equals("DISCOVERY_RESPONSE") && !LookupRoute(host)) {
                            Log.e(TAG, ">>> Broadcast response from destination address: " + packet.getAddress().getHostAddress());
                            onDiscResRcv(packet);
                        }
                    } else {
                        if (message.equals("DiscoveryPacket")) {  //&& !rtEntry.getHostAddress().equals(host)
                            onDiscRcv(packet);
                            Log.e(TAG, ">>> Else Discovery Packet received from destination address: " + packet.getAddress().getHostAddress());
                        } else if (message.equals("DISCOVERY_RESPONSE")) {
                            Log.e(TAG, ">>> Else Broadcast response from destination address: " + packet.getAddress().getHostAddress());
                            onDiscResRcv(packet);
                        }
                    }
                } else {
                    Log.e(TAG, "Host IP address in packet is my IP address, so ignored");
                }
                Thread.sleep(50);
            } catch (Exception ex) {
                Logger.getLogger(TAG).log(Level.SEVERE, null, ex);
                break;
            }
        }
    }

//    private boolean isDuplicated() {
//        for (int i = 1; i < Global.rtEntry.size(); i++) {
//            Log.e(TAG, "Global List Size: " + Global.rtEntry.size() + " Host Address in isDuplicated: " + rtEntry.getHostAddress() + " Host address in table in isDuplicated: " + Global.rtEntry.get(i).getHostAddress() + " i: " + i);
//            if (Global.rtEntry.get(i).getHostAddress().equals(rtEntry.getHostAddress())) {
//                return true;
//            }
//        }
//        return false;
//    }

    private void onDiscRcv(DatagramPacket packet) {
        String hostAddr = packet.getAddress().getHostAddress();
        InetAddress hostAddress = null;
        try {
            hostAddress = InetAddress.getByName(packet.getAddress().getHostAddress());
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
        final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, hostAddress, 8888);
        try {
            socket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(TAG, ">>>Sent packet to: " + sendPacket.getAddress().getHostAddress());
    }


    private void onDiscResRcv(DatagramPacket packet) {

        String hostAddr = packet.getAddress().getHostAddress();


        RoutingTable rtEntry2 = new RoutingTable();
        rtEntry2.setSourceAddress(myAddr);
        rtEntry2.setInsertTime(getTime());
        rtEntry2.setHostAddress(hostAddr);
        adapter.insertRTEntry(rtEntry2);
        Log.e(TAG, "onDiscResRcv: Route inserted to adapter");

    }

    private boolean LookupRoute(String hostAddress) {
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

    /**
     * Override method for DiscoveryThread
     */
    @Override
    public void run() {
        init();
        //new SendDiscoveryPacketThread().start();
        new RecvThread().start();
        new SendThread().start();
    }

    public static DiscoveryThread getInstance() {
        return DiscoveryThread.DiscoveryThreadHolder.INSTANCE;
    }

    private static class DiscoveryThreadHolder {

        private static final DiscoveryThread INSTANCE = new DiscoveryThread();
    }

    private InetAddress getBroadcastAddress() throws IOException {
        // handle null somehow
        WifiManager mWifi = (WifiManager) SngltonClass.get().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = mWifi.getDhcpInfo();
        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    /**
     * @return private IP address in device or null
     */
    private String getLocalIpAddr() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        //routingTable.setId(DeviceID.getID());
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private class SendThread extends Thread {

        @Override
        public void run() {
            //Open a random port to send the package
            try {
                skt = new DatagramSocket();
                skt.setBroadcast(true);
                //socket.setReuseAddress(true);

                byte[] sendData = "DiscoveryPacket".getBytes();

                //Try the 255.255.255.255 first
//                try {
//                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,InetAddress.getByName("255.255.255.255"),8888);
//                    skt.send(sendPacket);
//                    Log.e(TAG, ">>> Discovery Packet Broadcasted");
//
//                } catch (Exception e) {
//                    Log.e(TAG,"Fail to send packet");
//                }

                // Broadcast the message over all the network interfaces
                Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {

                    final NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

                    if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                        continue; // Don't want to broadcast to the loopback interface
                    }

                    for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                        final InetAddress broadcast = interfaceAddress.getBroadcast();
                        if (broadcast == null) {
                            continue;
                        }

                        // Send the broadcast packet!
                        try {
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
                            skt.send(sendPacket);
                            Log.e(TAG, ">>> Discovery Packet Broadcasted");
                            Thread.sleep(5000);
                        } catch (Exception ignored) {
                        }

                        Log.e(TAG, ">>> Broadcast packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e(TAG, ">>> Done looping over all network interfaces. Now waiting for a reply!");
        }

    }

    private class SendDiscoveryPacketThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    String msg = "DiscoveryPacket";
                    byte[] uf = msg.getBytes();
                    DatagramPacket pkt = new DatagramPacket(uf, uf.length);
                    pkt.setAddress(getBroadcastAddress());
                    pkt.setPort(8888);
                    socket.send(pkt);
                    Thread.sleep(10000); // period time for sending, 5sec
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    private class RecvThread extends Thread {
        @Override
        public void run() {
            recvPacket();
        }
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

    public String getTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
        String strDate = "Current Time : " + mdformat.format(calendar.getTime());
        return strDate;
    }


}
