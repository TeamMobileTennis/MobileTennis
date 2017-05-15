package com.crazyking.mobiletennis.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.crazyking.mobiletennis.MessageInterface;
import com.crazyking.mobiletennis.connection.Server.ServerPeerConn;
import com.crazyking.mobiletennis.game.MobileTennis;
import com.crazyking.mobiletennis.game.managers.ScreenManager;
import com.crazyking.mobiletennis.game.ui.UIBuilder;

import static com.badlogic.gdx.Input.Keys.S;
import static java.sql.Types.FLOAT;


public class CreateLobbyScreen extends AbstractScreen implements MessageInterface {

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

        mt.activity.addEvent(this);
    }

    @Override
    public void update(float delta) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK)){
            mt.screenManager.setScreen(ScreenManager.STATE.MENU);
        }


        //player1.setText("Spieler 1: " + (int)player1Axis + " Messages");
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
    public void GetMessage(String message) {
        player1Axis++;
        player1.setText("Test");
        //player1Axis = Float.parseFloat(message);
    }
}
