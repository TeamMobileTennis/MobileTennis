package com.crazyking.mobiletennis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.crazyking.mobiletennis.MobileTennis;
import com.crazyking.mobiletennis.managers.ScreenManager;
import com.crazyking.mobiletennis.ui.UIBuilder;


public class CreateLobbyScreen extends AbstractScreen {

    public CreateLobbyScreen(MobileTennis mt){
        super(mt);

        float width = Gdx.graphics.getWidth() / 2;
        float height = Gdx.graphics.getHeight() / 10;
        Label title = UIBuilder.createLabel("Create Lobby", mt.skin, "default", mt.titleStyle, width, height, 0.85f);

        stage.addActor(title);
    }


    @Override
    public void show() {

    }

    @Override
    public void update(float delta) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK)){
            mt.screenManager.setScreen(ScreenManager.STATE.MENU);
        }
    }

    @Override
    public void render(float delta){
        super.render(delta);

        stage.draw();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }
}
