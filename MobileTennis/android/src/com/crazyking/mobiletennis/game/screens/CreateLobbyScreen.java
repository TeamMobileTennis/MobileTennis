package com.crazyking.mobiletennis.game.screens;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.crazyking.mobiletennis.connection.Messages;
import com.crazyking.mobiletennis.game.GameVars;
import com.crazyking.mobiletennis.game.MobileTennis;
import com.crazyking.mobiletennis.game.managers.ScreenManager;
import com.crazyking.mobiletennis.game.ui.UIBuilder;

import static com.crazyking.mobiletennis.connection.Constants.BALL_SPEED;
import static com.crazyking.mobiletennis.connection.Constants.CMD.START_GAME;
import static com.crazyking.mobiletennis.connection.Constants.INFO_LOBBY;
import static com.crazyking.mobiletennis.connection.Constants.SELECTED_BALL;
import static com.crazyking.mobiletennis.connection.Constants.WINNING_POINTS;


public class CreateLobbyScreen extends AbstractScreen {

    // the sliders and stuff
    Slider winningPoints;
    Slider ballSpeed;
    SelectBox<String> selectBall;

    // the value labels, that needs to get updated
    Label winningPointsValue;
    Label ballSpeedValue;
    Texture selectBallTexture;
    Sprite selectBallValue;

    // List of different balls
    Array<String> balls;


    public CreateLobbyScreen(final MobileTennis mt){
        super(mt);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);

        // set up the balls
        balls = new Array<String>();
        balls.add("Tennisball");
        balls.add("Fussball");
        balls.add("Basketball");
        balls.add("Tabletennisball");

        // Create the UI elements of the screen
        createUIElements();

    }

    @Override
    public void show() {
        // input only on the stage elements
        Gdx.input.setInputProcessor(stage);

        // Delete existing connections
        mt.disconnect();

        mt.createServer();
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            mt.screenManager.setScreen(ScreenManager.STATE.MENU);
        }
    }


    private void updateSliderValues(){
        winningPointsValue.setText((int)winningPoints.getValue() + "");
        ballSpeedValue.setText((int)ballSpeed.getValue() + "");

        // Send Information, if slider changed
        String update = Messages.getDataStr(INFO_LOBBY,
                                            WINNING_POINTS,
                                            (int)winningPoints.getValue() + "",
                                            SELECTED_BALL,
                                            selectBall.getSelected(),
                                            BALL_SPEED,
                                            (int)ballSpeed.getValue() + ""
        );
        mt.sendMessage(update);
    }

    @Override
    public void render(float delta){
        super.render(delta);

        stage.act();
        stage.draw();

        mt.batch.setProjectionMatrix(camera.combined);
        mt.batch.begin();
        selectBallValue.draw(mt.batch);
        mt.batch.end();
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


    public void getMessage(String message) {
        String cmd = Messages.getCommand(message);
        Log.d("String", message);
        Log.d("String", cmd);

        switch (cmd){
            default:
                Log.d("Message Empfangen", Messages.getCommand(message) + " wird hier nicht gehandlet!!");
                break;
        }
    }

    // just some private helper functions
    private void createUIElements() {
        float labelWidth = Gdx.graphics.getWidth() / 2;
        float labelHeight = Gdx.graphics.getHeight() / 10;

        EventListener sliderEvent = new EventListener() {
            @Override
            public boolean handle(Event event) {
                updateSliderValues();
                return false;

            }
        };


        Label title = UIBuilder.CreateLabel("Lobby", mt.fntTitle, labelWidth, labelHeight, width/2, height * 0.9f);
        stage.addActor(title);

        TextButton btnStart = UIBuilder.CreateButton("Start", mt.fntButton, labelWidth, labelHeight, width/2, height * 0.1f);
        btnStart.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startGame();
            }
        });
        stage.addActor(btnStart);


        //TODO: some testing with sliders

        // the first slider -------------------------------------
        Label winningPointsTitle = UIBuilder.CreateLabel("Winning Points", mt.fntButton, labelWidth, labelHeight, width/2, height * 0.75f);
        stage.addActor(winningPointsTitle);

        winningPoints = new Slider(1, 10, 1, false, mt.skin);
        winningPoints.setSize(labelWidth, labelHeight);
        winningPoints.setPosition(width/2, height * 0.7f, Align.center);
        winningPoints.setValue(GameVars.WinningPoints);
        stage.addActor(winningPoints);

        winningPointsValue = UIBuilder.CreateLabel("0", mt.fntButton, labelWidth/2, labelHeight, width/2 + labelWidth/2 + 30, height * 0.7f);
        stage.addActor(winningPointsValue);
        // end of the first slider --------------------------------


        // lets try a select menu
        Label selectBallLabel = UIBuilder.CreateLabel("Select Ball", mt.fntButton, labelWidth, labelHeight, width/2, height * 0.65f);
        stage.addActor(selectBallLabel);

        selectBall = new SelectBox<String>(mt.skin);
        selectBall.setItems(balls);
        selectBall.setSize(labelWidth, labelHeight/4);
        selectBall.setPosition(width/2, height * 0.6f, Align.center);
        selectBall.setSelected(GameVars.BallSprite);
        stage.addActor(selectBall);

        selectBallTexture = new Texture(Gdx.files.internal("sprites/tennisball.png"));
        selectBallValue = new Sprite(selectBallTexture);
        selectBallValue.setSize(100, 100);
        selectBallValue.setOrigin(50, 50);
        selectBallValue.setScale(width/750);
        selectBallValue.setPosition(width/2 - selectBallValue.getWidth()/2 + width/3, height * 0.6f - selectBallValue.getHeight()/2);

        selectBall.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                // texture thing is a bit tricky
                selectBallTexture.dispose();
                selectBallTexture = new Texture(Gdx.files.internal("sprites/" + selectBall.getSelected().toLowerCase() + ".png"));
                selectBallValue.setTexture(selectBallTexture);
            }
        });
        // end of the select menu

        // the first slider -------------------------------------
        Label ballSpeedLabel = UIBuilder.CreateLabel("Ballspeed", mt.fntButton, labelWidth, labelHeight, width/2, height * 0.55f);
        stage.addActor(ballSpeedLabel);

        ballSpeed = new Slider(1, 10, 1, false, mt.skin);
        ballSpeed.setSize(labelWidth, labelHeight);
        ballSpeed.setPosition(width/2, height * 0.5f, Align.center);
        ballSpeed.setValue(GameVars.BallSpeed);
        stage.addActor(ballSpeed);

        winningPoints.addListener(sliderEvent);
        selectBall.addListener(sliderEvent);
        ballSpeed.addListener(sliderEvent);

        ballSpeedValue = UIBuilder.CreateLabel("0", mt.fntButton, labelWidth/2, labelHeight, width/2 + labelWidth/2 + 30, height * 0.5f);
        stage.addActor(ballSpeedValue);
        // end of the first slider --------------------------------
    }


    private void startGame(){
        //TODO: get the information from the sliders
        // maybe like this
        GameVars.WinningPoints = (int)winningPoints.getValue();
        GameVars.BallSprite = selectBall.getSelected();
        //GameVars.BallSpeed = (int)BallSpeed.getValue();

        // send message to the paddles
        String message = Messages.getDataStr(START_GAME);
        //String message = Messages.getDataStr(START);
        mt.sendMessage(message);

        // start the actual game
        mt.screenManager.setScreen(ScreenManager.STATE.PLAY);
    }

}
