package com.example.samsung.smartpcsys.communicationmanager;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.example.samsung.smartpcsys.adapters.RoutesAdapter;
import com.example.samsung.smartpcsys.discoverynmonitoringmanager.DiscoveryAndMonitoringManager;
import com.example.samsung.smartpcsys.resourcepool.RoutingTable;
import com.example.samsung.smartpcsys.utils.Global;
import com.example.samsung.smartpcsys.utils.SngltonClass;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.example.samsung.smartpcsys.discoverynmonitoringmanager.DiscoveryAndMonitoringManager.LookupRoute;
import static com.example.samsung.smartpcsys.discoverynmonitoringmanager.DiscoveryAndMonitoringManager.compareTime;
import static com.example.samsung.smartpcsys.discoverynmonitoringmanager.DiscoveryAndMonitoringManager.onDiscResRcv;
import static com.example.samsung.smartpcsys.discoverynmonitoringmanager.DiscoveryAndMonitoringManager.updateTime;

/**
 * Created by Hassam Mughal on 2019-01-08.
 */

public class CommunicationManager implements Runnable {

    private String TAG = "CommunicationManager";
    private DatagramSocket socket, skt;
    private RoutesAdapter adapter;
    private String myAddr;
    private RoutingTable rtEntry = null;
    private DiscoveryAndMonitoringManager discoveryAndMonitoringManager;

    public CommunicationManager() {
    }

    public CommunicationManager(RoutesAdapter adapter) {

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
            discoveryAndMonitoringManager = new DiscoveryAndMonitoringManager();

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

                        if (message.equals("DiscoveryPacket") && !LookupRoute(host)) {
                            DiscoveryAndMonitoringManager.onDiscRcv(packet);
                        } else if (message.equals("DiscoveryPacket") && LookupRoute(host)) {
                            updateTime(host);
                            compareTime();
                        }

                        if (message.equals("DISCOVERY_RESPONSE") && !LookupRoute(host)) {
                            Log.e(TAG, ">>> Broadcast response from destination address: " + packet.getAddress().getHostAddress());
                            onDiscResRcv(packet);
                        } else if (message.equals("DISCOVERY_RESPONSE") && LookupRoute(host)) {
                            updateTime(host);
                            compareTime();
                        }

                    } else {
                        if (message.equals("DiscoveryPacket")) {
                            DiscoveryAndMonitoringManager.onDiscRcv(packet);
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

    public void sendPacket(byte[] contents, InetAddress hostAddress) {
        try {
            DatagramSocket sokt = new DatagramSocket(8888, InetAddress.getByName("0.0.0.0"));
            DatagramPacket packet = new DatagramPacket(contents, contents.length, hostAddress, 8888);
            sokt.send(packet);
            Log.e(TAG, ">>>Sent packet to: " + packet.getAddress().getHostAddress());
            sokt.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Override method for CommunicationManager
     */
    @Override
    public void run() {
        init();
        discoveryAndMonitoringManager.init();
        new SendDiscoveryPacketThread().start();
        new RecvThread().start();
        new SendThread().start();
    }

    public static CommunicationManager getInstance() {
        return CommunicationManager.DiscoveryThreadHolder.INSTANCE;
    }

    private static class DiscoveryThreadHolder {
        private static final CommunicationManager INSTANCE = new CommunicationManager();
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
                    Thread.sleep(5000); // period time for sending, 5sec
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

    /**
     * @return private IP address in device or null
     */
    public String getLocalIpAddr() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}