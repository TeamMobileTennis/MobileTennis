package com.crazyking.mobiletennis.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.crazyking.mobiletennis.MobileTennisCore;
import com.crazyking.mobiletennis.ui.ButtonHandler;
import com.crazyking.mobiletennis.managers.ScreenManager;
import com.crazyking.mobiletennis.ui.UIBuilder;


public class MenuScreen extends AbstractScreen {

    Sprite background;

    TextButton createLobby, joinLobby, settings, accelTest;
    Label title;

    public MenuScreen(final MobileTennisCore mt){
        super(mt);

        // Make the background sprite and set it to 0/0
        background = new Sprite(new Texture(Gdx.files.internal("menu.jpg")));
        background.setPosition(0, 0);
        background.setSize(MobileTennisCore.V_WIDTH, MobileTennisCore.V_HEIGHT);

        createUI();

        // input only on the stage elements
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {

    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void render(float delta){
        super.render(delta);

        mt.batch.setProjectionMatrix(camera.combined);
        mt.batch.begin();
        background.draw(mt.batch);
        mt.batch.end();

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height);
        stage.getViewport().update(width, height, true);
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

    @Override
    public void dispose(){
        super.dispose();
        background.getTexture().dispose();
    }

    private void createUI(){
        float width = Gdx.graphics.getWidth() / 2;
        float height = Gdx.graphics.getHeight() / 10;

        title = UIBuilder.createLabel("Menu", mt.skin, "default", mt.titleStyle, width, height, 0.85f);

        createLobby = UIBuilder.createButton("Create Lobby", mt.skin, "default", mt.buttonStyle, width, height, 0.7f);
        createLobby.addListener(new ButtonHandler(mt, ScreenManager.STATE.CREATE_LOBBY));

        joinLobby = UIBuilder.createButton("Join Lobby", mt.skin, "default", mt.buttonStyle, width, height, 0.5f);
        joinLobby.addListener(new ButtonHandler(mt, ScreenManager.STATE.JOIN_LOBBY));

        settings = UIBuilder.createButton("Settings", mt.skin, "default", mt.buttonStyle, width, height, 0.3f);
        settings.addListener(new ButtonHandler(mt, ScreenManager.STATE.SETTINGS));

        accelTest = UIBuilder.createButton("Accelerometer", mt.skin, "default", mt.buttonStyle, width, height, 0.1f);
        accelTest.addListener(new ButtonHandler(mt, ScreenManager.STATE.ACCEL_TEST));

        stage.addActor(title);
        stage.addActor(createLobby);
        stage.addActor(joinLobby);
        stage.addActor(settings);
        stage.addActor(accelTest);
    }
}
