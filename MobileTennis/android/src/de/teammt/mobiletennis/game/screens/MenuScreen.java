package de.teammt.mobiletennis.game.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import de.teammt.mobiletennis.game.MobileTennis;
import de.teammt.mobiletennis.game.managers.ScreenManager;
import de.teammt.mobiletennis.game.ui.ButtonHandler;
import de.teammt.mobiletennis.game.ui.UIBuilder;


public class MenuScreen extends AbstractScreen {


    TextButton createLobby, joinLobby, settings, accelTest;
    Label title;

    public MenuScreen(final MobileTennis mt){
        super(mt);

        createUIElements();

        // input only on the stage elements
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        // input only on the stage elements
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void render(float delta){
        super.render(delta);

        mt.batch.setProjectionMatrix(camera.combined);
        mt.batch.begin();
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
    }

    private void createUIElements(){
        float labelWidth = Gdx.graphics.getWidth() / 2;
        float labelHeight = Gdx.graphics.getHeight() / 10;

        title = UIBuilder.CreateLabel("Menu",  mt.fntTitle, labelWidth, labelHeight, width/2, height * 0.85f);

        createLobby = UIBuilder.CreateButton("Create Lobby",  mt.fntButton, labelWidth, labelHeight, width/2, height * 0.7f);
        createLobby.addListener(new ButtonHandler(mt, ScreenManager.STATE.CREATE_LOBBY));

        joinLobby = UIBuilder.CreateButton("Join Lobby",  mt.fntButton, labelWidth, labelHeight, width/2, height * 0.5f);
        joinLobby.addListener(new ButtonHandler(mt, ScreenManager.STATE.JOIN_LOBBY));

        settings = UIBuilder.CreateButton("Settings",  mt.fntButton, labelWidth, labelHeight, width/2, height * 0.3f);
        settings.addListener(new ButtonHandler(mt, ScreenManager.STATE.SETTINGS));

        accelTest = UIBuilder.CreateButton("Accelerometer",  mt.fntButton, labelWidth, labelHeight, width/2, height * 0.1f);
        accelTest.addListener(new ButtonHandler(mt, ScreenManager.STATE.ACCEL_TEST));

        stage.addActor(title);
        stage.addActor(createLobby);
        stage.addActor(joinLobby);
        stage.addActor(settings);
        stage.addActor(accelTest);
    }
}
