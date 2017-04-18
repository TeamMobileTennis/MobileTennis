package de.a_berisha.testp2pnetwork.connection;

import android.content.BroadcastReceiver;
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

import de.a_berisha.testp2pnetwork.connection.Client.Client;
import de.a_berisha.testp2pnetwork.connection.Server.GameLobby;

/**
 * Created by Adrian Berisha on 06.04.2017.
 */

public class PeerConnection implements PeerInterface{

    private Context context;
    private ViewPeerInterface view;

    private IntentFilter intentFilter;
    private WifiP2pManager.Channel channel;                         // Channel for P2P Connections
    private WifiP2pManager manager;                                 // Wifi P2P Manager
    private BroadcastReceiver receiver;

    private WifiP2pInfo wifiInfo;
    private ArrayList<WifiP2pDevice> peerList;

    private final static String INFO = "INFO";
    private final static String ERROR = "ERROR";

    private final static int PORT = 9540;

    private String playerName = "";



    private static PeerConnection instance;

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
    private PeerConnection(Context context, ViewPeerInterface view) {
        this.context = context;
        this.view = view;

        initialize();
    }
    private PeerConnection(Context context, ViewPeerInterface view, String playerName){
        this.context = context;
        this.view = view;
        this.playerName = playerName;

        initialize();
    }

    public static PeerConnection getInstance(Context context, ViewPeerInterface view){
        if(instance == null){
            instance = new PeerConnection(context, view);
        }
        return instance;
    }
    public static PeerConnection getInstance(Context context, ViewPeerInterface view, String playerName){
        if(instance == null){
            instance = new PeerConnection(context, view, playerName);
        }
        return instance;
    }



    private void initialize(){
        manager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(context, Looper.getMainLooper(),null);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    public void setPeerList(ArrayList<WifiP2pDevice> deviceList){
        this.peerList = deviceList;
    }

    // Call that Functions on an Activity-Class in onStop and onResume
    public void registerReceiver(){
        context.registerReceiver(receiver, intentFilter);
    }
    public void unregisterReceiver(){
        context.unregisterReceiver(receiver);
    }


    public void closeConnections() throws IOException{
        unregisterReceiver();
        if(manager != null)
            manager.removeGroup(channel, null);
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
    public void getConnectionInfo() {
        manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                if (info != null) {
                    if (info.groupFormed) {
                        wifiInfo = info;
                    } else {
                        Log.d("INFO", "Group not formed");
                    }
                }
            }
        });
    }

    /*
            GETTERS and SETTERS
     */

    public void setWifiInfo(WifiP2pInfo info){
        this.wifiInfo = info;
    }
    public WifiP2pInfo getWifiInfo(){
        return this.wifiInfo;
    }

    public String getPlayerName(){
        return playerName;
    }

    public Context getContext() {
        return context;
    }

    public ViewPeerInterface getView() {
        return view;
    }

    public IntentFilter getIntentFilter() {
        return intentFilter;
    }

    public WifiP2pManager.Channel getChannel() {
        return channel;
    }

    public WifiP2pManager getManager() {
        return manager;
    }

    public BroadcastReceiver getReceiver() {
        return receiver;
    }

    public void setReceiver(BroadcastReceiver receiver) {
        this.receiver = receiver;
    }

    public int getPORT() {
        return PORT;
    }
}
