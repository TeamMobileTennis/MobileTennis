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


	private ArrayList<WifiP2pDevice> devList = new ArrayList<>();

	//Information for the Log Tags
	public static final String INFO = "INFO";
	public static final String ERROR = "ERROR";

	private Context context;
	private ViewPeerInterface view;

	private Operator operator;

    // The message Handler who to send the messages to handle
    private MessageHandler messageHandler;

	private MobileTennis mobileTennis;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = this;
		view = this;

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useWakelock = true;
		mobileTennis = new MobileTennis(this);

		initialize(mobileTennis, config);
	}

	public Context getContext(){
		return context;
	}

	public ViewPeerInterface getView(){
		return view;
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

	public void setMessageHandler(MessageHandler messageHandler){
        this.messageHandler = messageHandler;
    }


	//@Override
	public void passInformation(Information info) {
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
	public ArrayList<WifiP2pDevice> searchingDevices() {
		operator = ConnectionFactory.createClient(context,view);

		return devList;
	}

	public void connectToDevice(WifiP2pDevice device) {
		try {
			ConnectionFactory.connectToGame(operator,device.deviceAddress);
		}catch (Exception e){
			Log.d(ERROR, e.getMessage()+"");
		}
	}

	public void createServer() {
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


