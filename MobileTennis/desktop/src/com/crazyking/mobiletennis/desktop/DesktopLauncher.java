package com.crazyking.mobiletennis.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.crazyking.mobiletennis.MobileTennis;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = MobileTennis.APP_TITLE + " v" + MobileTennis.APP_VERSION;
		config.width = MobileTennis.APP_DESKTOP_WIDTH;
		config.height = MobileTennis.APP_DESKTOP_HEIGHT;
		config.backgroundFPS = MobileTennis.APP_FPS;
		config.foregroundFPS = MobileTennis.APP_FPS;
		new LwjglApplication(new MobileTennis(), config);
	}
}
