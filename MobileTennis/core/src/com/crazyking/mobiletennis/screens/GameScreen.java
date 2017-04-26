package com.crazyking.mobiletennis.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.crazyking.mobiletennis.MobileTennisCore;
import com.crazyking.mobiletennis.managers.ScreenManager;

import static com.crazyking.mobiletennis.utils.B2DConstants.PPM;


public class GameScreen extends AbstractScreen{

    OrthographicCamera camera;

    // Box2D
    World world;
    Box2DDebugRenderer b2dr;

    public GameScreen(MobileTennisCore mt){
        super(mt);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, MobileTennisCore.V_WIDTH, MobileTennisCore.V_HEIGHT);


        world = new World(new Vector2(0f, 0f), false);  // Vector2 := gravity // bool if something is sleeping offscreen
        b2dr = new Box2DDebugRenderer();

    }

    @Override
    public void show() {
        mt.batch.setProjectionMatrix(camera.combined);
        mt.shapeBatch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void update(float delta) {
        world.step(1 / MobileTennisCore.APP_FPS, 6, 2);

        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK)){
            mt.screenManager.setScreen(ScreenManager.STATE.MENU);
        }

    }

    @Override
    public void render(float delta){
        super.render(delta);

        // render our objects in the world, respective to our pixel per meter scale
        b2dr.render(world, camera.combined.cpy().scl(PPM));
        // draw our stage(UI) on top
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
    public void dispose(){
        super.dispose();
        world.dispose();
    }
}
