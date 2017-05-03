package com.crazyking.mobiletennis.connection.Server;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.util.ArrayList;

import com.crazyking.mobiletennis.connection.Operator;
import com.crazyking.mobiletennis.connection.PeerConnection;
import com.crazyking.mobiletennis.connection.ViewPeerInterface;

/**
 * Created by Adrian Berisha on 14.04.2017.
 */

public class ServerPeerConn implements Operator {

    private String action = "";             // Action to do, after successful connection

    private GameLobby lobby = null;

    private PeerConnection peerConnection;

    private Context context;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private ViewPeerInterface view;
    private int port;

    private ServerReceiver receiver;

    private static ServerPeerConn instance = null;



    /*
        Constructors
     */
    private ServerPeerConn(Context context, ViewPeerInterface view){
        peerConnection = PeerConnection.getInstance(context, view);
        initialize();
    }
    private ServerPeerConn(Context context, ViewPeerInterface view, String playerName){
        peerConnection = PeerConnection.getInstance(context, view, playerName);
        initialize();
    }
    private ServerPeerConn(PeerConnection peerConnection){
        this.peerConnection = peerConnection;
        initialize();
    }

    /*
        getInstance  -  Singleton-Pattern
     */
    public static ServerPeerConn getInstance(Context context, ViewPeerInterface view){
        if(instance == null){
            instance = new ServerPeerConn(context, view);
        }
        return instance;
    }
    public static ServerPeerConn getInstance(Context context, ViewPeerInterface view, String playerName){
        if(instance == null){
            instance = new ServerPeerConn(context, view, playerName);
        }
        return instance;
    }
    public static ServerPeerConn getInstance(PeerConnection peerConnection){
        if(instance == null){
            instance = new ServerPeerConn(peerConnection);
        }
        return instance;
    }



    private void initialize(){
        manager = peerConnection.getManager();
        channel = peerConnection.getChannel();
        context = peerConnection.getContext();
        view = peerConnection.getView();
        port = peerConnection.getPORT();

        receiver = new ServerReceiver(context, manager, channel, this, view);
        peerConnection.setReceiver(receiver);
        registerReceiver();
    }


    @Override
    public void setView(ViewPeerInterface view) {
        this.view = view;
        if(peerConnection != null){
            peerConnection.setView(view);
        }
        if(receiver != null){
            receiver.setView(view);
        }
        if(lobby != null){
            lobby.setView(view);
        }
    }

    @Override
    public void setup(String lobbyName){
        try {
            manager.createGroup(channel, null);
            lobby = new GameLobby(lobbyName, port, view,
                    ((WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress());
            lobby.start();
//            peerConnection.startPeerDiscover();
        }catch (Exception e){
            Log.d("ERROR", e.getMessage()+"");
        }
    }

    @Override
    public void sendMessage(String message) {
        if(lobby != null){
            if(lobby.isAlive()){
                lobby.sendMessage(message);
            }
        }
    }

    @Override
    public void closeConnections() {
        try {
            if (lobby != null) {
                if (lobby.isAlive()) {
                    lobby.closeLobby();
                }
            }
            peerConnection.closeConnections();
        }catch (Exception e){
            Log.d("ERROR",e.getMessage()+"");
        }
    }

    @Override
    public void setPeerList(ArrayList<WifiP2pDevice> list) {
        peerConnection.setPeerList(list);
    }

    @Override
    public void registerReceiver() {
        peerConnection.registerReceiver();
    }

    @Override
    public void unregisterReceiver() {
        peerConnection.unregisterReceiver();
    }

    @Override
    public void getConnectionInfo(){
        if(manager != null){
            manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                @Override
                public void onConnectionInfoAvailable(WifiP2pInfo info) {
                    if(info != null) {
                        if(info.groupFormed) {
                            peerConnection.setWifiInfo(info);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void connectToGame(String deviceAddress) {
        // Don't need here
    }

}
