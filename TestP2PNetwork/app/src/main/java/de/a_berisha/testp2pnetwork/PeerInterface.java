package de.a_berisha.testp2pnetwork;

import android.net.wifi.p2p.WifiP2pDevice;

import java.util.ArrayList;

/**
 * Created by Adrian Berisha on 06.04.2017.
 */

public interface PeerInterface {

    /**
     * Starting the peer discovery
     */
    void startPeerDiscover();

    /**
     * Get connection information, about the current p2p connection
     */
    void getConnectionInfo();

    /**
     * Connects to the Device over WiFi-Direct
     *
     * @param dev   The P2P-Device which to connect
     */
    void connect(WifiP2pDevice dev);

    /**
     * Disconnect from the WiFi-Direct Group
     */
    void disconnect();

    /**
     * @param deviceList    A list of all founded peers
     */
    void setPeerList(ArrayList<WifiP2pDevice> deviceList);
}
