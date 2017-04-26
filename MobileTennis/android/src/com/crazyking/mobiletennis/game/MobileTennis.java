package com.crazyking.mobiletennis.game;

import android.app.Activity;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.crazyking.mobiletennis.AndroidLauncher;
import com.crazyking.mobiletennis.connection.ConnectionInterface;
import com.crazyking.mobiletennis.connection.ViewPeerInterface;
import com.crazyking.mobiletennis.game.managers.ScreenManager;
import com.crazyking.mobiletennis.game.ui.StyleBuilder;

public class MobileTennis extends Game {


	// Virtual game width and height for every aspect ratio
	public static int V_WIDTH = 100;
	public static int V_HEIGHT = 200;

	// reference to activity
	public final AndroidLauncher activity;

	// Managers
	public AssetManager assets;
	public ScreenManager screenManager;

	// Batches
	public SpriteBatch batch;
	public ShapeRenderer shapeBatch;
	public static Skin skin;

	// font titleStyle
	public Label.LabelStyle titleStyle;
	public Label.LabelStyle buttonStyle;

	public MobileTennis(AndroidLauncher act){
		activity = act;
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		shapeBatch = new ShapeRenderer();
		// default gibldx skin
		skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

		createStyles();

		Gdx.input.setCatchBackKey(true);

		// setup managers
		assets = new AssetManager();
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

	private void createStyles(){
		titleStyle = StyleBuilder.createStyle(10, Color.BLACK);
		buttonStyle = StyleBuilder.createStyle(20, Color.WHITE);
	}
}
