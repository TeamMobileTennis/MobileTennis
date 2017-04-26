package com.crazyking.mobiletennis;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.Collection;

public class AndroidLauncher extends AndroidApplication {

	// Views on the Activity
	public TextView textView;

	public WifiP2pManager.Channel gameChannel;         // Channel for P2P Connections
	public WifiP2pManager manager;                     // Wifi P2P Manager
	public Receiver receiver;                          // BroadcastReceiver

	private IntentFilter filter = new IntentFilter();   // Filters for the BroadcastReceiver
	private AndroidLauncher activity = this;               // Current Activity

	public Collection<WifiP2pDevice> wifiDeviceList;  // All requested Wifi P2P Devices

	//Information for the Log Tags
	public static final String INFO = "INFO";
	public static final String ERROR = "ERROR";

	private static final int PORT = 9540;   // Port for the Sockets

	public boolean owner = false;          // True - Group Owner  False - Client
	public WifiP2pInfo wifiInfo = null;    // Global Variable for the WifiP2pInformation

	private ReadData read;                  // To Read Data (Server)
	private SendData send;                  // To Send Data (Clients)

	public boolean connected;

	Skin skin;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//FIXME: buttons views etc
		//Initialize Views
		textView = (TextView) findViewById(R.id.textV);

		//Add Actions if Peers is changed
		filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


		//Initialize WifiP2pManager, Channel and Receiver
		manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		gameChannel = manager.initialize(this, getMainLooper(), null);
		receiver = new Receiver(manager, gameChannel, this);



		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new MobileTennis(this), config);
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

		read = new ReadData(PORT, this);

		read.execute();
	}

	// Send Data with each Client to the Server
	public void sendToServer(String message){

		send = new SendData(wifiInfo, PORT, this);

		send.execute(message);
	}

	// This Method log the Messages in the Console
	// and in the TextView of the Application
	public void logAll(String log) {
		textView.append(log + '\n');
		System.out.println(log);
	}
	public void logAll(String tag, String log){
		textView.append(log + '\n');
		Log.d(tag, log);
	}
	public String codeErrorMessage(int code){
		String error;
		switch(code){
			case 0: error = " (Error)"; break;
			case 1: error = " (P2P not supported)"; break;
			case 2: error = " (Busy)"; break;
			default: error = ""; break;
		}
		return error;
	}

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
