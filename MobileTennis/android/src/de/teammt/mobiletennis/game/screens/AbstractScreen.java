package de.teammt.mobiletennis.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.teammt.mobiletennis.game.MobileTennis;

public abstract class AbstractScreen implements Screen {

    public final MobileTennis mt;

    Stage stage;
    OrthographicCamera camera;
    Viewport viewport;

    // the dimensions of the screen (real device)
    float width = Gdx.graphics.getWidth();
    float height = Gdx.graphics.getHeight();

    public AbstractScreen(MobileTennis mt){
        this.mt = mt;
        this.stage = new Stage();

        // Make a camera + Extendviewport and apply the camera to the viewport
        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);
        viewport = new ExtendViewport(MobileTennis.V_WIDTH, MobileTennis.V_HEIGHT, camera);
        viewport.apply();

        camera.position.set(MobileTennis.V_WIDTH/2, MobileTennis.V_HEIGHT/2, 0);
    }

    public abstract void update(float delta);

    @Override
    public void render(float delta){
        update(delta);
        Gdx.gl.glClearColor(166/255f, 1f, 169/255f, 1f);
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

    public void getMessage(String message){}

}
