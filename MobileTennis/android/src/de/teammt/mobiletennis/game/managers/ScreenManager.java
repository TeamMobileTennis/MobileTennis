package de.teammt.mobiletennis.game.managers;

import de.teammt.mobiletennis.game.MobileTennis;
import de.teammt.mobiletennis.game.screens.AbstractScreen;
import de.teammt.mobiletennis.game.screens.AccelScreen;
import de.teammt.mobiletennis.game.screens.CreateLobbyScreen;
import de.teammt.mobiletennis.game.screens.GameScreen;
import de.teammt.mobiletennis.game.screens.JoinLobbyScreen;
import de.teammt.mobiletennis.game.screens.MenuScreen;
import de.teammt.mobiletennis.game.screens.PaddleLobbyScreen;
import de.teammt.mobiletennis.game.screens.PaddleScreen;
import de.teammt.mobiletennis.game.screens.SettingsScreen;

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
        PADDLE_LOBBY,
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
        screens.put(STATE.PADDLE_LOBBY, new PaddleLobbyScreen(mt));
        screens.put(STATE.PADDLE, new PaddleScreen(mt));
    }

    public void setScreen(STATE next){
        // set this screen to the active display screen
        mt.setScreen(screens.get(next));

        // set this screen to the screen who gets the messages
        mt.messageHandler.SetActiveScreen(screens.get(next));
    }

    public void dispose(){
        for(AbstractScreen screen : screens.values()){
            if(screen != null)
                screen.dispose();
        }
    }

    public AbstractScreen GetScreen(STATE state){
        return screens.get(state);
    }

}
