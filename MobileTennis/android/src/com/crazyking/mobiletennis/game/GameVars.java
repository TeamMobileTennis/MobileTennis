package com.crazyking.mobiletennis.game;


import java.util.HashMap;
import java.util.Map;

/**
 * Variables and Properties of the actual game
 */
public class GameVars {

    private GameVars(){}

    // the staring values
    public static int winningPoints = 10;                           // how many points/goals to win the game
    public static float ballSpeed = 5;                            // the starting speed of the ball

    // The string to the sprites
    public static String BallSprite = "tennisball";
    public static String WallSprite = "wall";
    public static String PaddleSprite = "paddle";

    // The properties of the body
    public static int PaddleWidth = 100;
    public static int PaddleHeight = 20;


    //TODO: maybe use the variables in the game screen
    // the current values
    public static int player1Goals = 0, player2Goals = 0;           // the current goals of the players
    public static float player1Accel = 0, player2Accel = 0;         // the current x-value of the players paddles
}
