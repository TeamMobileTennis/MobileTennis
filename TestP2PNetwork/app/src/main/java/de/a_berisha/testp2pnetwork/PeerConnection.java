package de.a_berisha.testp2pnetwork;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import static de.a_berisha.testp2pnetwork.Constants.*;
import static de.a_berisha.testp2pnetwork.Constants.CMD.*;

/**
 * Created by Adrian Berisha on 06.04.2017.
 */

public class PeerConnection implements PeerInterface{

    private Context context;
    private ViewPeerInterface view;

    private IntentFilter intentFilter;
    private WifiP2pManager.Channel channel;                         // Channel for P2P Connections
    private WifiP2pManager manager;                                 // Wifi P2P Manager
    private Receiver receiver;                                      // BroadcastReceiver

    private WifiP2pInfo wifiInfo;
    private ArrayList<WifiP2pDevice> peerList;

    private final static String INFO = "INFO";
    private final static String ERROR = "ERROR";

    private final static int PORT = 9540;

    private String playerName;

    private GameLobby lobby;    // Need only the Lobby-Creator
    private Client client;      // Need only the Client

    private GetInformation getInfo;

    /**
     * Need to call registerReceiver at onResume() and if you want call unregisterReceiver at
     * onPause() to save battery life.
     *
     * startLobby() to create a Lobby on this device
     * getInformation() to get all Information from lobby's around you
     * sendMessage() to send a Message if a connection is up
     *
     * @param context   A Android Context
     * @param view      A View which implements methods to pass any information's
     */
    public PeerConnection(Context context, ViewPeerInterface view, String playerName){
        this.context = context;
        this.view = view;
        this.playerName = playerName;

        manager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(context, Looper.getMainLooper(),null);
        receiver = new Receiver(context, manager, channel, this, view);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }



    public void startLobby(String lobbyName){
        try {
            WifiManager wifiManager  = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            lobby = new GameLobby(lobbyName, PORT, view, wifiManager.getConnectionInfo().getMacAddress());
            startPeerDiscover();
            Log.d("INFO","Created Lobby");

        }catch (IOException e){
            Log.d(ERROR, "Error at creating Lobby: "+ e.getMessage());
        }
    }

    public void searchLobby(){
        startPeerDiscover();
        getInformation();
        /*client = new Client(wifiInfo, PORT, view, playerName);
        for (WifiP2pDevice dev : peerList) {
            connect(dev);
        }*/

    }
    public void endSearching(){
        getInfo.endSearching();
    }

    public void getInformation(){

        getInfo = GetInformation.getInstance(peerList, manager, channel, view, PORT);
        if(!getInfo.isAlive())
            getInfo.start();
        getInfo.updatePeerList(peerList);
    }

    public void setPeerList(ArrayList<WifiP2pDevice> deviceList){
        this.peerList = deviceList;
        if(getInfo != null){
            getInfo.updatePeerList(peerList);
        }
    }

    // Call that Functions on an Activity-Class in onStop and onResume
    public void registerReceiver(){
        context.registerReceiver(receiver, intentFilter);
    }
    public void unregisterReceiver(){
        context.unregisterReceiver(receiver);
    }

    public boolean sendMessage(String message){
        if(lobby != null && wifiInfo != null)
            lobby.sendMessage(message);
        else if(client != null && wifiInfo != null)
            client.sendMessage(message);
        else
            return false;
        return true;
    }

    public void connectAsGameClient(String deviceAddress){
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = deviceAddress;
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        if(info != null){
                            if(info.groupFormed){
                                wifiInfo = info;
                                client = new Client(wifiInfo, PORT, view, playerName);
                                client.start();
                                client.sendMessage(Messages.getDataStr(CONN, NAME, playerName));
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailure(int reason) {
                Log.d("ERROR", "Connection failed.");
            }
        });
    }

    @Override
    public void connect(WifiP2pDevice dev) {

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = dev.deviceAddress;
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {
                Log.d(ERROR, "Connection failed");
            }
        });
    }

    @Override
    public void disconnect() {
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {
                Log.d(ERROR,"Remove group failed.");
            }
        });
    }

    @Override
    public void startPeerDiscover(){
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {
                Log.d(ERROR, "Discover Peers failed");
            }
        });
    }

    @Override
    public void getConnectionInfo(){
        manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                if(info != null){
                    if(info.groupFormed){
                        wifiInfo = info;
                    }
                }
            }
        });
    }

}
