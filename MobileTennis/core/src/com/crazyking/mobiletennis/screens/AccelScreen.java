package com.crazyking.mobiletennis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.crazyking.mobiletennis.MobileTennis;
import com.crazyking.mobiletennis.managers.ScreenManager;
import com.crazyking.mobiletennis.ui.UIBuilder;

import static java.awt.SystemColor.text;


public class AccelScreen extends AbstractScreen {

    float xPos = 0, yPos = 0, zPos = 0;
    TextField xlabel, ylabel, zlabel;

    public AccelScreen(MobileTennis mt){
        super(mt);

        float width = Gdx.graphics.getWidth() / 2;
        float height = Gdx.graphics.getHeight() / 10;
        Label title = UIBuilder.createLabel("Accelerometer", mt.skin, "default", mt.titleStyle, width, height, 0.85f);

        stage.addActor(title);

        xlabel = new TextField("X: " + xPos, mt.skin, "default");
        xlabel.setSize(50, 20);
        xlabel.setPosition(Gdx.graphics.getWidth() / 2 - 25, Gdx.graphics.getHeight() * 0.7f);
        stage.addActor(xlabel);

        ylabel = new TextField("Y: " + yPos, mt.skin, "default");
        ylabel.setSize(50, 20);
        ylabel.setPosition(Gdx.graphics.getWidth() / 2 - 25, Gdx.graphics.getHeight() * 0.5f);
        stage.addActor(ylabel);

        zlabel = new TextField("Z: " + zPos, mt.skin, "default");
        zlabel.setSize(50, 20);
        zlabel.setPosition(Gdx.graphics.getWidth() / 2 - 25, Gdx.graphics.getHeight() * 0.3f);
        stage.addActor(zlabel);

    }


    @Override
    public void show() {

    }

    @Override
    public void update(float delta) {
        xPos = Gdx.input.getAccelerometerX();
        yPos = Gdx.input.getAccelerometerY();
        zPos = Gdx.input.getAccelerometerZ();

        xlabel.setText("X: " + xPos);
        ylabel.setText("Y: " + yPos);
        zlabel.setText("Z: " + zPos);

        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK)){
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

    }
}
