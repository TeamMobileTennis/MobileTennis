package de.teammt.mobiletennis.game.screens;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import de.teammt.mobiletennis.connection.Messages;
import de.teammt.mobiletennis.game.MobileTennis;
import de.teammt.mobiletennis.game.managers.ScreenManager;
import de.teammt.mobiletennis.game.ui.StyleBuilder;
import de.teammt.mobiletennis.game.ui.UIBuilder;

import de.teammt.mobiletennis.connection.Constants;

/**
 * Created by CrazyKing on 09.06.2017.
 */

public class PaddleLobbyScreen extends AbstractScreen {

    // Label references for updating the values
    Label winningPointsValue;
    Label selectedBallValue;
    Label ballSpeedValue;


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
        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK)){
            mt.disconnect();
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

    @Override
    public void getMessage(String message){
        String cmd = Messages.getCommand(message);

        switch (cmd){
            case Constants.CMD.START_GAME:
                mt.screenManager.setScreen(ScreenManager.STATE.PADDLE);
                break;
            case Constants.INFO_LOBBY:
                int wp = Integer.parseInt(Messages.getValue(message, Constants.WINNING_POINTS));
                if(wp != 0){
                    winningPointsValue.setText(wp + "");
                }
                String sb = Messages.getValue(message, Constants.SELECTED_BALL);
                if(sb != null){
                    selectedBallValue.setText(sb);
                }
                int bs = Integer.parseInt(Messages.getValue(message, Constants.BALL_SPEED));
                if(bs != 0){
                    ballSpeedValue.setText(bs + "");
                }
                break;
            case Constants.CMD.CLOSE:
                mt.screenManager.setScreen(ScreenManager.STATE.MENU);
            default:
                Log.d("INFO", Messages.getCommand(message) + " wird hier nicht gehandlet.");
                break;
        }
    }

    private void createUIElements(){
        float labelWidth = Gdx.graphics.getWidth() / 2;
        float labelHeight = Gdx.graphics.getHeight() / 10;

        Label.LabelStyle fnt = StyleBuilder.CreateStyle((int)width/25, Color.BLACK, 1, Color.BLACK);

        Label title = UIBuilder.CreateLabel("Lobby", mt.fntTitle, labelWidth, labelHeight, width/2, height * 0.9f);
        stage.addActor(title);

        Label winningPointsLabel = UIBuilder.CreateLabel("Punkte", mt.fntButton, labelWidth, labelHeight, width/2, height * 0.75f);
        stage.addActor(winningPointsLabel);

        winningPointsValue = UIBuilder.CreateLabel("0", fnt, labelWidth, labelHeight, width/2, height * 0.7f);
        stage.addActor(winningPointsValue);

        Label selectedBallLabel = UIBuilder.CreateLabel("Ball", mt.fntButton, labelWidth, labelHeight, width/2, height * 0.65f);
        stage.addActor(selectedBallLabel);

        selectedBallValue = UIBuilder.CreateLabel("Tennisball", fnt, labelWidth, labelHeight, width/2, height * 0.6f);
        stage.addActor(selectedBallValue);

        Label ballSpeedLabel = UIBuilder.CreateLabel("Geschwindigkeit", mt.fntButton, labelWidth, labelHeight, width/2, height * 0.55f);
        stage.addActor(ballSpeedLabel);

        ballSpeedValue = UIBuilder.CreateLabel("400", fnt, labelWidth, labelHeight, width/2, height * 0.5f);
        stage.addActor(ballSpeedValue);

    }

}
