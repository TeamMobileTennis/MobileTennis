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
import com.crazyking.mobiletennis.connection.ConnectionFactory;
import com.crazyking.mobiletennis.connection.Constants;
import com.crazyking.mobiletennis.connection.Information;
import com.crazyking.mobiletennis.connection.Messages;
import com.crazyking.mobiletennis.connection.Operator;
import com.crazyking.mobiletennis.connection.ViewPeerInterface;
import com.crazyking.mobiletennis.game.MobileTennis;
import com.crazyking.mobiletennis.game.managers.MessageHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.crazyking.mobiletennis.R.id.log;


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

    // The message Handler who to send the messages to handle
    private MessageHandler messageHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = this;
		view = this;

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useWakelock = true;
		initialize(new MobileTennis(this), config);

	}



	@Override
	public void fillPeerList(ArrayList<WifiP2pDevice> peerList) {
		devList.clear();
		devList.addAll(peerList);
	}

	@Override
	public void passMessage(final String message) {
        if(message.isEmpty())
            return;
		Log.d("String", message);
        messageHandler.HandleReceivedMessage(message);
	}

	public void SetMessagehandler(MessageHandler messageHandler){
        this.messageHandler = messageHandler;
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

	public void sendMessage(String message){
		Log.d("Message", "Gesendet");
		Log.d("String", message);
		operator.sendMessage(message);
	}

	public void disconnect(){
		//if(operator!=null)
			//operator.closeConnections();
	}
}


