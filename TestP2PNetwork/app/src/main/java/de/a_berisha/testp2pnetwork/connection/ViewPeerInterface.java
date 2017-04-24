package de.a_berisha.testp2pnetwork.connection;

import android.net.wifi.p2p.WifiP2pDevice;

import java.util.ArrayList;

/**
 * Created by Adrian Berisha on 06.04.2017.
 */

public interface ViewPeerInterface {
    void fillPeerList(ArrayList<WifiP2pDevice> peerList);

    /**
     * A Random Message without any Command Messages
     * @param message
     */
    void passMessage(String message);

    /**
     * Information class include a mac-address as string
     * to identify this information.
     *
     * @param info
     */
    void passInformation(Information info);



}
