package com.crazyking.mobiletennis.game.body;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import static com.crazyking.mobiletennis.game.screens.GameScreen.PPM;

public class BodyBuilder {

    /**
     * Build a simple Box (Kinematic body) and return it
     * @param world     The world the Box(body) "lives"
     * @param posX      The x-position of the body
     * @param posY      The y-position of the body
     * @param width     The width of the body
     * @param height    The height of the body
     * @param sprite    The string of the name of the sprite in the sprites folder
     * @return body     The body
     */
    public static Body BuildPaddle(World world, float width, float height, float posX, float posY, String sprite){
        Body body;
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.KinematicBody;
        def.position.set(posX / PPM, posY / PPM);
        def.fixedRotation = true;
        body = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2 / PPM, height / 2 / PPM);

        body.createFixture(shape, 1.0f);
        shape.dispose();

        // Create the sprite
        Sprite paddleSprite = new Sprite(new Texture(Gdx.files.internal("sprites/" + sprite + ".png")));
        paddleSprite.setSize(width / PPM, height / PPM);
        paddleSprite.setOrigin(width/2 / PPM, height/2 / PPM);
        body.setUserData(paddleSprite);

        return body;
    }

    /**
     * Build a wall (static body) and return it
     * @param world     The world the body "lives" in
     * @param xPos      The x-position of the body
     * @param yPos      The y-position of the body
     * @param width     The width of the body
     * @param height    The height of the body
     * @param sprite    The string of the name of the sprite in the sprites folder
     * @return body     The body
     */
    public static Body BuildWall(World world, float width, float height, float xPos, float yPos, String sprite){
        Body body;
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.fixedRotation = true;
        def.position.set(xPos / PPM, yPos / PPM);
        body = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2 / PPM, height / 2 / PPM);

        body.createFixture(shape, 1.0f);
        shape.dispose();

        // Create the sprite
        if(!sprite.isEmpty()) {
            Sprite wallSprite = new Sprite(new Texture(Gdx.files.internal("sprites/" + sprite + ".png")));
            wallSprite.setSize(width / PPM, height / PPM);
            wallSprite.setOrigin(width / 2 / PPM, height / 2 / PPM);
            body.setUserData(wallSprite);
        }

        return body;
    }

    /**
     * Build a wall (static body) and return it
     * @param world     The world the body "lives" in
     * @param xPos      The x-position of the body
     * @param yPos      The y-position of the body
     * @param width     The width of the body
     * @param height    The height of the body
     * @return body     The body
     */
    public static Body BuildWall(World world, float width, float height, float xPos, float yPos){
        return BuildWall(world, width, height, xPos, yPos, "");
    }

    /**
     * Build a Ball (dynamic body) and return it
     * @param world     The world where the ball "lives"
     * @param posX      The x-position of the ball
     * @param posY      The y-position of the ball
     * @param radius    The radius of the ball
     * @param sprite    The string of the name of the sprite in the sprites folder
     * @return body     The body
     */
    public static Body BuildBall(World world, float radius, float posX, float posY, String sprite){
        Body body;
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(posX / PPM, posY / PPM);
        def.fixedRotation = true;
        body = world.createBody(def);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius / PPM);

        FixtureDef fix = new FixtureDef();
        fix.shape = shape;
        fix.restitution = 1;

        //body.createFixture(shape, 1.0f);
        body.createFixture(fix);
        shape.dispose();

        // and give it a sprite
        sprite = sprite.toLowerCase();
        Sprite ballSprite = new Sprite(new Texture(Gdx.files.internal("sprites/" + sprite + ".png")));
        ballSprite.setSize(radius*2 / PPM, radius*2 / PPM);
        ballSprite.setOrigin(radius / PPM, radius / PPM);
        body.setUserData(ballSprite);

        return body;
    }


}
