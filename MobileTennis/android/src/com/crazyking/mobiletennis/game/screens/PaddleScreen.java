package com.crazyking.mobiletennis.game.screens;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.crazyking.mobiletennis.connection.Messages;
import com.crazyking.mobiletennis.game.MobileTennis;
import com.crazyking.mobiletennis.game.managers.ScreenManager;

import static android.R.attr.x;
import static com.crazyking.mobiletennis.connection.Constants.ACCX;
import static com.crazyking.mobiletennis.connection.Constants.CMD.ACCEL;
import static com.crazyking.mobiletennis.connection.Constants.CMD.RESP;
import static com.crazyking.mobiletennis.connection.Constants.CMD.START;
import static com.crazyking.mobiletennis.connection.Constants.CMD.START_GAME;
import static com.crazyking.mobiletennis.connection.Constants.CODE;
import static com.crazyking.mobiletennis.game.ui.UIBuilder.createLabel;


public class PaddleScreen extends AbstractScreen {

    Label title;
    boolean running = false;

    public PaddleScreen(MobileTennis mt){
        super(mt);

        float width = Gdx.graphics.getWidth() / 2;
        float height = Gdx.graphics.getHeight() / 10;
        title = createLabel("Lobby", mt.titleStyle, width, height, 0.85f);

        stage.addActor(title);
    }

    @Override
    public void show() {
        // input only on the stage elements
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void update(float delta) {
        // FIXME: we do not want to send it yet
        // we send always our axis
        // get the string in the right format
        if(running) {
            float x = Gdx.input.getAccelerometerX();
            int xx = (int) (x * 1000);
            String message = Messages.getDataStr(ACCEL, ACCX, xx + "");
            Log.d("String", message);
            // send the message
            mt.activity.sendMessage(message);
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

    @Override
    public void GetMessage(String message){
        String cmd = Messages.getCommand(message);

        switch (cmd){
            case START_GAME:
                title.setText("In Game");
                running = true;
                break;
            default:
                Log.d("Message Empfangen", Messages.getCommand(message) + " wird hier nicht gehandlet.");
                break;
        }
    }
}
