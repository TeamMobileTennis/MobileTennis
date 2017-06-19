package de.teammt.mobiletennis.game;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.teammt.mobiletennis.AndroidLauncher;
import de.teammt.mobiletennis.connection.ConnectionFactory;
import de.teammt.mobiletennis.connection.Operator;
import de.teammt.mobiletennis.connection.PeerListReceiver;
import de.teammt.mobiletennis.connection.ViewPeerInterface;
import de.teammt.mobiletennis.game.managers.MessageHandler;
import de.teammt.mobiletennis.game.managers.ScreenManager;
import de.teammt.mobiletennis.game.ui.StyleBuilder;

import java.util.ArrayList;

public class MobileTennis extends Game implements ViewPeerInterface {

	public static int V_WIDTH = 36;
	public static int V_HEIGHT = 78;

	// reference to activity
	private final AndroidLauncher activity;

	// Managers
	public AssetManager assets;
	public ScreenManager screenManager;
	public MessageHandler messageHandler;

	// Batches
	public SpriteBatch batch;
	public ShapeRenderer shapeBatch;
	public static Skin skin;

	// font fntTitle
	public Label.LabelStyle fntTitle;
	public Label.LabelStyle fntButton;

	private Context context;
	private ViewPeerInterface view;

    private Operator operator;

    private ArrayList<WifiP2pDevice> devList = new ArrayList<>();

    private ArrayList<PeerListReceiver> peerListReceivers = new ArrayList<>();

	public MobileTennis(AndroidLauncher activity){
		this.activity = activity;
        this.context = activity.getContext();
        this.view = this;
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		shapeBatch = new ShapeRenderer();

		// TODO: Most likely we dont need it anymore
		// default gibldx skin
		skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

		// create the fonts we need
		createFonts();

		Gdx.input.setCatchBackKey(true);

		// setup managers
		assets = new AssetManager();
		messageHandler = new MessageHandler(this, activity);
		screenManager = new ScreenManager(this);
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		super.dispose();
		batch.dispose();
		shapeBatch.dispose();
		assets.dispose();
		screenManager.dispose();
	}

	private void createFonts(){
		fntTitle = StyleBuilder.CreateStyle(Gdx.graphics.getWidth() / 9, Color.BLACK, 2, Color.BLACK);
		fntButton = StyleBuilder.CreateStyle(Gdx.graphics.getWidth() / 18, Color.WHITE, 2, Color.BLACK);
	}

    @Override
    public void fillPeerList(ArrayList<WifiP2pDevice> peerList) {
        devList.clear();
        devList.addAll(peerList);

        for(PeerListReceiver peerListReceiver : peerListReceivers){
            peerListReceiver.peerListChanged(devList);
        }
    }

    @Override
    public void passMessage(final String message) {
        if(message.isEmpty())
            return;
        Log.d("String", message);
        messageHandler.HandleReceivedMessage(message);
    }

    public void setMessageHandler(MessageHandler messageHandler){
        this.messageHandler = messageHandler;
    }

    // Methods for Connection to use
    public void searchingDevices() {
        operator = ConnectionFactory.createClient(context,view);
    }

    public void connectToDevice(WifiP2pDevice device) {
        try {
            ConnectionFactory.connectToGame(operator,device.deviceAddress);
        }catch (Exception e){
            Log.d("ERROR", e.getMessage()+"");
        }
    }

    public void createServer() {
        operator = ConnectionFactory.createHost(context,view,"Server");
    }

    public void sendMessage(String message){
        Log.d("INFO", message);
        operator.sendMessage(message);
    }

    public void disconnect(){
        if(operator!=null)
            operator.closeConnections();
    }

    public void registerPeerListReceiver(PeerListReceiver peerListReceiver){
        this.peerListReceivers.add(peerListReceiver);
    }

    public void unregisterPeerListReceiver(PeerListReceiver peerListReceiver){
        this.peerListReceivers.remove(peerListReceiver);
    }

    public Operator getOperator(){
        return operator;
    }

    public ArrayList<WifiP2pDevice> getDevList(){
        return devList;
    }
}
