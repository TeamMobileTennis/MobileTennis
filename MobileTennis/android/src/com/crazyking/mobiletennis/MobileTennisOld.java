package com.crazyking.mobiletennis;

import android.app.Activity;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by CrazyKing on 07.04.2017.
 */

public class MobileTennisOld extends Game {
    SpriteBatch batch;
    Texture img;

    Activity activity;

    public MobileTennisOld(AndroidLauncher act){
        activity = act;
    }

    @Override
    public void create () {
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(0.25f, 0.75f, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void dispose () {
        batch.dispose();
        img.dispose();
    }


}
