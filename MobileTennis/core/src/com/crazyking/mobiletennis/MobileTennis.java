package com.crazyking.mobiletennis;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.particles.renderers.PointSpriteRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.crazyking.mobiletennis.managers.ScreenManager;

public class MobileTennis extends Game {

	public static String APP_TITLE = "MobileTennis";
	public static double APP_VERSION = 0.1;
	public static int APP_DESKTOP_WIDTH = 720;
	public static int APP_DESKTOP_HEIGHT = 420;
	public static int APP_FPS = 60;


	// Virtual game width and height for every aspect ratio
	public static int V_WIDTH = 100;
	public static int V_HEIGHT = 200;

	// Managers
	public AssetManager assets;
	public ScreenManager screenManager;

	// Batches
	public SpriteBatch batch;
	public ShapeRenderer shapeBatch;
	public Skin skin;

	@Override
	public void create () {
		batch = new SpriteBatch();
		shapeBatch = new ShapeRenderer();
		// default gibldx skin
		skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

		// setup managers
		assets = new AssetManager();
		screenManager = new ScreenManager(this);



		Gdx.input.setCatchBackKey(true);
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
}
