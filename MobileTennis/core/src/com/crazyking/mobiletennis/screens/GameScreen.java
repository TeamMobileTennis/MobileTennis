package com.crazyking.mobiletennis.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.crazyking.mobiletennis.MobileTennisCore;
import com.crazyking.mobiletennis.body.BodyBuilder;
import com.crazyking.mobiletennis.ui.UIBuilder;

import java.util.ArrayList;
import java.util.List;


public class GameScreen extends AbstractScreen{

    OrthographicCamera camera;

    // Box2D
    World world;
    Box2DDebugRenderer b2dr;

    Body player, playertwo;
    Body ball;
    Body leftBorder, rightBorder;
    int playerGoals = 0, playertwoGoals = 0;
    Body playerGoal, playertwoGoal;

    Label playerScore, playertwoScore;

    float ballSpeed = 100;
    float width, height;

    ArrayList<Body> flaggedBodies = new ArrayList<Body>();

    public GameScreen(MobileTennisCore mt){
        super(mt);

        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);

        // create the world for the bodies
        world = new World(new Vector2(0f, 0f), false);  // Vector2 := gravity // bool if something is sleeping offscreen
        b2dr = new Box2DDebugRenderer();

        // left and right border of the game
        leftBorder = BodyBuilder.BuildWall(world, 0, 0, 20, height);
        rightBorder = BodyBuilder.BuildWall(world, width-20, 0, 20, height);

        // the player paddles
        player = BodyBuilder.BuildBox(world, width/2, 20, 100, 20);
        playertwo = BodyBuilder.BuildBox(world, width/2, height-20, 100, 20);



        // the goal of the players
        playerGoal = BodyBuilder.BuildWall(world, 0, -10, width, 10);
        playertwoGoal = BodyBuilder.BuildWall(world, 0, height, width, 10);

        playerScore = UIBuilder.createLabel("0", mt.skin, "default", mt.buttonStyle, 50, 50, 0.4f);
        playerScore.setPosition(100, 0.4f*height);
        stage.addActor(playerScore);

        playertwoScore = UIBuilder.createLabel("0", mt.skin, "default", mt.buttonStyle, 50, 50, 0.4f);
        playertwoScore.setPosition(100, 0.6f*height);
        stage.addActor(playertwoScore);

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {

                // if the ball collides with something, it should change the direction
                //FIXME: can not hit the sides of the paddle
                if(contact.getFixtureA().getBody() == ball){
                    if(contact.getFixtureB().getBody() == player || contact.getFixtureB().getBody() == playertwo)
                        ball.setLinearVelocity(ball.getLinearVelocity().scl(1, -1));
                    if(contact.getFixtureB().getBody() == rightBorder || contact.getFixtureB().getBody() == leftBorder)
                        ball.setLinearVelocity(ball.getLinearVelocity().scl(-1, 1));

                    // if the ball hits a goal the goalcounter of the (opposite player) should go up
                    if(contact.getFixtureB().getBody() == playerGoal) {
                        playertwoGoals++;
                        playertwoScore.setText(playertwoGoals + "");
                        flaggedBodies.add(ball);
                    }
                    if(contact.getFixtureB().getBody() == playertwoGoal) {
                        playerGoals++;
                        playerScore.setText(playerGoals + "");
                        flaggedBodies.add(ball);
                    }

                } else if(contact.getFixtureB().getBody() == ball){
                    if(contact.getFixtureA().getBody() == player || contact.getFixtureA().getBody() == playertwo)
                        ball.setLinearVelocity(ball.getLinearVelocity().scl(1, -1));
                    if(contact.getFixtureA().getBody() == rightBorder || contact.getFixtureA().getBody() == leftBorder)
                        ball.setLinearVelocity(ball.getLinearVelocity().scl(-1, 1));

                    // if the ball hits a goal the goalcounter of the (opposite player) should go up
                    if(contact.getFixtureA().getBody() == playerGoal) {
                        playertwoGoals++;
                        playertwoScore.setText(playertwoGoals + "");
                        flaggedBodies.add(ball);
                    }
                    if(contact.getFixtureA().getBody() == playertwoGoal) {
                        playerGoals++;
                        playerScore.setText(playerGoals + "");
                        flaggedBodies.add(ball);
                    }
                }
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
    }

    @Override
    public void show() {
        createNewBall();
    }

    @Override
    public void update(float delta) {
        world.step(1 / 60f, 6, 2);

        deleteFlaggedBodies();
    }

    @Override
    public void render(float delta){
        super.render(delta);

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // render our objects in the world, respective to our pixel per meter scale
        b2dr.render(world, camera.combined);
        // draw our stage(UI) on top
        stage.draw();
    }

    @Override
    public void resize(int width, int height){
        camera.setToOrtho(false, width, height);
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
        b2dr.dispose();
        world.dispose();
    }

    private void createNewBall(){
        // game ball
        ball = BodyBuilder.BuildBall(world, width/2, height/2, 20);
        Vector2 direction = new Vector2();
        direction.setToRandomDirection();
        direction.setLength(ballSpeed);
        ball.setLinearVelocity(direction);
    }

    private void deleteFlaggedBodies(){
        // only used for the ball at the moment
        //FIXME: Do no
        while(!flaggedBodies.isEmpty()) {
            Body body = flaggedBodies.get(0);
            flaggedBodies.remove(body);
            world.destroyBody(body);
        }
    }
}
