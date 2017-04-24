package de.a_berisha.testp2pnetwork;

import java.util.Set;

/**
 * Created by CrazyKing on 19.04.2017.
 */

public interface ConnectInterface {

    /**
     * Searches for Devices.
     * @return
     * List of found Devices
     */
    public Set<Gerät> SearchDevices();

    /**
     * Connects to a device
     * @param gerät
     * The Device to connect to
     */
    public void Connect(Gerät gerät);

    /**
     * Disconnects from the current Device
     */
    public void Disconnect();

    /**
     * Sends a Message to the Device it
     * is connected to
     * @param message
     * The Message
     */
    public void SendMessage(String message);
}
