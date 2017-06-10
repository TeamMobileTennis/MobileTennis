package com.crazyking.mobiletennis.game;


/**
 * Variables and Properties of the actual game
 */
public class GameVars {

    private GameVars(){}

    // the staring values
    public static int winningPoints = 10;                           // how many points/goals to win the game
    public static float ballSpeed = 500;                            // the starting speed of the ball



    //TODO: maybe use the variables in the game screen
    // the current values
    public static int player1Goals = 0, player2Goals = 0;           // the current goals of the players
    public static float player1Accel = 0, player2Accel = 0;         // the current x-value of the players paddles
}
