package de.teammt.mobiletennis.game.screens;


import android.util.Log;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
import com.badlogic.gdx.utils.Array;
import de.teammt.mobiletennis.connection.Messages;
import de.teammt.mobiletennis.game.GameVars;
import de.teammt.mobiletennis.game.MobileTennis;
import de.teammt.mobiletennis.game.body.BodyBuilder;
import de.teammt.mobiletennis.game.managers.ScreenManager;
import de.teammt.mobiletennis.game.ui.UIBuilder;
import java.util.Random;


import de.teammt.mobiletennis.connection.Constants;

import static de.teammt.mobiletennis.game.GameVars.BorderWidth;
import static de.teammt.mobiletennis.game.GameVars.PaddleDist;
import static de.teammt.mobiletennis.game.GameVars.PaddleWidth;
import static de.teammt.mobiletennis.game.MobileTennis.V_WIDTH;



public class GameScreen extends AbstractScreen {

    public static float PPM = 8;

    // Box2D
    World world;
    Box2DDebugRenderer b2dr;
    Array<Body> tmpBodies = new Array<Body>();

    Label winner;

    // the Bodies
    Body player1;
    Body player2;
    Body ball;
    Body leftBorder;
    Body rightBorder;
    Body player1Goal;
    Body player2Goal;

    // score
    int player1Goals = 0, player2Goals = 0;
    Label player1Score, player2Score;

    // ball properties
    Body reset = null;

    // some soundy thingys
    Sound sound;

    // the current accel
    float player1Accel = 0, player2Accel = 0;

    Sprite background;

    boolean running;

    // mini fix var
    private float ydir = 0;

    public GameScreen(MobileTennis mt){
        super(mt);

        background = new Sprite(new Texture(Gdx.files.internal("sprites/Tennisplatz.jpg")));
        background.setPosition(0 / PPM, 0 / PPM);
        background.setSize(V_WIDTH / PPM, MobileTennis.V_HEIGHT / PPM);

        createGameObjects();

        setCollisionProperties();

        // create some sound thingy
        sound = Gdx.audio.newSound(Gdx.files.internal("sounds/boing.wav"));
    }

    @Override
    public void show() {
        if(ball == null)
            createNewBall();
        else {
            ball.setTransform(V_WIDTH/2 / PPM, MobileTennis.V_HEIGHT/2 / PPM, 0);
            Vector2 direction = new Vector2();
            //direction.setToRandomDirection();
            direction = generateRandomDirection(direction);
            direction.scl(GameVars.BallSpeed);
            ball.setLinearVelocity(direction);
        }

        player1Goals = 0;
        player2Goals = 0;
        player1Score.setText("0");
        player2Score.setText("0");
        running = true;

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void update(float delta) {
        world.step(1 / 60f, 6, 2);
        resetBall();
        //ball.applyLinearImpulse(ball.getLinearVelocity(), ball.getAngle(), true);
        //Vector2 vec2 = ball.getLinearVelocity();

        //ball.setLinearVelocity(vec2.x*1.1f, vec2.y*1.1f);

        //TODO: just some paddle movement testing
        float x = -1 * player1Accel;
        if(running)
            player1.setLinearVelocity(x, 0);
        float xx = MathUtils.clamp(player1.getPosition().x * PPM, BorderWidth/2 + PaddleWidth/2, V_WIDTH- BorderWidth/2 - PaddleWidth/2);
        player1.setTransform(xx / PPM, PaddleDist / PPM, 0);


        float y = player2Accel;
        if(running)
            player2.setLinearVelocity(y, 0);
        float yy = MathUtils.clamp(player2.getPosition().x * PPM, BorderWidth/2 + PaddleWidth/2, MobileTennis.V_WIDTH-BorderWidth/2 - PaddleWidth/2);
        player2.setTransform(yy / PPM, (MobileTennis.V_HEIGHT-PaddleDist) / PPM, 0);

        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK) && !running){
            winner.remove();
            mt.screenManager.setScreen(ScreenManager.STATE.CREATE_LOBBY);
        }

        /*
        if(Math.abs(ball.getLinearVelocity().y) < 0.1 && running) {
            Vector2 dir = new Vector2(ball.getLinearVelocity().x, ydir);
            dir.setLength(GameVars.BallSpeed);
            ball.setLinearVelocity(dir);
        } else if(running){
            ydir = ball.getLinearVelocity().y;
        }*/
    }

    @Override
    public void render(float delta){
        super.render(delta);

        Gdx.gl.glClearColor(0f, 1f, 0.114f, 0.6f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // render our objects in the world, respective to our pixel per meter scale
        //b2dr.render(world, camera.combined.cpy().scl(PPM));


        // try to draw a sprite
        mt.batch.setProjectionMatrix(camera.combined.cpy().scl(PPM));
        mt.batch.begin();
        background.draw(mt.batch);
        //FIXME: i cant get the bodies
        world.getBodies(tmpBodies);
        for(Body body : tmpBodies){
            if(body.getUserData() != null){// && body.getUserData() instanceof  Sprite){
                Sprite sprite = (Sprite)body.getUserData();
                sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
                sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
                sprite.draw(mt.batch);
            }
        }
        mt.batch.end();



        // draw our stage(UI) on top
        stage.draw();


    }

    @Override
    public void resize(int width, int height){
        //camera.setToOrtho(false, width, height);
        viewport.update(width, height);
        stage.getViewport().update(width, height, true);
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
    public void getMessage(String message) {
        String cmd = Messages.getCommand(message);

        switch (cmd){
            case Constants.CMD.ACCEL:
                int xx = Integer.parseInt(Messages.getValue(message, Constants.ACCX));
                int x = xx/10;
                int player = Integer.parseInt(Messages.getValue(message, Constants.PLAYER_CODE));
                if(player == 1)
                    player1Accel = x;
                else
                    player2Accel = x;
                break;
            default:
                Log.d("INFO", Messages.getCommand(message) + " wird hier nicht gehandlet!!");
                break;
        }
    }

    private void createGameObjects(){
        // create the world for the bodies
        world = new World(new Vector2(0f, 0f), false);  // Vector2 := gravity // bool if something is sleeping offscreen
        b2dr = new Box2DDebugRenderer();

        // left and right border of the game
        leftBorder = BodyBuilder.BuildWall(world, BorderWidth, GameVars.BorderHeight, BorderWidth/2, MobileTennis.V_HEIGHT/2, GameVars.WallSprite);
        rightBorder = BodyBuilder.BuildWall(world, BorderWidth, GameVars.BorderHeight, V_WIDTH- BorderWidth/2, MobileTennis.V_HEIGHT/2, GameVars.WallSprite);

        // the player1 paddles
        player1 = BodyBuilder.BuildPaddle(world, PaddleWidth, GameVars.PaddleHeight, V_WIDTH/2, PaddleDist, GameVars.PaddleSprite);
        player2 = BodyBuilder.BuildPaddle(world, PaddleWidth, GameVars.PaddleHeight, V_WIDTH/2, MobileTennis.V_HEIGHT- PaddleDist, GameVars.PaddleSprite);

        // the goal of the players
        player1Goal = BodyBuilder.BuildWall(world, V_WIDTH, 2, V_WIDTH/2, -1);
        player2Goal = BodyBuilder.BuildWall(world, V_WIDTH, 2, V_WIDTH/2, MobileTennis.V_HEIGHT+1);

        // Create the score display
        player1Score = UIBuilder.CreateLabel("0", mt.fntButton, 50, 50, width, height * 0.4f);
        player1Score.setPosition(100, 0.4f*height);
        stage.addActor(player1Score);

        player2Score = UIBuilder.CreateLabel("0", mt.fntButton, 50, 50, width, height * 0.4f);
        player2Score.setPosition(100, 0.6f*height);
        stage.addActor(player2Score);
    }

    private void setCollisionProperties(){
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {

                // TODO: player different sounds depending on the thing we hit
                sound.play();

                if(Collision(contact, ball, player1Goal)){
                    // we hit the goal of player 1
                    Goal(2);
                } else if(Collision(contact, ball, player2Goal)){
                    // we hit the goal of player 2
                    Goal(1);
                }


                /*
                // if the ball collides with something, it should change the direction
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
                */
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

    // helper function for collision
    private boolean Collision(Contact contact, Body a, Body b){
        if((contact.getFixtureA().getBody() == a && contact.getFixtureB().getBody() == b) ||
                (contact.getFixtureA().getBody() == b && contact.getFixtureB().getBody() == a))
            return true;
        return false;
    }

    private void createNewBall(){
        // game ball
        ball = BodyBuilder.BuildBall(world, GameVars.BallRadius, V_WIDTH/2, MobileTennis.V_HEIGHT/2, GameVars.BallSprite);
        Vector2 direction = new Vector2();
        //direction.setToRandomDirection();
        direction = generateRandomDirection(direction);

        direction.setLength(GameVars.BallSpeed);
        ball.setLinearVelocity(direction);

        ydir = ball.getLinearVelocity().y;
    }

    private void resetBall(){
        if(reset != null){
            reset.setTransform(V_WIDTH/2 / PPM, MobileTennis.V_HEIGHT/2 / PPM, reset.getAngle());
            Vector2 direction = new Vector2();
            //direction.setToRandomDirection();
            direction = generateRandomDirection(direction);
            direction.scl(GameVars.BallSpeed);
            ball.setLinearVelocity(direction);
            reset = null;

            ydir = ball.getLinearVelocity().y;
        }
    }

    private Vector2 generateRandomDirection(Vector2 rndDir){
        Random random = new Random();
        rndDir.set((random.nextInt()%361)/360f,1f);

        Log.d("INFO",rndDir.toString());

        if(random.nextInt()%2==0)
            rndDir.y = rndDir.y * -1;
        return rndDir;
    }

    private void Goal(int player){
        //TODO: clean up this mess
        // player is the player who get the goal/point
        if(player == 1) {
            player1Goals++;
            player1Score.setText(player1Goals + "");

            // also check if the game is over
            if(player1Goals == GameVars.WinningPoints) {
                gameEnd(1);
            } else {
                // let the ball reset in the next step
                reset = ball;
            }

        } else {
            player2Goals++;
            player2Score.setText(player2Goals + "");

            // also check if the game is over
            if(player2Goals == GameVars.WinningPoints) {
                gameEnd(2);
            } else {
                // let the ball reset in the next step
                reset = ball;
            }
        }

        // maybe message the player that someone shot a goal

    }

    private void gameEnd(int winningPlayer){
        running = false;

        // stop the ball
        ball.setLinearVelocity(0, 0);


        winner = UIBuilder.CreateLabel("Spieler " + winningPlayer + "\nhat das Spiel gewonnen", mt.fntButton, width, height/8, width/2, height/2);

        stage.addActor(winner);
        mt.sendMessage(Messages.getDataStr(Constants.CMD.END));
    }

}
