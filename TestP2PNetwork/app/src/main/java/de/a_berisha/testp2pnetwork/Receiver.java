package de.a_berisha.testp2pnetwork;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Adrian Berisha on 28.03.2017.
 */

public class Receiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private MainActivity activity;


    public Receiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity){
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        activity.logAll("onReceive()-Action: " + action);

        if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            if(manager != null) {
                manager.requestPeers(channel, peerList);
                activity.logAll("Request Peers List");
            }


        }else if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
            int wifi_state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if( wifi_state == WifiP2pManager.WIFI_P2P_STATE_DISABLED){
                activity.logAll("Wifi disabled, try to enable it.");
                wifiManager.setWifiEnabled(true);
            }
            /*
            NetworkInfo netInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if(netInfo.isConnected()){
                manager.requestConnectionInfo(channel, infoListener);
            }
            */
        }else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

        }else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            activity.logAll("The P2P Connection changed.");
        }

    }

    private WifiP2pManager.PeerListListener peerList = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
            Collection<WifiP2pDevice> list = peers.getDeviceList();

            activity.logAll("Found " + list.size() + " Devices.");
            if(list.size()>0) {
                WifiP2pDevice dev = list.iterator().next();

                while (list.iterator().hasNext()) {
                    activity.logAll("Dev-Name: " + dev.deviceName + " Dev-Adr: " + dev.deviceAddress);
                    dev = list.iterator().next();
                }
                activity.logAll("Dev-Name: " + dev.deviceName + " Dev-Adr: " + dev.deviceAddress);
                activity.connect(dev);
            }
            else {
                activity.logAll("No Devices found.");
            }
        }
    };

}
