package de.teammt.mobiletennis.game.screens;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import de.teammt.mobiletennis.connection.Messages;
import de.teammt.mobiletennis.game.MobileTennis;
import de.teammt.mobiletennis.game.managers.ScreenManager;

import static de.teammt.mobiletennis.connection.Constants.ACCX;
import static de.teammt.mobiletennis.connection.Constants.CMD.ACCEL;
import static de.teammt.mobiletennis.connection.Constants.CMD.END;
import static de.teammt.mobiletennis.game.ui.UIBuilder.CreateLabel;


public class PaddleScreen extends AbstractScreen {

    private boolean end = false;

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
        if(!end) {
            float x = Gdx.input.getAccelerometerX();
            int xx = (int) (x * 1000);
            String message = Messages.getDataStr(ACCEL, ACCX, xx + "");
            Log.d("String", message);
            // send the message
            mt.sendMessage(message);
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
            case END:
                end = true;
                mt.screenManager.setScreen(ScreenManager.STATE.PADDLE_LOBBY);

                break;
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
