package com.crazyking.mobiletennis.game.screens;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.crazyking.mobiletennis.MessageInterface;
import com.crazyking.mobiletennis.connection.Messages;
import com.crazyking.mobiletennis.connection.Server.ServerPeerConn;
import com.crazyking.mobiletennis.game.MobileTennis;
import com.crazyking.mobiletennis.game.managers.ScreenManager;
import com.crazyking.mobiletennis.game.ui.UIBuilder;

import static android.R.attr.x;
import static com.badlogic.gdx.Input.Keys.S;
import static com.crazyking.mobiletennis.connection.Constants.ACCX;
import static com.crazyking.mobiletennis.connection.Constants.CMD.ACCEL;
import static com.crazyking.mobiletennis.connection.Constants.CMD.RESP;
import static com.crazyking.mobiletennis.connection.Constants.CODE;
import static java.sql.Types.FLOAT;


public class CreateLobbyScreen extends AbstractScreen {

    TextButton btnMessage;

    Label player1;
    float player1Axis = 0;

    public CreateLobbyScreen(final MobileTennis mt){
        super(mt);

        float width = Gdx.graphics.getWidth() / 2;
        float height = Gdx.graphics.getHeight() / 10;
        Label title = UIBuilder.createLabel("Lobby", mt.titleStyle, width, height, 0.85f);

        btnMessage = UIBuilder.createButton("Send Message", mt.buttonStyle, width, height, 0.15f);
        btnMessage.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mt.activity.sendMessage("Test");
            }
        });
        stage.addActor(btnMessage);

        player1 = UIBuilder.createLabel("", mt.buttonStyle, 200, 50, 0.6f);
        stage.addActor(player1);

        stage.addActor(title);
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


        player1.setText("Spieler 1: " + player1Axis);
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

        switch (cmd){
            case ACCEL:
                int xx = Integer.parseInt(Messages.getValue(message, ACCX));
                float x = xx / 1000f;
                player1Axis = x;
                break;
            default:
                Log.d("Message Empfangen", Messages.getCommand(message) + " wird hier nicht gehandlet!!");
                break;
        }
    }
}
