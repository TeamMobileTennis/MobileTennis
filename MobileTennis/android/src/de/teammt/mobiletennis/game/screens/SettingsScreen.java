package de.teammt.mobiletennis.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import de.teammt.mobiletennis.game.MobileTennis;
import de.teammt.mobiletennis.game.managers.ScreenManager;
import de.teammt.mobiletennis.game.ui.UIBuilder;


public class SettingsScreen extends AbstractScreen {

    public SettingsScreen(MobileTennis mt){
        super(mt);

        float labelWidth = Gdx.graphics.getWidth() / 2;
        float labelHeight = Gdx.graphics.getHeight() / 10;
        Label title = UIBuilder.CreateLabel("Einstellungen",  mt.fntTitle, labelWidth, labelHeight, width/2, height * 0.85f);

        stage.addActor(title);
    }


    @Override
    public void show() {
        // input only on the stage elements
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void update(float delta) {
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
