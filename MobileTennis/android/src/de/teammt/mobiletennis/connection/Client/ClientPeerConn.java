package de.teammt.mobiletennis.connection.Client;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import de.teammt.mobiletennis.connection.Constants;
import de.teammt.mobiletennis.connection.Messages;
import de.teammt.mobiletennis.connection.Operator;
import de.teammt.mobiletennis.connection.PeerConnection;
import de.teammt.mobiletennis.connection.ViewPeerInterface;

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


    /**
     * Initialize the peer connection
     */
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

    /**
     * Start to discover for nearby peers
     */
    public void startPeerDiscover(){
        peerConnection.startPeerDiscover();
    }

    /**
     * Send message to server
     * @param message A String with a message to send.
     */
    @Override
    public void sendMessage(String message) {
        if(client != null) {
            if(client.isAlive())
                client.sendMessage(message);
        }
    }

    /**
     * Close all connections
     */
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

    /**
     * The list of current Peer2Peer Devices in the near
     * @param list  A ArrayList of WifiP2PDevices.
     */
    @Override
    public void setPeerList(ArrayList<WifiP2pDevice> list){
        peerConnection.setPeerList(list);
    }

    /**
     * Get the current connection info and connect to server
     */
    @Override
    public void getConnectionInfo(){
        Log.d("INFO","Requested Connection Info - Begin");
        if(manager != null){
            Log.d("INFO","Requested Connection Info - Manager not null");
            manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                @Override
                public void onConnectionInfoAvailable(WifiP2pInfo info) {
                    Log.d("INFO","Requested Connection Info");
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

    /**
     *
     * @param view  The view to pass messages and info
     */
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

    /**
     * Connect to a p2p device (game host)
     * @param deviceAddress The Mac-Address of Server to connect
     */
//    @Override
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
                Log.d("INFO","Connection success. Trying to open a socket");
                action = Messages.getDataStr(Constants.CMD.CONN, Constants.NAME, playerName);
            }

            @Override
            public void onFailure(int reason) {
                Log.d("ERROR", "Connection failed. Reason: "+reason);
                String error_string;
                switch (reason){
                    case WifiP2pManager.P2P_UNSUPPORTED:
                        error_string = "Fehler beim Verbinden.\nPeer-to-Peer wird nicht unterstützt.";
                        break;
                    case WifiP2pManager.BUSY:
                        error_string = "Fehler beim Verbinden.\nPartner kann die Anfrage gerade nicht annehmen.";
                        break;
                    case WifiP2pManager.ERROR:
                        error_string = "Fehler beim Verbinden.\nEin interner Fehler ist aufgetreten.";
                        break;
                    default:
                        error_string = "Fehler beim Verbinden.";
                        break;
                }
                Toast.makeText(context,error_string,Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Register Receiver
     */
    @Override
    public void registerReceiver() {
        peerConnection.registerReceiver();
    }

    /**
     * Unregister Receiver
     */
    @Override
    public void unregisterReceiver() {
        peerConnection.unregisterReceiver();
    }

    @Override
    public boolean isHost() {
        return false;
    }

    @Override
    public boolean getDiscoveryState(){
        return receiver!=null && receiver.getDiscoveryState();
    }

    @Override
    public WifiP2pGroup getGroupInfo() {
        if(receiver==null)
            return null;
        return receiver.getGroupInfo();
    }
}
