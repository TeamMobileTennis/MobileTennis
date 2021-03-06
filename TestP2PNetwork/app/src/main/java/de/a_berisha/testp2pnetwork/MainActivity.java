package de.a_berisha.testp2pnetwork;


import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
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

import de.a_berisha.testp2pnetwork.connection.Client.ClientPeerConn;
import de.a_berisha.testp2pnetwork.connection.Information;
import de.a_berisha.testp2pnetwork.connection.Operator;
import de.a_berisha.testp2pnetwork.connection.Server.ServerPeerConn;
import de.a_berisha.testp2pnetwork.connection.ViewPeerInterface;

public class MainActivity extends AppCompatActivity implements ViewPeerInterface {


    // Views on the Activity
    private Button btnSearch;
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

    private Context context;
    private ViewPeerInterface view;

    private LinkedHashMap<String, Information> infoMap = new LinkedHashMap<>();

    private Operator operator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize Views
        textView = (TextView) findViewById(R.id.textV);
        btnSearch = (Button) findViewById(R.id.buttonDisconnect);
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

        context = this;
        view = this;


        // Search Button
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operator = ClientPeerConn.getInstance(context, view, playerName);
                operator.setup("");
            }
        });
        // Send a Message from the Activity
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String message = etMessage.getText().toString();
                    if(operator != null) {
                        operator.sendMessage(message);
                    }else {
                        Log.d("ERROR","Operator is null");
                    }
                }catch (Exception e){
                    Log.d(ERROR, e.getMessage()+"");
                }
            }
        });

        peerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    operator.connectToGame(devList.get(position).deviceAddress);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lobbyName = etMessage.getText().toString();

                operator = ServerPeerConn.getInstance(context, view, playerName);
                operator.setup(lobbyName);
            }
        });

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operator.getConnectionInfo();
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
    public void passMessage(final String message) {
        Log.d("INFO", "Received: "+ message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toTextLog(message);
            }
        });
    }

    public void toTextLog(String message){
        textView.append(message+'\n');
    }

    @Override
    public void passInformation(Information info) {
//        if(infoMap.get(info.getAddress()).equals(info)){
//            return;
//        }
//        infoMap.put(info.getAddress(), info);
//        updateListView();
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
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(operator != null) {
            operator.closeConnections();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
