package com.crazyking.mobiletennis;


import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
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

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.crazyking.mobiletennis.connection.Client.ClientPeerConn;
import com.crazyking.mobiletennis.connection.ConnectionFactory;
import com.crazyking.mobiletennis.connection.Constants;
import com.crazyking.mobiletennis.connection.Information;
import com.crazyking.mobiletennis.connection.Messages;
import com.crazyking.mobiletennis.connection.Operator;
import com.crazyking.mobiletennis.connection.Server.ServerPeerConn;
import com.crazyking.mobiletennis.connection.ViewPeerInterface;
import com.crazyking.mobiletennis.game.MobileTennis;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.crazyking.mobiletennis.connection.Constants.*;
import static com.crazyking.mobiletennis.connection.Constants.CMD.*;


public class AndroidLauncher extends AndroidApplication implements ViewPeerInterface {


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
				//operator = ClientPeerConn.getInstance(context, view, playerName);
				//operator.setup("");
				operator = ConnectionFactory.createClient(context,view);
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
					e.printStackTrace();
				}
			}
		});

		peerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
//					operator.connectToGame(devList.get(position).deviceAddress);
					ConnectionFactory.connectToGame(operator,devList.get(position).deviceAddress);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});

		btnCreate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String lobbyName = etMessage.getText().toString();

				//operator = ServerPeerConn.getInstance(context, view, playerName);
				//operator.setup(lobbyName);
				operator = ConnectionFactory.createHost(context,view,lobbyName);
			}
		});

		btnInfo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				operator.getConnectionInfo();
				NetworkInfo networkInfo = getIntent().getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
				if(networkInfo != null){
					if(networkInfo.isConnected()){
						Log.d("INFO","Connected!");
					}
				}else {
					Log.d("INFO","NetInfo is null (MainActivity)");
				}
			}
		});

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new MobileTennis(this), config);

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
		String cmd = Messages.getCommand(message);

		if(!cmd.isEmpty()){
			switch(cmd){
				case RESP:
					// Only handled by Client
					if(Integer.parseInt(Messages.getValue(message, CODE)) == 0) {
						// TODO - Connection accepts
					}else {
						// TODO - Connection refused
					}
					break;
				case START:
					// TODO - Game starts
					break;
				case PAUSE:
					// TODO - Game paused
					break;
				case END:
					// TODO - Game ends
					break;

				case CLOSE:
					// TODO - Connection closed
					break;

				case CONN:
					// Only handled by Server
					String playerName = Messages.getValue(message,NAME);
					// TODO - A Player connected

					// Testing:
					operator.sendMessage("Welcome in Lobby " + playerName);
					countGame();
				default:
					break;
			}
		}

	}
	public void countGame() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					for (int i = 10; i > 0; i--) {
						operator.sendMessage("Game starts in " + i + " seconds");
						Thread.sleep(1000);
					}
					operator.sendMessage(Messages.getDataStr(Constants.CMD.START));
					operator.sendMessage("Game has started");
				} catch (InterruptedException e) {
					Log.d("ERROR", e.getMessage() + "");
				}
			}
		}).start();
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
	public void onResume() {
		super.onResume();
	}
	@Override
	public void onPause() {
		super.onPause();
	}
	@Override
	public void onStop() {
		super.onStop();
		operator.closeConnections();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}



	// Methods for Connection to use
	public ArrayList<WifiP2pDevice> SearchingDevices() {
//		operator = ClientPeerConn.getInstance(context, view, "Player");
//		operator.setup("");
		operator = ConnectionFactory.createClient(context,view);

		return devList;
	}

	public void ConnectToDevice(WifiP2pDevice device) {
		try {
//			operator.connectToGame(device.deviceAddress);
			ConnectionFactory.connectToGame(operator,device.deviceAddress);
		}catch (Exception e){
			Log.d(ERROR, e.getMessage()+"");
//			throw e;
		}
	}

	public void CreateServer() {
//		operator = ServerPeerConn.getInstance(context, view, "Server");
//		operator.setup("Spiel");
		operator = ConnectionFactory.createHost(context,view,"Server");
	}



	//FIXME: JUST TESTING
	// some testing stuff

	private ArrayList<TestInterface> List = new ArrayList<TestInterface>();

	public void ClientConnect(){
		for(TestInterface ti : List){
			ti.TestFunction();
		}
	}

	public void addEvent(TestInterface ti){
		List.add(ti);
	}
}


