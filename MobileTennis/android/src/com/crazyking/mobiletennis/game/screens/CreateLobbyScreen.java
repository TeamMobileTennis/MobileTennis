package com.crazyking.mobiletennis.game.screens;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.crazyking.mobiletennis.connection.Messages;
import com.crazyking.mobiletennis.game.MobileTennis;
import com.crazyking.mobiletennis.game.managers.ScreenManager;
import com.crazyking.mobiletennis.game.ui.UIBuilder;

import static com.crazyking.mobiletennis.connection.Constants.CMD.START_GAME;


public class CreateLobbyScreen extends AbstractScreen {

    // the sliders
    Slider winningPoints;

    // the value labels, that needs to get updated
    Label winningPointsValue;

    public CreateLobbyScreen(final MobileTennis mt){
        super(mt);

        // Create the UI elements of the screen
        creatUIElements();

    }

    @Override
    public void show() {
        // input only on the stage elements
        Gdx.input.setInputProcessor(stage);

        mt.activity.CreateServer();
    }

    @Override
    public void update(float delta) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK)){
            mt.screenManager.setScreen(ScreenManager.STATE.MENU);
        }

        updateSliderValues();
    }

    private void updateSliderValues(){
        winningPointsValue.setText((int)winningPoints.getValue() + "");
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


    public void GetMessage(String message) {
        String cmd = Messages.getCommand(message);
        Log.d("String", message);
        Log.d("String", cmd);

        switch (cmd){
            default:
                Log.d("Message Empfangen", Messages.getCommand(message) + " wird hier nicht gehandlet!!");
                break;
        }
    }

    // just some private helper functions
    private void creatUIElements() {
        float labelWidth = Gdx.graphics.getWidth() / 2;
        float labelHeight = Gdx.graphics.getHeight() / 10;

        Label title = UIBuilder.CreateLabel("Lobby", mt.fntTitle, labelWidth, labelHeight, width/2, height * 0.9f);
        stage.addActor(title);

        TextButton btnStart = UIBuilder.CreateButton("Start", mt.fntButton, labelWidth, labelHeight, width/2, height * 0.1f);
        btnStart.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                StartGame();
            }
        });
        stage.addActor(btnStart);


        //TODO: some testing with sliders

        // the first slider -------------------------------------
        Label winningPointsTitle = UIBuilder.CreateLabel("Winning Points", mt.fntButton, labelWidth, labelHeight, width/2, height * 0.75f);
        stage.addActor(winningPointsTitle);

        winningPoints = new Slider(1, 10, 1, false, mt.skin);
        winningPoints.setSize(labelWidth, labelHeight);
        winningPoints.setPosition(width/2, height * 0.7f, Align.center);
        stage.addActor(winningPoints);

        winningPointsValue = UIBuilder.CreateLabel("0", mt.fntButton, labelWidth/2, labelHeight, width/2 + labelWidth/2 + 30, height * 0.7f);
        stage.addActor(winningPointsValue);
        // end of the first slider --------------------------------
    }


    private void StartGame(){
        //TODO: get the information from the sliders

        // send message to the paddles
        String message = Messages.getDataStr(START_GAME);
        mt.activity.sendMessage(message);

        // start the actual game
        mt.screenManager.setScreen(ScreenManager.STATE.PLAY);
    }

}
