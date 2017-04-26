package de.a_berisha.testp2pnetwork.connection.Client;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.util.ArrayList;

import de.a_berisha.testp2pnetwork.connection.Constants;
import de.a_berisha.testp2pnetwork.connection.Messages;
import de.a_berisha.testp2pnetwork.connection.Operator;
import de.a_berisha.testp2pnetwork.connection.PeerConnection;
import de.a_berisha.testp2pnetwork.connection.ViewPeerInterface;

/**
 * Created by Adrian Berisha on 14.04.2017.
 */

public class ClientPeerConn implements Operator {

    private String action = "";             // Action to do, after successful connection

    private Client client = null;

    private PeerConnection peerConnection;

    private Context context;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private ViewPeerInterface view;

    private ClientReceiver receiver;

    private int port;
    private String playerName;

    private static ClientPeerConn instance = null;

    /*
        Constructors
     */
    private ClientPeerConn(Context context, ViewPeerInterface view){
        peerConnection = PeerConnection.getInstance(context, view);
        initialize();
    }
    private ClientPeerConn(Context context, ViewPeerInterface view, String playerName){
        peerConnection = PeerConnection.getInstance(context, view, playerName);
        initialize();
    }
    private ClientPeerConn(PeerConnection peerConnection){
        this.peerConnection = peerConnection;
        initialize();
    }

    /*
        getInstance  -  Singleton-Pattern
     */
    public static ClientPeerConn getInstance(Context context, ViewPeerInterface view){
        if(instance == null){
            instance = new ClientPeerConn(context, view);
        }
        return instance;
    }
    public static ClientPeerConn getInstance(Context context, ViewPeerInterface view, String playerName){
        if(instance == null){
            instance = new ClientPeerConn(context, view, playerName);
        }
        return instance;
    }
    public static ClientPeerConn getInstance(PeerConnection peerConnection){
        if(instance == null){
            instance = new ClientPeerConn(peerConnection);
        }
        return instance;
    }



    private void initialize(){

        context = peerConnection.getContext();
        manager = peerConnection.getManager();
        channel = peerConnection.getChannel();
        view = peerConnection.getView();
        port = peerConnection.getPORT();
        playerName = peerConnection.getPlayerName();

        receiver = new ClientReceiver(context, manager, channel, this, view);
        peerConnection.setReceiver(receiver);
        registerReceiver();
    }

    @Override
    public void sendMessage(String message) {
        if(client != null) {
            if(client.isAlive())
                client.sendMessage(message);
        }
    }

    @Override
    public void closeConnections(){
        try {
            if (client != null) {
                if (client.isAlive()) {
                    client.closeConn();
                    client = null;
                }
            }
            peerConnection.closeConnections();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setPeerList(ArrayList<WifiP2pDevice> list){
        peerConnection.setPeerList(list);
    }

    @Override
    public void getConnectionInfo(){
        if(manager != null){
            manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                @Override
                public void onConnectionInfoAvailable(WifiP2pInfo info) {
                    if(info != null) {
                        peerConnection.setWifiInfo(info);
                        if(info.groupFormed){

                            if(client != null){
                                if(!client.isAlive())
                                    client = null;
                                else if(client.getState() == Thread.State.TERMINATED){
                                    client = null;
                                }
                            }

                            if(client == null){
                                client = new Client(info,port, view, playerName);
                                client.start();
                            }

                            if (!action.isEmpty()) {
                                client.sendMessage(action);
                                action = "";
                            }

                        }else{
                            Log.d("ERROR", "Group not formed.");
                        }
                    }else {
                        Log.d("ERROR", "No connection information available");
                    }
                }
            });
        }
    }

    @Override
    public void setup(String name) {
        Log.d("INFO","Search for Lobbys");
        peerConnection.startPeerDiscover();
    }
    public void setup(){
        setup("");
    }

    @Override
    public void setView(ViewPeerInterface view) {
        this.view = view;
        if(peerConnection != null)
            peerConnection.setView(view);
        if(receiver != null)
            receiver.setView(view);
        if(client != null){
            client.setView(view);
        }
    }

    @Override
    public void connectToGame(String deviceAddress){

        if(receiver.isConnect()){
            Log.d("INFO","Already connected");
            action = Messages.getDataStr(Constants.CMD.CONN, Constants.NAME, playerName);
            getConnectionInfo();
            return;
        }

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = deviceAddress;
        config.groupOwnerIntent = 0;

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                action = Messages.getDataStr(Constants.CMD.CONN, Constants.NAME, playerName);
            }

            @Override
            public void onFailure(int reason) {
                Log.d("ERROR", "Connection failed.");
            }
        });
        Log.d("INFO", "connectToGame in Client");


    }

    @Override
    public void registerReceiver() {
        peerConnection.registerReceiver();
    }

    @Override
    public void unregisterReceiver() {
        peerConnection.unregisterReceiver();
    }
}
