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


public class AndroidLauncher extends AndroidApplication {


	private Context context;

	private MobileTennis mobileTennis;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = this;

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useWakelock = true;
		mobileTennis = new MobileTennis(this);

		initialize(mobileTennis, config);
	}

	public Context getContext(){
		return context;
	}
}


