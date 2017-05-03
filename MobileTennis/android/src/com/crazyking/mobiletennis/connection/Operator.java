package com.crazyking.mobiletennis.connection;

import android.net.wifi.p2p.WifiP2pDevice;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Adrian Berisha on 14.04.2017.
 */

public interface Operator {
    /**
     * @param name At the Server the Lobby-Name and with
     *             the client just a empty string
     */
    void setup(String name);

    /**
     * @param deviceAddress The Mac-Address of Server to connect
     *                      (Only for Client - No Usage for Server)
     */
    void connectToGame(String deviceAddress);

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
}
