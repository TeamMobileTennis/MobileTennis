package de.a_berisha.testp2pnetwork;


import android.net.wifi.p2p.WifiP2pDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static de.a_berisha.testp2pnetwork.Constants.*;
import static de.a_berisha.testp2pnetwork.Constants.CMD.*;

public class MainActivity extends AppCompatActivity implements ViewPeerInterface{


    // Views on the Activity
    private Button btnDisconnect;
    private Button btnInfo;
    private Button btnSend;
    private Button btnCreate;
    private TextView textView;
    private EditText etMessage;
    private ListView peerListView;
    private TabHost tabHost;

    private ArrayList<WifiP2pDevice> devList = new ArrayList<>();

    //Information for the Log Tags
    public static final String INFO = "INFO";
    public static final String ERROR = "ERROR";

    private String playerName = "Max Mustermann";
    private PeerConnection peerConnection;

    private LinkedHashMap<String, Information> infoMap = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize Views
        textView = (TextView) findViewById(R.id.textV);
        btnDisconnect = (Button) findViewById(R.id.buttonDisconnect);
        btnInfo = (Button) findViewById(R.id.buttonInfo);
        btnSend = (Button) findViewById(R.id.buttonSend);
        btnCreate =(Button) findViewById(R.id.buttonCreate);
        etMessage = (EditText) findViewById(R.id.message);
        peerListView = (ListView) findViewById(R.id.peerList);
        tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();
        // Tab 1 : Connection with the ListView
        TabHost.TabSpec tab = tabHost.newTabSpec("Connection");
        tab.setContent(R.id.connection);
        tab.setIndicator("Connection");
        tabHost.addTab(tab);

        // Tab 2 : Log
        tab = tabHost.newTabSpec("Log");
        tab.setContent(R.id.log);
        tab.setIndicator("Log");
        tabHost.addTab(tab);

        peerConnection = new PeerConnection(this.getApplicationContext(), this, playerName);

        // Disconnect Button
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                peerConnection.disconnect();
                peerConnection.searchLobby();
            }
        });
        // Send a Message from the Activity
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String message = etMessage.getText().toString();
                    peerConnection.sendMessage(message);
                }catch (Exception e){
                    Log.d(ERROR, e.getMessage());
                }
            }
        });

        peerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Information curr = (Information) infoMap.values().toArray()[position];
                    peerConnection.connectAsGameClient(curr.getAddress());

                }catch (Exception e){
                    Log.d(ERROR, e.getMessage());
                }
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lobbyName = etMessage.getText().toString();
                peerConnection.startLobby(lobbyName);
            }
        });


    }


    @Override
    public void fillPeerList(ArrayList<WifiP2pDevice> peerList) {
        devList.clear();
        devList.addAll(peerList);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        for(WifiP2pDevice dev : devList){
            adapter.add(dev.deviceName);
        }

        peerListView.setAdapter(adapter);
    }

    @Override
    public void passMessage(String message) {
        textView.append("Message: " + message +"\n");
    }

    @Override
    public void passInformation(Information info) {
        if(infoMap.get(info.getAddress()).equals(info)){
            return;
        }
        infoMap.put(info.getAddress(), info);
        updateListView();
    }
    public void updateListView(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        for (Information information : infoMap.values()) {
                adapter.add(information.getLobbyName()+" Player 1: "+information.getPlayer1() + " Player 2: "+information.getPlayer2());
        }
        peerListView.setAdapter(adapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        peerConnection.registerReceiver();
    }
    @Override
    protected void onPause() {
        super.onPause();
        peerConnection.unregisterReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
