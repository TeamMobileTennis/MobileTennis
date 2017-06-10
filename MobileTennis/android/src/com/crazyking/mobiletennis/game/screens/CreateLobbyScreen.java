package com.crazyking.mobiletennis.game.screens;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.crazyking.mobiletennis.connection.Messages;
import com.crazyking.mobiletennis.game.GameVars;
import com.crazyking.mobiletennis.game.MobileTennis;
import com.crazyking.mobiletennis.game.managers.ScreenManager;
import com.crazyking.mobiletennis.game.ui.UIBuilder;

import static com.crazyking.mobiletennis.connection.Constants.CMD.START_GAME;
import static com.crazyking.mobiletennis.connection.Constants.INFO_LOBBY;
import static com.crazyking.mobiletennis.connection.Constants.WINNING_POINTS;


public class CreateLobbyScreen extends AbstractScreen {

    // the sliders and stuff
    Slider winningPoints;
    SelectBox<String> selectBall;

    // the value labels, that needs to get updated
    Label winningPointsValue;
    Label selectBallValue;

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

        //FIXME: probably dont send this all the time
        // send the information for the lobby/game all the time?
        String wpupdate = Messages.getDataStr(INFO_LOBBY, WINNING_POINTS, (int)winningPoints.getValue() + "");
        mt.activity.sendMessage(wpupdate);
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


        // lets try a select menu
        Label selectBallLabel = UIBuilder.CreateLabel("Select Ball", mt.fntButton, labelWidth, labelHeight, width/2, height * 0.65f);
        stage.addActor(selectBallLabel);

        //FIXME: propably wanna do this with a list
        selectBall = new SelectBox<String>(mt.skin);
        selectBall.setItems("Tennisball", "Fußball");
        selectBall.setSelected("Tennisball");
        selectBall.setSize(labelWidth, labelHeight/4);
        selectBall.setPosition(width/2, height * 0.6f, Align.center);
        stage.addActor(selectBall);
        // end of the select menu
    }


    private void StartGame(){
        //TODO: get the information from the sliders
        // maybe like this
        GameVars.winningPoints = (int)winningPoints.getValue();

        // send message to the paddles
        String message = Messages.getDataStr(START_GAME);
        mt.activity.sendMessage(message);

        // start the actual game
        mt.screenManager.setScreen(ScreenManager.STATE.PLAY);
    }

}
