package com.crazyking.mobiletennis.connection;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Adrian Berisha on 07.04.2017.
 */

public class GetInformation extends Thread {
    private ArrayList<WifiP2pDevice> peerList;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private ViewPeerInterface view;
    private WifiP2pInfo wifiInfo;

    private int port;

    private static GetInformation instance = null;

    private boolean finish = false;
    private boolean finishSearching = false;

    private Thread sendThread = null;


    public static GetInformation getInstance(ArrayList<WifiP2pDevice> peerList, WifiP2pManager manager, WifiP2pManager.Channel channel, ViewPeerInterface view, int port){
        if(instance == null){
            instance = new GetInformation(peerList, manager, channel, view, port);
            Log.d("INFO", "Created getInformation Class");
        }
        return instance;
    }

    private GetInformation(ArrayList<WifiP2pDevice> peerList, WifiP2pManager manager, WifiP2pManager.Channel channel, ViewPeerInterface view, int port) {
        this.peerList = peerList;
        this.manager = manager;
        this.channel = channel;
        this.port = port;
        this.view = view;
    }
    public void updatePeerList(ArrayList<WifiP2pDevice> list){
        this.peerList = list;
    }
    public void endSearching(){
        this.finishSearching = true;
    }

    @Override
    public void run() {
        super.run();
        Log.d("INFO", "Start Running");

        if(peerList != null){
            Log.d("INFO","PeerList: "+peerList.toString());
        }
        while(!finishSearching){

            if(peerList != null) {

                for (WifiP2pDevice device : peerList) {
                    sendThread = null;
                    WifiP2pConfig config = new WifiP2pConfig();
                    config.deviceAddress = device.deviceAddress;
                    config.groupOwnerIntent = 0;
                    manager.removeGroup(channel, null);
                    manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                                @Override
                                public void onConnectionInfoAvailable(final WifiP2pInfo info) {
                                    wifiInfo = info;

                                    sendThread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (wifiInfo.groupFormed) {
                                                try {
                                                    if(wifiInfo.isGroupOwner) {
                                                        Log.d("INFO", "Don't work. Im the Group-Owner");
                                                        manager.removeGroup(channel,null);
                                                    }else {
                                                        final Thread t = currentThread();


                                                        Thread timeout = new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    Log.d("INFO", "Timeout Thread started. Go to sleep for 500ms");
                                                                    Thread.sleep(500);
//                                                                    t.interrupt();
                                                                    sendThread.interrupt();
                                                                    Log.d("INFO", "Thread Timeout");
                                                                }catch (InterruptedException e){
                                                                    Log.d("ERROR", e.getMessage());
                                                                }

                                                            }
                                                        });
                                                        timeout.start();

                                                        Socket socket = new Socket();
                                                        socket.setReuseAddress(true);
                                                        socket.connect(new InetSocketAddress(wifiInfo.groupOwnerAddress, port));

                                                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                                        PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

                                                        writer.println(Messages.getDataStr(Constants.CMD.GETINFO));
                                                        String message = reader.readLine();
                                                        if (Messages.getCommand(message).equals(Constants.CMD.INFO)) {
                                                            view.passInformation(new Information(message));
                                                        }
                                                        writer.println(Messages.getDataStr(Constants.CMD.CLOSE));
                                                        writer.close();
                                                        reader.close();
                                                        socket.close();
                                                        manager.removeGroup(channel, null);
                                                        finish = true;
                                                        timeout.interrupt();
                                                        Log.d("INFO", "Thread finished");

                                                    }
                                                } catch (Exception e) {
                                                    Log.d("ERROR", e.getMessage());
                                                }
                                            }else {
                                                Log.d("INFO", "Group not formed.");
                                            }
                                        }
                                    });
                                    sendThread.start();

                                }
                            });
                        }

                        @Override
                        public void onFailure(int reason) {
                            Log.d("ERROR", "Connecting failed: "+reason);
                        }
                    });
                    // Wait here for all Threads
//                    while (!finish);
                    try {
                        if (sendThread != null) {
                            if (sendThread.isAlive()) {
                                sendThread.join();
                            }
                        }
                    }catch (Exception e){
                        Log.d("ERROR",e.getMessage());
                    }

                }
            }

        }
    }
}
