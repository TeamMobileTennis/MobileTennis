package com.crazyking.mobiletennis.game.screens;

import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.crazyking.mobiletennis.MessageInterface;
import com.crazyking.mobiletennis.game.MobileTennis;
import com.crazyking.mobiletennis.game.managers.ScreenManager;
import com.crazyking.mobiletennis.game.ui.UIBuilder;

import java.util.ArrayList;

import static com.crazyking.mobiletennis.game.ui.UIBuilder.createLabel;


public class JoinLobbyScreen extends AbstractScreen implements MessageInterface {

    private ArrayList<WifiP2pDevice> devices = new ArrayList<WifiP2pDevice>();

    private ArrayList<Label> devList = new ArrayList<Label>();

    Label connected;

    int messages = 0;

    public JoinLobbyScreen(final MobileTennis mt){
        super(mt);

        float width = Gdx.graphics.getWidth() / 2;
        float height = Gdx.graphics.getHeight() / 10;
        Label title = createLabel("Join Lobby", mt.titleStyle, width, height, 0.85f);

        TextButton btnSearch = UIBuilder.createButton("Search",  mt.buttonStyle, 200, 50, 0.2f);
        btnSearch.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                search();
            }
        });


        connected = UIBuilder.createLabel("", mt.buttonStyle, 200, 50, 0.5f);
        stage.addActor(connected);

        stage.addActor(title);
        stage.addActor(btnSearch);

    }


    @Override
    public void show() {
        // input only on the stage elements
        Gdx.input.setInputProcessor(stage);

        search();

        mt.activity.addEvent(this);
    }

    @Override
    public void update(float delta) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK)){
            mt.screenManager.setScreen(ScreenManager.STATE.MENU);
        }

        connected.setText(messages + " Messages empfangen\n" + Gdx.input.getAccelerometerX());
        mt.activity.sendMessage(Gdx.input.getAccelerometerX() + "");

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

    private void search(){
        devices = mt.activity.SearchingDevices();
        // create device list in stage
        for (Label label : devList) {
            label.remove();
        }
        devList.clear();
        int i = 0;
        for (WifiP2pDevice dev : devices ) {
            Label labelDev = createLabel(dev.deviceName, mt.buttonStyle, 200, 50, 0.7f - 0.1f * i);
            labelDev.addListener(new LabelHandler(dev));
            devList.add(labelDev);
            stage.addActor(labelDev);
            i++;
        }
    }



    private class LabelHandler extends ClickListener{

        WifiP2pDevice dev;

        LabelHandler(WifiP2pDevice dev){
            this.dev = dev;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            mt.activity.ConnectToDevice(dev);
            Log.d("INFO", "Connect to "+dev.deviceName);
        }
    }


    @Override
    public void GetMessage(String message) {
        messages++;
        //connected.setText(connected.getText() + " \n" + message);
    }
}
