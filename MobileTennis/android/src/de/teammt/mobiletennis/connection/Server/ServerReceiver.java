package de.teammt.mobiletennis.connection.Server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;

import java.util.ArrayList;

import de.teammt.mobiletennis.connection.ViewPeerInterface;

/**
 * Created by Adrian Berisha on 14.04.2017.
 */

public class ServerReceiver extends BroadcastReceiver {

    private Context context;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private ServerPeerConn peer;
    private ViewPeerInterface view;

    private NetworkInfo networkInfo;
    private WifiP2pGroup wifiGroup = null;
    private int discoveryState = 0;

    /**
     * Constructor for Server-Receiver, which receives actions from device, like changing of the peer list and so stuff
     * @param context   Application Context
     * @param manager   Wifi P2P Manager
     * @param channel   Wifi P2P Channel
     * @param peer      Wifi Server Peer-Connection
     * @param view      ViewPeerInterface
     */
    public ServerReceiver(Context context, WifiP2pManager manager, WifiP2pManager.Channel channel, ServerPeerConn peer, ViewPeerInterface view) {
        super();
        this.context = context;
        this.manager = manager;
        this.channel = channel;
        this.peer = peer;
        this.view = view;
    }

    /**
     * Updates the view
     * @param view  ViewPeerInterface to pass messages
     */
    public void setView(ViewPeerInterface view) {
        this.view = view;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();         // Action that the Server-Receiver received

        if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            if(manager != null) {
                manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peers) {
                        ArrayList<WifiP2pDevice> list = new ArrayList<>(peers.getDeviceList());
                        peer.setPeerList(list);
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
                networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                wifiGroup = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP);
                if (networkInfo != null) {
                    if (networkInfo.isConnected()) {
                        peer.getConnectionInfo();
                    }
                }
            }
        }else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            if (manager != null) {
//                WifiP2pDevice info = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            }
        }else if(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)){
            if(manager != null){
                discoveryState = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, 0);
            }
        }
    }

    /**
     * Boolean with State of Discovery
     * @return  True, if discovery running and false, if discovery stopped
     */
    public boolean getDiscoveryState(){
        if(discoveryState == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED){
            return true;
        }
        return false;
    }

    /**
     * Get Information about the current connected Wifi-P2P-Group
     * @return  The current WifiP2pGroup Information
     */
    public WifiP2pGroup getGroupInfo(){
        return wifiGroup;
    }
}
