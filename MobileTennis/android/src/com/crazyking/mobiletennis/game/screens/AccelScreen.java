package com.crazyking.mobiletennis.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.crazyking.mobiletennis.game.MobileTennis;
import com.crazyking.mobiletennis.game.managers.ScreenManager;
import com.crazyking.mobiletennis.game.ui.StyleBuilder;
import com.crazyking.mobiletennis.game.ui.UIBuilder;


public class AccelScreen extends AbstractScreen {

    float xPos = 0, yPos = 0, zPos = 0;
    Label xlabel, ylabel, zlabel;

    public AccelScreen(MobileTennis mt){
        super(mt);

        Label.LabelStyle style = StyleBuilder.createStyle(15, Color.BLACK);

        float width = Gdx.graphics.getWidth() / 2;
        float height = Gdx.graphics.getHeight() / 6;
        Label title = UIBuilder.createLabel("Accelerometer", mt.skin, "default", mt.titleStyle, width, height, 0.85f);

        stage.addActor(title);

        xlabel = UIBuilder.createLabel("X:", mt.skin, "default", style, width, height, 0.6f);
        stage.addActor(xlabel);

        ylabel = UIBuilder.createLabel("Y:", mt.skin, "default", style, width, height, 0.4f);
        stage.addActor(ylabel);

        zlabel = UIBuilder.createLabel("Z:", mt.skin, "default", style, width, height, 0.2f);
        stage.addActor(zlabel);
    }


    @Override
    public void show() {
        // input only on the stage elements
        Gdx.input.setInputProcessor(stage);
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
