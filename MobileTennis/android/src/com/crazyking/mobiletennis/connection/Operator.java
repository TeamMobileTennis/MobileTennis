package com.crazyking.mobiletennis.connection;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Adrian Berisha on 14.04.2017.
 */

public interface Operator {

    /**
     * @param message A String with a message to send.
     *                It can be only a message or a
     *                command for the game
     */
    void sendMessage(String message);

    /**
     * Closing all open connections
     */
    void closeConnections();

    /**
     * Get the current connection information
     */
    void getConnectionInfo();

    /**
     *
     * @param list  A ArrayList of WifiP2PDevices.
     *              The list contains the current
     *              devices in the environment
     */
    void setPeerList(ArrayList<WifiP2pDevice> list);

    /**
     *
     * @param view  The view to set for all linked
     *              objects
     */
    void setView(ViewPeerInterface view);

    /**
     * Register a receiver for the PeerConnections
     */
    void registerReceiver();

    /**
     * Unregister the Receiver
     */
    void unregisterReceiver();

    /**
     * Method to get information about from which operator
     * this device is (Host or Client)
     *
     * @return  Returns true if Host and false if Client
     */
    boolean isHost();

    /**
     * Boolean with state of discovery
     * @return  True => discovery runs, false => discovery stops
     */
    boolean getDiscoveryState();

    /**
     *
     * @return  The current Group Info from Peer-to-Peer connection
     */
    WifiP2pGroup getGroupInfo();
}
