package com.crazyking.mobiletennis.game.screens;


import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.crazyking.mobiletennis.connection.Messages;
import com.crazyking.mobiletennis.game.MobileTennis;
import com.crazyking.mobiletennis.game.body.BodyBuilder;
import com.crazyking.mobiletennis.game.ui.UIBuilder;

import static com.crazyking.mobiletennis.connection.Constants.ACCX;
import static com.crazyking.mobiletennis.connection.Constants.CMD.ACCEL;
import static com.crazyking.mobiletennis.connection.Constants.PLAYER_CODE;


public class GameScreen extends AbstractScreen {

    // the camera
    OrthographicCamera camera;

    // Box2D
    World world;
    Box2DDebugRenderer b2dr;

    // the Bodies
    Body player1, player2;
    Body ball;
    Body leftBorder, rightBorder;
    Body player1Goal, player2Goal;

    // Body properties


    // score
    int player1Goals = 0, player2Goals = 0;
    Label player1Score, player2Score;

    // ball properties
    float ballSpeed = 500;
    Body reset = null;

    // the current accel
    float player1Accel = 0, player2Accel = 0;

    float width, height;


    public GameScreen(MobileTennis mt){
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

        // the player1 paddles
        player1 = BodyBuilder.BuildBox(world, width/2, 20, 100, 20);
        player2 = BodyBuilder.BuildBox(world, width/2, height-20, 100, 20);



        // the goal of the players
        player1Goal = BodyBuilder.BuildWall(world, 0, -10, width, 10);
        player2Goal = BodyBuilder.BuildWall(world, 0, height, width, 10);

        player1Score = UIBuilder.CreateLabel("0", mt.fntButton, 50, 50, width, height * 0.4f);
        player1Score.setPosition(100, 0.4f*height);
        stage.addActor(player1Score);

        player2Score = UIBuilder.CreateLabel("0", mt.fntButton, 50, 50, width, height * 0.4f);
        player2Score.setPosition(100, 0.6f*height);
        stage.addActor(player2Score);

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {

                // if the ball collides with something, it should change the direction
                //FIXME: can not hit the sides of the paddle
                if(contact.getFixtureA().getBody() == ball){
                    if(contact.getFixtureB().getBody() == player1 || contact.getFixtureB().getBody() == player2)
                        ball.setLinearVelocity(ball.getLinearVelocity().scl(1, -1));
                    if(contact.getFixtureB().getBody() == rightBorder || contact.getFixtureB().getBody() == leftBorder)
                        ball.setLinearVelocity(ball.getLinearVelocity().scl(-1, 1));

                    // if the ball hits a goal the goalcounter of the (opposite player1) should go up
                    if(contact.getFixtureB().getBody() == player1Goal) {
                        Goal(2);
                    }
                    if(contact.getFixtureB().getBody() == player2Goal) {
                        Goal(1);
                    }

                } else if(contact.getFixtureB().getBody() == ball){
                    if(contact.getFixtureA().getBody() == player1 || contact.getFixtureA().getBody() == player2)
                        ball.setLinearVelocity(ball.getLinearVelocity().scl(1, -1));
                    if(contact.getFixtureA().getBody() == rightBorder || contact.getFixtureA().getBody() == leftBorder)
                        ball.setLinearVelocity(ball.getLinearVelocity().scl(-1, 1));

                    // if the ball hits a goal the goalcounter of the (opposite player1) should go up
                    if(contact.getFixtureA().getBody() == player1Goal) {
                        Goal(2);
                    }
                    if(contact.getFixtureA().getBody() == player2Goal) {
                        Goal(1);
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
        resetBall();

        //TODO: just some paddle movement testing
        float x = -1 * player1Accel * 1000;
        player1.setLinearVelocity(x, 0);
        float xx = MathUtils.clamp(player1.getPosition().x, 70, width-70);
        player1.setTransform(xx, 20, 0);


        float y = player2Accel * 1000;
        player2.setLinearVelocity(y, 0);
        float yy = MathUtils.clamp(player2.getPosition().x, 70, width-70);
        player2.setTransform(yy, height-20, 0);
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

    @Override
    public void GetMessage(String message) {
        String cmd = Messages.getCommand(message);

        switch (cmd){
            case ACCEL:
                int xx = Integer.parseInt(Messages.getValue(message, ACCX));
                float x = xx / 1000f;
                int player = Integer.parseInt(Messages.getValue(message, PLAYER_CODE));
                if(player == 1)
                    player1Accel = x;
                else
                    player2Accel = x;
                break;
            default:
                Log.d("Message Empfangen", Messages.getCommand(message) + " wird hier nicht gehandlet!!");
                break;
        }
    }

    private void createNewBall(){
        // game ball
        ball = BodyBuilder.BuildBall(world, width/2, height/2, 20);
        Vector2 direction = new Vector2();
        direction.setToRandomDirection();
        direction.setLength(ballSpeed);
        ball.setLinearVelocity(direction);
    }

    private void resetBall(){
        if(reset != null){
            reset.setTransform(width/2, height/2, reset.getAngle());
            Vector2 direction = new Vector2();
            direction.setToRandomDirection();
            direction.setLength(ballSpeed);
            ball.setLinearVelocity(direction);
            reset = null;
        }
    }

    private void Goal(int player){
        // player is the player who get the goal/point
        if(player == 1) {
            player1Goals++;
            player1Score.setText(player1Goals + "");
        } else {
            player2Goals++;
            player2Score.setText(player2Goals + "");
        }


        // let the ball reset in the next step
        reset = ball;


        // maybe message the player that someone shot a goal
    }

}
