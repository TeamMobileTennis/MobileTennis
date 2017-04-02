package de.a_berisha.testp2pnetwork;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
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

        if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            if(manager != null) {

                manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peers) {
                        activity.logAll(activity.INFO,"Getting Peer List");
                        Collection<WifiP2pDevice> list = peers.getDeviceList();


                        if (list.size() > 0) {
                            activity.logAll(activity.INFO,"Found " + list.size() + " Devices.");

                            WifiP2pDevice dev = list.iterator().next();

                            /*for (WifiP2pDevice wifiP2pDevice : list) {
                                activity.logAll(activity.INFO,"Dev-Name: " + wifiP2pDevice.deviceName + " Dev-Adr: " + wifiP2pDevice.deviceAddress);
                            }*/

                            activity.logAll(activity.INFO,"\nSelected Device:\nDev-Name: " + dev.deviceName + " Dev-Adr: " + dev.deviceAddress);
                            activity.setWifiDeviceList(peers.getDeviceList());
                        } else {
                            activity.logAll(activity.INFO,"No Devices found.");
                        }

                    }
                });

            }


        }else if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
            int wifi_state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if( wifi_state == WifiP2pManager.WIFI_P2P_STATE_DISABLED){
                activity.logAll(activity.INFO,"Wifi disabled, try to enable it.");
                wifiManager.setWifiEnabled(true);
            }

        }else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if(manager != null) {
                NetworkInfo netInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                if (netInfo != null) {
                    if (netInfo.isConnected()) {
                        activity.logAll(activity.INFO, "Get Connection Info (Receiver)");
                        activity.getConnectionInfo();
                    }
                }
            }
        }else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){
            if(manager != null) {
                WifiP2pDevice info = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                activity.logAll(activity.INFO,"Device-Status: " + info.status);
                if(info.status == 3){
                    activity.setConnected(false);
                    activity.startPeerDiscover();
                }else if(info.status == 0){
                    activity.setConnected(true);
                }
            }
        }

    }

}
