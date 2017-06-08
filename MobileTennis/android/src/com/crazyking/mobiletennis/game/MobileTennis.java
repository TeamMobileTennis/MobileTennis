package com.crazyking.mobiletennis.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.crazyking.mobiletennis.AndroidLauncher;
import com.crazyking.mobiletennis.game.managers.MessageHandler;
import com.crazyking.mobiletennis.game.managers.ScreenManager;
import com.crazyking.mobiletennis.game.ui.StyleBuilder;

public class MobileTennis extends Game {

	public static int V_WIDTH = 100;
	public static int V_HEIGHT = 200;

	// reference to activity
	public final AndroidLauncher activity;

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

	public MobileTennis(AndroidLauncher act){
		activity = act;
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
		fntTitle = StyleBuilder.CreateStyle(Gdx.graphics.getWidth() / 10, Color.BLACK, 2, Color.BLACK);
		fntButton = StyleBuilder.CreateStyle(Gdx.graphics.getWidth() / 20, Color.WHITE, 2, Color.BLACK);
	}
}
