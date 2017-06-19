package de.teammt.mobiletennis.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import de.teammt.mobiletennis.game.MobileTennis;
import de.teammt.mobiletennis.game.managers.ScreenManager;

/**
 * A simple ButtonHandler to handle the menu "movement"
 */
public class ButtonHandler extends ClickListener {

    MobileTennis mt;
    ScreenManager.STATE nextState;

    public ButtonHandler(MobileTennis mt, ScreenManager.STATE nextState){
        this.mt = mt;
        this.nextState = nextState;
    }

    @Override
    public void clicked(InputEvent event, float x, float y){
        mt.screenManager.setScreen(nextState);
        //Gdx.app.log("!", "!");
    }

}
