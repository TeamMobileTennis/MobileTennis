package com.crazyking.mobiletennis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.crazyking.mobiletennis.MobileTennisCore;

public abstract class AbstractScreen implements Screen {

    public final MobileTennisCore mt;

    Stage stage;
    OrthographicCamera camera;
    Viewport viewport;

    public AbstractScreen(MobileTennisCore mt){
        this.mt = mt;
        this.stage = new Stage();

        // Make a camera + Extendviewport and apply the camera to the viewport
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(MobileTennisCore.V_WIDTH, MobileTennisCore.V_HEIGHT, camera);
        viewport.apply();

        camera.position.set(MobileTennisCore.V_WIDTH/2, MobileTennisCore.V_HEIGHT/2, 0);
    }

    public abstract void update(float delta);

    @Override
    public void render(float delta){
        update(delta);

        Gdx.gl.glClearColor(1f, 1f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void resize(int width, int height){
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose(){
        stage.dispose();
    }

}
