package de.a_berisha.testp2pnetwork;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo;
import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Views on the Activity
    private Button btnConnect;
    private Button btnDisconnect;
    private Button btnInfo;
    private Button btnSend;
    private TextView textView;
    private EditText etMessage;

    private WifiP2pManager.Channel gameChannel;         // Channel for P2P Connections
    private WifiP2pManager manager;                     // Wifi P2P Manager
    private Receiver receiver;                          // BroadcastReceiver

    private IntentFilter filter = new IntentFilter();   // Filters for the BroadcastReceiver
    private MainActivity activity = this;               // Current Activity

    private Collection<WifiP2pDevice> wifiDeviceList;  // All requested Wifi P2P Devices

    //Information for the Log Tags
    public static final String INFO = "INFO";
    public static final String ERROR = "ERROR";

    private static final int PORT = 9540;               // Port for the Sockets

    private boolean owner = false;                      // True - Group Owner  False - Client
    private WifiP2pInfo wifiInfo = null;                // Global Variable for the WifiP2pInformation

    private boolean connected;                          // Boolean to check if device is connected

    private Client server;                                          // Socket for the Server
    private ServerHandler[] clients = new ServerHandler[2];  // Socket for all Clients

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize Views
        textView = (TextView) findViewById(R.id.textV);
        btnConnect = (Button) findViewById(R.id.buttonConnect);
        btnDisconnect = (Button) findViewById(R.id.buttonDisconnect);
        btnInfo = (Button) findViewById(R.id.buttonInfo);
        btnSend = (Button) findViewById(R.id.buttonSend);
        etMessage = (EditText) findViewById(R.id.message);

        //Add Actions if Peers is changed
        filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


        //Initialize WifiP2pManager, Channel and Receiver
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        gameChannel = manager.initialize(this, getMainLooper(), null);
        receiver = new Receiver(manager, gameChannel, this);

        //startPeerDiscover();

        // Connect Button
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wifiDeviceList != null) {
                    if (wifiDeviceList.size() > 0) {
                        logAll(INFO, "Connect to the first Device in the List.");
                        WifiP2pDevice dev = wifiDeviceList.iterator().next();
                        connect(dev);
                    } else {
                        Toast.makeText(MainActivity.this, "No Devices to connect.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Disconnect Button
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });

        // Info Button
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(manager != null) {

                    manager.requestConnectionInfo(gameChannel, new WifiP2pManager.ConnectionInfoListener() {
                        @Override
                        public void onConnectionInfoAvailable(WifiP2pInfo info) {
                            try {
                                if (info != null) {
                                    if (info.groupFormed) {
                                        if (info.isGroupOwner) {
                                            logAll(INFO, "Im the Group-Owner");
                                        } else {
                                            logAll(INFO, "Im just a client");
                                        }
                                    }
                                    else {
                                        logAll(INFO, "Group is not formed");
                                    }
                                }
                            }catch(NullPointerException np){
                                np.printStackTrace();
                                logAll(ERROR, "NullPointerException: "+np.getMessage());
                            }catch(NetworkOnMainThreadException nt){
                                nt.printStackTrace();
                                logAll(ERROR, "NetworkOnMainThreadException: "+nt.getMessage());
                            }catch(Exception e){
                                e.printStackTrace();
                                Toast.makeText(activity,"No Connection Information available",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        // Send a Message from the Activity
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (connected) {
                        String message = etMessage.getText().toString();
                        if (wifiInfo != null && !owner) {

                            activity.logAll("To Server: " + message);
                            if (server == null)
                                setupSend();
                            server.sendMessage(message);

                        } else if (wifiInfo == null)
                            logAll(ERROR, "No Connection Information available");
                        else if (owner){

                            logAll(INFO, "To All Clients: " + message);
                            for(ServerHandler handler : clients){
                                if(handler!=null){
                                    handler.sendMessage(message);
                                }
                            }
                        }
                    } else {
                        logAll(INFO, "No Connection exists");
                    }
                }catch (Exception e){
                    logAll(ERROR, e.getMessage());
                }
            }
        });

    }

    // Start to discover for peers and creating a group
    public void startPeerDiscover(){
        // Discover for Peer to Peer Devices
        manager.discoverPeers(gameChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                logAll(INFO,"Discover Peers success");
            }

            @Override
            public void onFailure(int reason) {
                logAll(ERROR, "Discover for peers fails." + codeErrorMessage(reason));
            }
        });
    }

    // Connect to the Device
    public void connect(WifiP2pDevice dev) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = dev.deviceAddress;

        // Connect to the Device
        manager.connect(gameChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                logAll(INFO, "Connecting successful.");
            }

            @Override
            public void onFailure(int reason) {
                logAll(ERROR, "Connecting failed."+ codeErrorMessage(reason));
            }
        });

    }

    // Disconnect from the current devices
    public void disconnect(){
        manager.removeGroup(gameChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                logAll(INFO,"Remove Group successful.");
            }

            @Override
            public void onFailure(int reason) {
                logAll(ERROR, "Remove Group failed."+codeErrorMessage(reason));
            }
        });
    }

    // Get connection information of the current group
    // to get Information for Sockets, ...
    public void getConnectionInfo(){
        if(manager != null){
            manager.requestConnectionInfo(gameChannel, new WifiP2pManager.ConnectionInfoListener() {
                @Override
                public void onConnectionInfoAvailable(WifiP2pInfo info) {
                    if(info != null){
                        if(info.groupFormed){
                            try {
                                wifiInfo = info;
                                logAll(INFO, "Host-Address: " + info.groupOwnerAddress.getHostAddress());
                                if(info.isGroupOwner) {
                                    logAll(INFO, "Server");
                                    owner = true;
                                    receive();
                                }else {
                                    logAll(INFO, "Client");
                                    owner = false;
                                }
                            }catch(Exception e){
                                logAll(ERROR, e.getMessage());
                            }
                        }else {
                            logAll(INFO, "No Group available");
                        }
                    }

                }
            });
        }
    }

    // Receive Data with the Device who is Owner
    public void receive(){
        try {
            ServerSocket socket = new ServerSocket();
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(PORT));


            new AsyncTask<ServerSocket, Void, Void>(){
                @Override
                protected Void doInBackground(ServerSocket... params) {
                    try {
                        for (ServerSocket socket : params) {
                            while(true) {

                                int pos = getFreePosAtServerHandler();

                                if(pos<0){
                                    logAll(ERROR,"Cannot create Connection. Only 2 Connections are allowed.");
                                }else {
                                    clients[pos] = new ServerHandler(socket.accept(), activity);
                                    clients[pos].start();
                                }

                            }
                        }
                    }catch(Exception e){
                        logAll(ERROR, e.getMessage());
                    }
                    return null;
                }
            }.execute(socket);

        }catch (IOException io){
            logAll(ERROR, io.getMessage());
        }

    }

    // Notify this Activity that a ServerHandler stops
    public void stopServerHandler(ServerHandler serverHandler){
        int pos = -1;
        for(int i=0;i<clients.length;i++){
            if(clients[i] == serverHandler){
                pos = i;
                break;
            }
        }
        if(pos >= 0){
            clients[pos] = null;
        }
    }

    // Get a free Position at the ServerHandler Array to put
    // a ServerHandler in it
    private int getFreePosAtServerHandler(){
        int pos = -1;

        for(int i=0; i<clients.length; i++){
            if(clients[i] == null){
                pos = i;
                break;
            }
        }

        return pos;
    }

    // Send Data with each Client to the Server
    private void setupSend(){

        try {
            if(wifiInfo != null) {
                logAll(INFO, wifiInfo.groupOwnerAddress.getHostAddress()+" - " + PORT);

                Socket socket = new Socket(wifiInfo.groupOwnerAddress,PORT);
                server = new Client(socket, activity);

                server.start();

            }else {
                logAll(INFO, "wifiInfo is null");
            }
        }catch(Exception e){
            logAll(ERROR, e.getMessage());
        }

    }

    // This Method log the Messages in the Console
    // and in the TextView of the Application
    public void logAll(String log) {
        textView.append(log+'\n');
        System.out.println(log);
    }
    public void logAll(String tag, String log){
        textView.append(log+'\n');
        Log.d(tag, log);
    }

    // Error Messages for the ActionListeners
    private String codeErrorMessage(int code){
        String error;
        switch(code){
            case 0: error = " (Error)"; break;
            case 1: error = " (P2P not supported)"; break;
            case 2: error = " (Busy)"; break;
            default: error = ""; break;
        }
        return error;
    }

    // Getter and Setter for "connected" (Boolean to check if a Connection is open)
    public void setConnected(boolean connected){
        this.connected = connected;
    }
    public boolean getConnected(){
        return connected;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, filter);
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(manager!=null && gameChannel != null) {
            manager.removeGroup(gameChannel, null);
        }
    }

    public void setWifiDeviceList(Collection<WifiP2pDevice> dev){ this.wifiDeviceList = dev; }

}
