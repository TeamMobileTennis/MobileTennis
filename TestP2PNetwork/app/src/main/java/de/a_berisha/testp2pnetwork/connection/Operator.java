package de.a_berisha.testp2pnetwork.connection;

import android.net.wifi.p2p.WifiP2pDevice;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Adrian Berisha on 14.04.2017.
 */

public interface Operator {
    void setup(String name);
    void connectToGame(String deviceAddress);
    void sendMessage(String message);
    void closeConnections();
    void getConnectionInfo();
    void setPeerList(ArrayList<WifiP2pDevice> list);
    void registerReceiver();
    void unregisterReceiver();
}
