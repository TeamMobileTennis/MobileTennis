package com.crazyking.mobiletennis.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.crazyking.mobiletennis.MobileTennis;
import com.crazyking.mobiletennis.managers.ScreenManager;


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
