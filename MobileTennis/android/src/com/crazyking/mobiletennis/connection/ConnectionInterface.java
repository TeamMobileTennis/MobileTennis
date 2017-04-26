package com.crazyking.mobiletennis.connection;


import android.net.wifi.p2p.WifiP2pDevice;

import java.util.ArrayList;

public interface ConnectionInterface {

    public ArrayList<WifiP2pDevice> SearchingDevices();

    public void ConnectToDevice(WifiP2pDevice device);

    public void CreateServer();



}
