package com.crazyking.mobiletennis.body;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class BodyBuilder {

    public static Body BuildBox(World world, float posX, float posY, float width, float height){
        Body body;
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(posX, posY);
        def.fixedRotation = true;
        body = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        body.createFixture(shape, 1.0f);
        shape.dispose();

        return body;
    }

    public static Body BuildWall(World world, float posX, float posY, float width, float height){
        Body body;
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(posX + width / 2, posY + height / 2);
        def.fixedRotation = true;
        body = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        body.createFixture(shape, 1.0f);
        shape.dispose();

        return body;
    }

    public static Body BuildBall(World world, float posX, float posY, float radius){
        Body body;
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(posX, posY);
        def.fixedRotation = true;
        def.linearDamping = 0;
        body = world.createBody(def);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        body.createFixture(shape, 1.0f);
        shape.dispose();

        return body;
    }


}
