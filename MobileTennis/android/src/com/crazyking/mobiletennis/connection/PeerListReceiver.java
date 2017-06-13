package com.crazyking.mobiletennis.connection;

import android.net.wifi.p2p.WifiP2pDevice;

import java.util.ArrayList;

/**
 * Created by Adrian Berisha on 13.06.17.
 */

public interface PeerListReceiver {
    void peerListChanged(ArrayList<WifiP2pDevice> peerList);
}
