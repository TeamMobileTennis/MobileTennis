package de.a_berisha.testp2pnetwork;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

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

        activity.logAll("Receiver Init");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        activity.logAll("onReceive()-Action: " + action);

        if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            manager.requestPeers(channel, peerList);
            activity.logAll("Request Peers List");

        }else if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){

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
                    activity.logAll("Dev-Name: " + dev.deviceName + " Dev-Addr: " + dev.deviceAddress);
                    dev = list.iterator().next();
                }
                activity.logAll("Dev-Name: " + dev.deviceName + " Dev-Addr: " + dev.deviceAddress);
                activity.connect(dev);
            }
            else {
                activity.logAll("No Devices found.");
            }
        }
    };
}
