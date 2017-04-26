package com.crazyking.mobiletennis.game.utils;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public class B2DBodyBuilder {

    public static Body createBox(World world, float x, float y, float width, float height){
        Body body;

        BodyDef bDef = new BodyDef();
        body = world.createBody(bDef);

        return body;
    }

}
