package com.crazyking.mobiletennis.game.screens;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.crazyking.mobiletennis.connection.Messages;
import com.crazyking.mobiletennis.game.MobileTennis;

import static com.crazyking.mobiletennis.connection.Constants.ACCX;
import static com.crazyking.mobiletennis.connection.Constants.CMD.ACCEL;
import static com.crazyking.mobiletennis.connection.Constants.CMD.START_GAME;
import static com.crazyking.mobiletennis.game.ui.UIBuilder.CreateLabel;


public class PaddleScreen extends AbstractScreen {

    public PaddleScreen(MobileTennis mt){
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
        // we send always our axis
        // get the string in the right format
        float x = Gdx.input.getAccelerometerX();
        int xx = (int) (x * 1000);
        String message = Messages.getDataStr(ACCEL, ACCX, xx + "");
        Log.d("String", message);
        // send the message
        mt.activity.sendMessage(message);

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
            default:
                Log.d("Message Empfangen", Messages.getCommand(message) + " wird hier nicht gehandlet.");
                break;
        }
    }

    private void createUIElements(){
        float labelWidth = Gdx.graphics.getWidth() / 2;
        float labelHeight = Gdx.graphics.getHeight() / 10;
        Label title = CreateLabel("In Game", mt.fntTitle, labelWidth, labelHeight, width/2, height * 0.85f);

        stage.addActor(title);
    }
}
