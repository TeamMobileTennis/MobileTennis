package com.crazyking.mobiletennis.connection.Client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;

import com.crazyking.mobiletennis.connection.ViewPeerInterface;

import java.util.ArrayList;

/**
 * Created by Adrian Berisha on 14.04.2017.
 */

public class ClientReceiver extends BroadcastReceiver {

    private Context context;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private ClientPeerConn peer;
    private ViewPeerInterface view;

    private WifiP2pGroup wifiGroup;
    private WifiP2pInfo wifiInfo;

    public ClientReceiver(Context context, WifiP2pManager manager, WifiP2pManager.Channel channel, ClientPeerConn peer, ViewPeerInterface view) {
        super();
        this.context = context;
        this.manager = manager;
        this.channel = channel;
        this.peer = peer;
        this.view = view;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();         // Action that the Receiver received

        if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            if(manager != null) {
                manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peers) {
                        ArrayList<WifiP2pDevice> list = new ArrayList<>(peers.getDeviceList());
                        if (list.size() > 0) {
                            view.fillPeerList(list);
                            peer.setPeerList(list);
                        }
                    }
                });
            }
        }else if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
            int wifi_state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            // Try to enable Wi-Fi if it is off
            if(wifi_state == WifiP2pManager.WIFI_P2P_STATE_DISABLED){
                wifiManager.setWifiEnabled(true);
            }

        }else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if(manager != null) {
                // Get Network-Info to check if we are connected to a p2p device
                NetworkInfo netInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                if (netInfo != null) {
                    if (netInfo.isConnected()) {
                        peer.getConnectionInfo();
                    }
                }
                wifiGroup = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP);
                wifiInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);

            }
        }else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){
            if(manager != null) {
//                WifiP2pDevice info = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            }
        }


    }

    public boolean isConnected(){
        if(wifiGroup.getClientList().size() <= 0){
            return false;
        }
        return true;
    }
    public boolean isConnect(){
        if(wifiInfo != null){
            if(wifiInfo.groupFormed)
                return true;
            return false;
        }
        return false;
    }
}
