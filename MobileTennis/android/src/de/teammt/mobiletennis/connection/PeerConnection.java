package de.teammt.mobiletennis.connection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by Adrian Berisha on 06.04.2017.
 */


/**
 * Need to call registerReceiver at onResume() and if you want call unregisterReceiver at
 * onPause() to save battery life.
 *
 * startLobby() to create a Lobby on this device
 * getInformation() to get all Information from lobby's around you
 * sendMessage() to send a Message if a connection is up
 */
public class PeerConnection implements PeerInterface{

    private Context context;
    private ViewPeerInterface view;

    private IntentFilter intentFilter;
    private WifiP2pManager.Channel channel;                         // Channel for P2P Connections
    private WifiP2pManager manager;                                 // Wifi P2P Manager
    private BroadcastReceiver receiver;

    private WifiP2pInfo wifiInfo;
    private ArrayList<WifiP2pDevice> peerList;

    private final static String INFO = "INFO";
    private final static String ERROR = "ERROR";

    private final static int PORT = 9540;

    private String playerName = "";


    private static PeerConnection instance;


    /**
     * Private Constructor for a singleton
     *
     * @param context   A Android Context
     * @param view      A View which implements methods to pass any information's
     */
    private PeerConnection(Context context, ViewPeerInterface view) {
        this.context = context;
        this.view = view;

        initialize();
    }

    /**
     * Private Constructor for a singleton
     *
     * @param context       A context, which will be need to set up wifi p2p
     *                      and other things
     * @param view          The view to send messages, information and so on
     * @param playerName    The player name of the current device
     */
    private PeerConnection(Context context, ViewPeerInterface view, String playerName){
        this.context = context;
        this.view = view;
        this.playerName = playerName;

        initialize();
    }

    /**
     *
     * @param context   A context, which will be need to set up wifi p2p
     *                  and other things
     * @param view      The view to send messages, information and so on
     * @return          Return the instance of the singleton
     */
    public static PeerConnection getInstance(Context context, ViewPeerInterface view){
        if(instance == null){
            instance = new PeerConnection(context, view);
        }
        return instance;
    }

    /**
     *
     * @param context       A context, which will be need to set up wifi p2p
     *                      and other things
     * @param view          The view to send message, information and so on
     * @param playerName    The player name of the current device
     * @return              Return the instance of the singleton
     */
    public static PeerConnection getInstance(Context context, ViewPeerInterface view, String playerName){
        if(instance == null){
            instance = new PeerConnection(context, view, playerName);
        }
        return instance;
    }

    /**
     * initialize the peer connection and the receiver
     */
    private void initialize(){
        manager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(context, Looper.getMainLooper(),null);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);       // Check if available peers changed
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);       // Check if wifi p2p is enabled or disabled
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);  // Check if the state of the wifi p2p connectivity has changed
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION); // Check if this device details have changed
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);   // Check if discovery changed (started or stopped)
    }


    /**
     * Connect to a specific P2P Device
     *
     * @param dev   The P2P-Device which to connect
     */
    @Override
    public void connect(WifiP2pDevice dev) {

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = dev.deviceAddress;
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {
                Log.d(ERROR, "Connection failed");
            }
        });
    }

    /**
     * Disconnect from the current Peer Group
     */
    @Override
    public void disconnect() {
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {
                Log.d(ERROR,"Remove group failed.");
            }
        });
    }

    /**
     * Start searching for Peers in environment
     *
     * Stop searching if device connect to a p2pDevice
     * or if the user stop searching for the peers
     * with stopPeerDiscover()
     */
    @Override
    public void startPeerDiscover(){
        if(manager != null && channel != null) {
            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onFailure(int reason) {
                    Log.d(ERROR, "Discover Peers failed");
                }
            });
        }
    }

    /**
     * Stop searching for peers in environment
     */
    @Override
    public void stopPeerDiscover() {
        if(manager != null && channel != null){
            manager.stopPeerDiscovery(channel,null);
        }
    }

    /**
     * Get information about the current connection
     */
    @Override
    public void getConnectionInfo() {
        manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                if (info != null) {
                    if (info.groupFormed) {
                        wifiInfo = info;
                    } else {
                        Log.d("INFO", "Group not formed");
                    }
                }
            }
        });
    }

    /**
     * register the receiver from current context
     */
    public void registerReceiver(){
        try {
            context.registerReceiver(receiver, intentFilter);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * unregister the receiver from current context
     */
    public void unregisterReceiver(){
        try {
            context.unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Close all active connections from this object
     * @throws IOException  Throws a IOException
     */
    public void closeConnections() throws IOException{
        unregisterReceiver();
        if(manager != null && channel != null)
            manager.removeGroup(channel, null);
    }


    /*
            SETTERS
     */

    /**
     *
     * @param view  The view to send messages, information and so on
     */
    public void setView(ViewPeerInterface view){
        this.view = view;
    }

    /**
     *
     * @param info  Set the actually Wifi-Info about the connection
     */
    public void setWifiInfo(WifiP2pInfo info){
        this.wifiInfo = info;
    }

    /**
     *
     * @param deviceList    A list of all founded peers
     */
    public void setPeerList(ArrayList<WifiP2pDevice> deviceList){
        this.peerList = deviceList;
    }

    /**
     *
     * @param receiver  Set the Receiver
     */
    public void setReceiver(BroadcastReceiver receiver) {
        this.receiver = receiver;
    }



    /*
            GETTERS
     */

    /**
     *
     * @return  currents WifiInfo
     */
    public WifiP2pInfo getWifiInfo(){
        return this.wifiInfo;
    }

    /**
     *
     * @return  playername in String
     */
    public String getPlayerName(){
        return playerName;
    }

    /**
     *
     * @return  the current context of the activity
     */
    public Context getContext() {
        return context;
    }

    /**
     *
     * @return  the current view to pass messages and info
     */
    public ViewPeerInterface getView() {
        return view;
    }

    /**
     *
     * @return  the current Wifi-P2P Channel
     */
    public WifiP2pManager.Channel getChannel() {
        return channel;
    }

    /**
     *
     * @return  the current WifiP2PManager
     */
    public WifiP2pManager getManager() {
        return manager;
    }

    /**
     *
     * @return  the current port for sockets
     */
    public int getPORT() {
        return PORT;
    }


}
