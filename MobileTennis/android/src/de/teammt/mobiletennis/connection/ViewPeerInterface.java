package de.teammt.mobiletennis.connection;

import android.net.wifi.p2p.WifiP2pDevice;

import java.util.ArrayList;

/**
 * Created by Adrian Berisha on 06.04.2017.
 */

public interface ViewPeerInterface {
    /**
     *
     * @param peerList  A ArrayList of WifiP2pDevices which
     *                  contains all current WifiP2pDevices
     *                  in the environment
     */
    void fillPeerList(ArrayList<WifiP2pDevice> peerList);

    /**
     * A Random Message without any Command Messages
     * @param message A String which contains a message
     *                which received the client or the server
     */
    void passMessage(String message);

    /**
     * Information class include a mac-address as string
     * to identify this information.
     *
     * @param info  A information object which contains
     *              the current information about the
     *              game lobby
     */
    //void passInformation(Information info);



}
