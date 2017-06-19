package de.teammt.mobiletennis.game.screens;

import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import de.teammt.mobiletennis.connection.Messages;
import de.teammt.mobiletennis.connection.PeerListReceiver;
import de.teammt.mobiletennis.game.MobileTennis;
import de.teammt.mobiletennis.game.managers.ScreenManager;
import de.teammt.mobiletennis.game.ui.UIBuilder;

import java.util.ArrayList;

import de.teammt.mobiletennis.connection.Constants;


public class JoinLobbyScreen extends AbstractScreen implements PeerListReceiver {

    private ArrayList<WifiP2pDevice> devices = new ArrayList<WifiP2pDevice>();

    private ArrayList<TextButton> devBtnList = new ArrayList<TextButton>();


    public JoinLobbyScreen(final MobileTennis mt){
        super(mt);

        createUIElements();

    }


    @Override
    public void show() {
        // input only on the stage elements
        Gdx.input.setInputProcessor(stage);

        // Delete all existing connections
        mt.disconnect();

        mt.registerPeerListReceiver(this);
        search();
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
        mt.unregisterPeerListReceiver(this);
    }

    private void search(){
        mt.searchingDevices();
        // create device list in stage
        for (TextButton btn : devBtnList) {
            btn.remove();
        }
        devBtnList.clear();

        if(devices==null)
            return;
        int i = 0;
        for (WifiP2pDevice dev : devices ) {
            TextButton buttonDev = UIBuilder.CreateButton(dev.deviceName,mt.fntButton,Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/12,width/2,height*(0.7f-0-1f*i));
            buttonDev.addListener(new ButtonHandler(dev));
            devBtnList.add(buttonDev);
            stage.addActor(buttonDev);
            i++;
        }
    }

    @Override
    public void peerListChanged(ArrayList<WifiP2pDevice> peerList) {
        this.devices = peerList;
        search();
    }


    private class ButtonHandler extends ClickListener{

        WifiP2pDevice dev;

        ButtonHandler(WifiP2pDevice dev){
            this.dev = dev;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            mt.connectToDevice(dev);
            Log.d("INFO", "Connect to "+dev.deviceName);
        }
    }


    // how to handle the messages this screen receives
    public void getMessage(String message) {
        //Log.d("Message Empfangen", Messages.getCommand(message));

        String cmd = Messages.getCommand(message);

        switch (cmd){
            case Constants.CMD.RESP:
                // Only handled by Client
                if(Integer.parseInt(Messages.getValue(message, Constants.CODE)) == 0) {
                    // we get a positive response
                    // connection successfull
                    mt.screenManager.setScreen(ScreenManager.STATE.PADDLE_LOBBY);
                }else {
                    // TODO - Connection refused
                }
                break;
            default:
                Log.d("INFO", Messages.getCommand(message) + " wird hier nicht gehandlet.");
                break;
        }
    }

    private void createUIElements(){
        float labelWidth = Gdx.graphics.getWidth() / 2;
        float labelHeight = Gdx.graphics.getHeight() / 10;
        Label title = UIBuilder.CreateLabel("Join Lobby", mt.fntTitle, labelWidth, labelHeight, width/2, height * 0.85f);


        TextButton btnSearch = UIBuilder.CreateButton("Search",  mt.fntButton, Gdx.graphics.getWidth()/3, Gdx.graphics.getHeight()/12, width/2, height * 0.1f);
        btnSearch.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                search();
            }
        });


        stage.addActor(title);
        stage.addActor(btnSearch);
    }
}
