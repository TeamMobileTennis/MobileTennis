package com.crazyking.mobiletennis;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.NetworkOnMainThreadException;
import android.widget.Toast;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import static com.crazyking.mobiletennis.AndroidLauncher.INFO;

/**
 * Created by CrazyKing on 07.04.2017.
 */

public class MobileTennis extends Game {
    SpriteBatch batch;
    Texture img;

    AndroidLauncher activity;

    Stage stage;
    Skin skin;
    TextArea textArea;
    TextButton btnConnect,
            btnDisconnect,
            btnInfo,
            btnSend;

    public MobileTennis(AndroidLauncher act){
        activity = act;
    }

    @Override
    public void create () {
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");

        setupUI();
        setupClickHandler();

        stage.addActor(btnConnect);
        stage.addActor(btnDisconnect);
        stage.addActor(btnInfo);
        stage.addActor(btnSend);

        activity.startPeerDiscover();

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(0.25f, 0.75f, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
        textArea.setText(activity.textView.getText().toString());
    }

    @Override
    public void dispose () {
        batch.dispose();
        img.dispose();
    }

    private void setupUI(){
        stage = new Stage();

        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        textArea = new TextArea("", skin);
        textArea.setPosition(0, 0);
        textArea.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/2);
        textArea.setColor(Color.WHITE);
        stage.addActor(textArea);

        btnConnect = new TextButton("Connect", skin);
        btnConnect.setSize(Gdx.graphics.getWidth() - 10, 100);
        btnConnect.setPosition(5, Gdx.graphics.getHeight() - btnConnect.getHeight() - 10);

        btnDisconnect = new TextButton("Disconnect", skin);
        btnDisconnect.setSize(Gdx.graphics.getWidth() - 10, 100);
        btnDisconnect.setPosition(5, Gdx.graphics.getHeight() - btnDisconnect.getHeight()*2 - 20);

        btnInfo = new TextButton("Info", skin);
        btnInfo.setSize(Gdx.graphics.getWidth() - 10, 100);
        btnInfo.setPosition(5, Gdx.graphics.getHeight() - btnInfo.getHeight()*3 - 30);

        btnSend = new TextButton("Send Message", skin);
        btnSend.setSize(Gdx.graphics.getWidth() - 10, 100);
        btnSend.setPosition(5, Gdx.graphics.getHeight() - btnSend.getHeight()*4 - 40);
    }

    private void setupClickHandler(){

        btnConnect.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(activity.wifiDeviceList != null) {
                    if (activity.wifiDeviceList.size() > 0) {
                        activity.logAll(INFO, "Connect to the first Device in the List.");
                        WifiP2pDevice dev = activity.wifiDeviceList.iterator().next();
                        activity.connect(dev);
                    } else {
                        Toast.makeText(activity, "No Devices to connect.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Disconnect Button
        btnDisconnect.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                activity.disconnect();
            }
        });

        // Info Button
        btnInfo.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(activity.manager != null) {

                    activity.manager.requestConnectionInfo(activity.gameChannel, new WifiP2pManager.ConnectionInfoListener() {
                        @Override
                        public void onConnectionInfoAvailable(WifiP2pInfo info) {
                            try {
                                if (info != null) {
                                    if (info.groupFormed) {
                                        if (info.isGroupOwner) {
                                            activity.logAll(INFO, "Im the Group-Owner");
                                        } else {
                                            activity.logAll(INFO, "Im just a client");
                                        }
                                    }
                                    else {
                                        activity.logAll(INFO, "Group is not formed");
                                    }
                                }
                            }catch(NullPointerException np){
                                np.printStackTrace();
                                activity.logAll(activity.ERROR, "NullPointerException: "+np.getMessage());
                            }catch(NetworkOnMainThreadException nt){
                                nt.printStackTrace();
                                activity.logAll(activity.ERROR, "NetworkOnMainThreadException: "+nt.getMessage());
                            }catch(Exception e){
                                e.printStackTrace();
                                Toast.makeText(activity,"No Connection Information available",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        btnSend.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(activity.connected) {
                    if (activity.wifiInfo != null && !activity.owner)
                        activity.sendToServer("This is a example Message from a Client");
                    else if (activity.wifiInfo == null)
                        activity.logAll(activity.ERROR, "No Connection Information available");
                    else if (activity.owner)
                        activity.logAll(activity.ERROR, "In this Version, only the Client can send a Message");
                }else {
                    activity.logAll(INFO, "No Connection exists");
                }
            }
        });

    }
}
