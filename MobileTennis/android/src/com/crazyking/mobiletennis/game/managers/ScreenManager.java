package com.crazyking.mobiletennis.game.managers;

import com.crazyking.mobiletennis.game.MobileTennis;
import com.crazyking.mobiletennis.game.screens.AbstractScreen;
import com.crazyking.mobiletennis.game.screens.AccelScreen;
import com.crazyking.mobiletennis.game.screens.CreateLobbyScreen;
import com.crazyking.mobiletennis.game.screens.GameScreen;
import com.crazyking.mobiletennis.game.screens.JoinLobbyScreen;
import com.crazyking.mobiletennis.game.screens.MenuScreen;
import com.crazyking.mobiletennis.game.screens.PaddleScreen;
import com.crazyking.mobiletennis.game.screens.SettingsScreen;

import java.util.HashMap;

public class ScreenManager {

    public MobileTennis mt;
    private HashMap<STATE, AbstractScreen> screens;

    public enum STATE {
        MENU,
        PLAY,
        SETTINGS,
        CREATE_LOBBY,
        JOIN_LOBBY,
        ACCEL_TEST,
        PADDLE
    }

    public ScreenManager(MobileTennis mt){
        this.mt = mt;

        initScreens();
        setScreen(STATE.MENU);
    }


    public void initScreens(){
        screens = new HashMap<STATE, AbstractScreen>();

        screens.put(STATE.PLAY, new GameScreen(mt));
        screens.put(STATE.MENU, new MenuScreen(mt));
        screens.put(STATE.CREATE_LOBBY, new CreateLobbyScreen(mt));
        screens.put(STATE.JOIN_LOBBY, new JoinLobbyScreen(mt));
        screens.put(STATE.SETTINGS, new SettingsScreen(mt));
        screens.put(STATE.ACCEL_TEST, new AccelScreen(mt));
        screens.put(STATE.PADDLE, new PaddleScreen(mt));
    }

    public void setScreen(STATE next){
        mt.setScreen(screens.get(next));
    }

    public void dispose(){
        for(AbstractScreen screen : screens.values()){
            if(screen != null)
                screen.dispose();
        }
    }

}
