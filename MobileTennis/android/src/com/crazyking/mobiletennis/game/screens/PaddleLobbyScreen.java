package com.crazyking.mobiletennis.game.screens;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.crazyking.mobiletennis.connection.Messages;
import com.crazyking.mobiletennis.game.MobileTennis;
import com.crazyking.mobiletennis.game.managers.ScreenManager;
import com.crazyking.mobiletennis.game.ui.UIBuilder;

import static com.crazyking.mobiletennis.connection.Constants.CMD.START_GAME;
import static com.crazyking.mobiletennis.connection.Constants.INFO_LOBBY;
import static com.crazyking.mobiletennis.connection.Constants.WINNING_POINTS;
import static com.crazyking.mobiletennis.game.GameVars.winningPoints;
import static com.crazyking.mobiletennis.game.ui.UIBuilder.CreateLabel;

/**
 * Created by CrazyKing on 09.06.2017.
 */

public class PaddleLobbyScreen extends AbstractScreen {

    // Label references for updating the values
    Label winningPointsValue;


    public PaddleLobbyScreen(MobileTennis mt) {
        super(mt);

        createUIElements();
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

    @Override
    public void GetMessage(String message){
        String cmd = Messages.getCommand(message);

        switch (cmd){
            case START_GAME:
                mt.screenManager.setScreen(ScreenManager.STATE.PADDLE);
                break;
            case INFO_LOBBY:
                int wp = Integer.parseInt(Messages.getValue(message, WINNING_POINTS));
                if(wp != 0){
                    winningPointsValue.setText(wp + "");
                }
                break;
            default:
                Log.d("Message Empfangen", Messages.getCommand(message) + " wird hier nicht gehandlet.");
                break;
        }
    }

    private void createUIElements(){
        float labelWidth = Gdx.graphics.getWidth() / 2;
        float labelHeight = Gdx.graphics.getHeight() / 10;

        Label title = CreateLabel("Lobby", mt.fntTitle, labelWidth, labelHeight, width/2, height * 0.9f);
        stage.addActor(title);

        Label winningPointsLabel = UIBuilder.CreateLabel("Winning Points", mt.fntButton, labelWidth, labelHeight, width/2, height * 0.75f);
        stage.addActor(winningPointsLabel);

        winningPointsValue = UIBuilder.CreateLabel("0", mt.fntButton, labelWidth, labelHeight, width/2, height * 0.7f);
        stage.addActor(winningPointsValue);

    }

}