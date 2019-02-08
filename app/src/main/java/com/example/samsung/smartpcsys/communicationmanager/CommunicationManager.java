package com.example.samsung.smartpcsys.communicationmanager;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;

import com.example.samsung.smartpcsys.adapters.RoutesAdapter;
import com.example.samsung.smartpcsys.discoverynmonitoringmanager.DiscoveryAndMonitoringManager;
import com.example.samsung.smartpcsys.packets.MDIRMPacket;
import com.example.samsung.smartpcsys.packets.MIMPacket;
import com.example.samsung.smartpcsys.packets.MIUMPacket;
import com.example.samsung.smartpcsys.packets.NIMPacket;
import com.example.samsung.smartpcsys.packets.NIRMPacket;
import com.example.samsung.smartpcsys.packets.NIUMPacket;
import com.example.samsung.smartpcsys.packets.TIMPacket;
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
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static com.example.samsung.smartpcsys.discoverynmonitoringmanager.DiscoveryAndMonitoringManager.LookupNode;
import static com.example.samsung.smartpcsys.discoverynmonitoringmanager.DiscoveryAndMonitoringManager.LookupRoute;
import static com.example.samsung.smartpcsys.discoverynmonitoringmanager.DiscoveryAndMonitoringManager.compareTime;
import static com.example.samsung.smartpcsys.discoverynmonitoringmanager.DiscoveryAndMonitoringManager.onNIMRcv;
import static com.example.samsung.smartpcsys.discoverynmonitoringmanager.DiscoveryAndMonitoringManager.onNIRMRcv;
import static com.example.samsung.smartpcsys.discoverynmonitoringmanager.DiscoveryAndMonitoringManager.onNIUMRcv;
import static com.example.samsung.smartpcsys.discoverynmonitoringmanager.DiscoveryAndMonitoringManager.updateNodeInfo;
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
                Log.e(TAG, ">>>Ready to receive packets!");

                //Receive a packet
                byte[] recvBuf = new byte[1500];
                final DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);

                socket.receive(packet);

                String host = packet.getAddress().getHostAddress();
                InetAddress hostAddress = null;
                try {
                    hostAddress = InetAddress.getByName(packet.getAddress().getHostAddress());
                    Log.e(TAG, "Host Address: " + hostAddress);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                Log.e(TAG, ">>> Packet received from: " + host);

                String message = new String(packet.getData()).trim();
                String[] msg = message.split(Pattern.quote("|"));
                int pktType = Integer.parseInt(msg[0]);
                if (pktType == 1) {
                    Log.e(TAG, "NIM Packet is Received!");
                }
                if (pktType == 2) {
                    Log.e(TAG, "NIRM Packet is Received!");
                }
                if (pktType == 3) {
                    Log.e(TAG, "NIUM Packet is Received!");
                }
                if (pktType == 4) {
                    Log.e(TAG, "TIM Packet is Received!");
                }

                if (!host.equals(myAddr)) {
                    //See if the packet holds the right command (message)


                    Log.e(TAG, ">>>Packet received; Data: " + message);
                    if (Global.rtEntry.size() > 1) {

                        if (pktType == 1 && !LookupRoute(host)) {
                            onNIMRcv(host, hostAddress);
                        } else if (pktType == 1 && LookupRoute(host)) {
                            updateTime(host);
                            compareTime();
                        }

                        if (pktType == 2 && !LookupRoute(host)) {
                            Log.e(TAG, ">>> NIRM Packet received from destination address: " + packet.getAddress().getHostAddress());
                            onNIRMRcv(host, hostAddress);
                        } else if (pktType == 2 && LookupRoute(host)) {
                            updateTime(host);
                            compareTime();
                        }

                        if (pktType == 3 && !LookupNode(host)) {
                            Log.e(TAG, ">>> NIUM Packet received from destination address: " + packet.getAddress().getHostAddress());

                            onNIUMRcv(msg[1], host, msg[3], Integer.parseInt(msg[4]), Double.parseDouble(msg[5]), Double.parseDouble(msg[6]), msg[7], msg[8],
                                    Double.parseDouble(msg[9]), Double.parseDouble(msg[10]), msg[11], msg[12]);
                        } else if (pktType == 3 && LookupNode(host)) {
                            updateNodeInfo(host, Integer.parseInt(msg[4]), Double.parseDouble(msg[5]), Double.parseDouble(msg[6]), msg[7], msg[8]);
                        }

                    } else {
                        if (pktType == 1) {
                            onNIMRcv(host, hostAddress);
                            Log.e(TAG, ">>> Else NIM Packet received from destination address: " + packet.getAddress().getHostAddress());
                        } else if (pktType == 2) {
                            Log.e(TAG, ">>> Else NIRM Packet received from destination address: " + packet.getAddress().getHostAddress());
                            onNIRMRcv(host, hostAddress);
                        } else if (pktType == 3) {
                            Log.e(TAG, ">>> Else NIUM Packet received from destination address: " + packet.getAddress().getHostAddress());

                            onNIUMRcv(msg[1], host, msg[3], Integer.parseInt(msg[4]), Double.parseDouble(msg[5]), Double.parseDouble(msg[6]), msg[7], msg[8],
                                    Double.parseDouble(msg[9]), Double.parseDouble(msg[10]), msg[11], msg[12]);
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

    private static StringBuilder data(byte[] a) {
        if (a == null)
            return null;
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (a[i] != 0) {
            ret.append((char) a[i]);
            i++;
        }
        return ret;
    }

    public void sendPacket(Object o) {

//        if (o instanceof NIMPacket) {
//            byte[] contents = o.toString().getBytes();
//            try {
//                DatagramSocket sokt = new DatagramSocket(8888, InetAddress.getByName("0.0.0.0"));
//                DatagramPacket packet = new DatagramPacket(contents, contents.length, ((NIMPacket) o).getBroadcastAddress(), 8888);
//                sokt.send(packet);
//                Log.e(TAG, ">>>Sent NIM packet to: " + packet.getAddress().getHostAddress());
//                sokt.close();
//            } catch (UnknownHostException e) {
//                e.printStackTrace();
//            } catch (SocketException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        if (o instanceof NIRMPacket) {
            byte[] contents = o.toString().getBytes();
            try {
                DatagramSocket sokt = new DatagramSocket();
                sokt.setReuseAddress(true);
                sokt.setBroadcast(true);
                DatagramPacket packet = new DatagramPacket(contents, contents.length, ((NIRMPacket) o).getDestinationAddress(), 8888);
                sokt.send(packet);
                Log.e(TAG, ">>>Sent NIRM packet to: " + packet.getAddress().getHostAddress());
                // sokt.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (o instanceof NIUMPacket) {
            Log.e(TAG, "sendPacket Method of NIUM Packet is Called");
            byte[] contents = o.toString().getBytes();
            try {
                DatagramSocket sokt = new DatagramSocket();
                sokt.setReuseAddress(true);
                sokt.setBroadcast(true);
                DatagramPacket packet = new DatagramPacket(contents, contents.length, ((NIUMPacket) o).getHostAddress(), 8888);
                sokt.send(packet);
                Log.e(TAG, ">>>Sent NIUM packet to: " + packet.getAddress().getHostAddress());
                // sokt.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (o instanceof TIMPacket) {
            byte[] contents = o.toString().getBytes();
            try {
                DatagramSocket sokt = new DatagramSocket();
                sokt.setReuseAddress(true);
                sokt.setBroadcast(true);
                DatagramPacket packet = new DatagramPacket(contents, contents.length, ((TIMPacket) o).getDestinationIP(), 8888);
                sokt.send(packet);
                Log.e(TAG, ">>>Sent TIM packet to: " + packet.getAddress().getHostAddress());
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (o instanceof MDIRMPacket) {
            try {
                DatagramSocket sokt = new DatagramSocket();
                sokt.setReuseAddress(true);
                sokt.setBroadcast(true);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        if (o instanceof MIMPacket) {
            try {
                DatagramSocket sokt = new DatagramSocket();
                sokt.setReuseAddress(true);
                sokt.setBroadcast(true);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        if (o instanceof MIUMPacket) {
            try {
                DatagramSocket sokt = new DatagramSocket();
                sokt.setReuseAddress(true);
                sokt.setBroadcast(true);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] convertDatatoBytes(String data) {
        byte[] dataArray = data.getBytes();

        return dataArray;
    }

    private class SendDiscoveryPacketThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    NIMPacket nimPacket = new NIMPacket();
                    String androidId = Settings.Secure.getString(SngltonClass.get().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                    nimPacket.setNodeID(androidId);
                    nimPacket.setPacketType(1);
                    nimPacket.setBroadcastAddress(getBroadcastAddress());
                    nimPacket.setCCT(DiscoveryAndMonitoringManager.getMaxCPUSpeed());
                    nimPacket.setCPI(DiscoveryAndMonitoringManager.getCurrentCPUSpeed());
                    String msg = nimPacket.toString();
                    byte[] uf = msg.getBytes();
                    DatagramPacket pkt = new DatagramPacket(uf, uf.length);
                    pkt.setAddress(getBroadcastAddress());
                    pkt.setPort(8888);
                    socket.send(pkt);
                    Log.e(TAG, "Periodic NIM Packet Sent!");
                    Thread.sleep(10000); // period time for sending, 5sec
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
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

    public InetAddress getBroadcastAddress() throws IOException {
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
                            NIMPacket nimPacket = new NIMPacket();
                            String androidId = Settings.Secure.getString(SngltonClass.get().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                            nimPacket.setNodeID(androidId);
                            nimPacket.setPacketType(1);
                            nimPacket.setBroadcastAddress(broadcast);
                            nimPacket.setCCT(DiscoveryAndMonitoringManager.getMaxCPUSpeed());
                            nimPacket.setCPI(DiscoveryAndMonitoringManager.getCurrentCPUSpeed());
                            String msg = nimPacket.toString();
                            byte[] sendData = msg.getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
                            skt.send(sendPacket);
                            Log.e(TAG, ">>> NIM Packet Broadcasted");
                            Thread.sleep(15000);
                        } catch (Exception ignored) {
                        }

                        Log.e(TAG, ">>> NIM packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e(TAG, ">>> Done looping over all network interfaces. Now waiting for a reply!");
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
//    public void sendDiscoveryPacket(byte[] contents) {
//        final Handler mHandler = new Handler(Looper.getMainLooper());
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        Thread.sleep(5000); // period time for sending, 5sec
//
//                        mHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                DatagramSocket sokt = null;
//                                try {
//                                    sokt = new DatagramSocket(8888, InetAddress.getByName("0.0.0.0"));
//                                    DatagramPacket pkt = new DatagramPacket(contents, contents.length);
//                                    pkt.setPort(8888);
//                                    pkt.setAddress(getBroadcastAddress());
//                                    sokt.send(pkt);
//                                    Log.e(TAG, "Broadcast Packet Sent!");
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//    }
