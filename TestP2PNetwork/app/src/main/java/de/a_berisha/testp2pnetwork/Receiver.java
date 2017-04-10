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
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Adrian Berisha on 28.03.2017.
 */

public class Receiver extends BroadcastReceiver {

    private Context context;
    private PeerInterface peerConnection;
    private ViewPeerInterface view;

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;


    public Receiver(Context context, WifiP2pManager manager, WifiP2pManager.Channel channel, PeerInterface peerConnection, ViewPeerInterface view){
        super();
        this.context = context;
        this.manager = manager;
        this.channel = channel;
        this.peerConnection = peerConnection;
        this.view = view;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            if(manager != null) {
                manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peers) {
                        ArrayList<WifiP2pDevice> list = new ArrayList<WifiP2pDevice>(peers.getDeviceList());
                        if (list.size() > 0) {
//                            view.fillPeerList(list);
                            Log.d("INFO", list.size()+"");
                            peerConnection.setPeerList(list);
                        }
                    }
                });
            }
        }else if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
            int wifi_state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if( wifi_state == WifiP2pManager.WIFI_P2P_STATE_DISABLED){
                wifiManager.setWifiEnabled(true);
            }

        }else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if(manager != null) {
                NetworkInfo netInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                if (netInfo != null) {
                    if (netInfo.isConnected()) {
                        peerConnection.getConnectionInfo();

                    }
                }
            }
        }else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){
            if(manager != null) {
                WifiP2pDevice info = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                if(info.status == 3){
//                    activity.setConnected(false);
//                    peerConnection.startPeerDiscover();
                }else if(info.status == 0){
//                    activity.setConnected(true);
                }
            }
        }

    }

}
