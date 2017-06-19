package de.teammt.mobiletennis;


import android.content.Context;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import de.teammt.mobiletennis.game.MobileTennis;


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


