package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MyGdxGame extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;
	Texture img1;
	Texture img;
	Sprite sprite;
	TextureRegion[][] regions;

	@Override
	public void create () {
		batch = new SpriteBatch();
		img1 = new Texture("tennisplatz3.jpg");
		img = new Texture("schlaeger.png");
		regions = TextureRegion.split(img, 201, 500);
		sprite = new Sprite(regions[0][0]);


		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
		{
			sprite.translateY(-10f);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
		{
			sprite.translateY(+10f);
		}
		/*
		if(Gdx.input.isTouched())
		{
			sprite.translateY(-5f);
		}
		*/

		batch.begin();
		batch.draw(img1, 200, 50);
		sprite.draw(batch);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {





        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
		float f1 = (float)screenX;
		float f2 = (float)screenY;
		//sprite.translateY(-5f);
	//	sprite.setPosition(f1, -f2+700);
		sprite.setPosition(0, -f2+700);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
