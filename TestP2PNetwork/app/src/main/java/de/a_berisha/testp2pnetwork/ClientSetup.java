package de.a_berisha.testp2pnetwork;

import android.net.wifi.p2p.WifiP2pInfo;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Adrian Berisha on 07.04.2017.
 */

public class ClientSetup extends Thread implements ConnectionHandler{

    private Client server;

    private WifiP2pInfo wifiInfo;
    private int port;
    private ViewPeerInterface view;
    private String playerName;


    public ClientSetup(WifiP2pInfo wifiInfo, int port, ViewPeerInterface view,String playerName){
        this.wifiInfo = wifiInfo;
        this.port = port;
        this.view = view;
        this.playerName = playerName;
    }

    @Override
    public void run() {
        super.run();
        try {
            Socket socket = new Socket();
            socket.setReuseAddress(true);
            socket.connect(new InetSocketAddress(wifiInfo.groupOwnerAddress, port));

            server = new Client(socket, view, playerName);
            server.start();
        }catch (Exception e){
            Log.d("ERROR", e.getMessage());
        }
    }


    @Override
    public void sendMessage(String message) {
        server.sendMessage(message);
    }

    @Override
    public void closeConn() throws IOException {
        server.closeConn();
    }
}
