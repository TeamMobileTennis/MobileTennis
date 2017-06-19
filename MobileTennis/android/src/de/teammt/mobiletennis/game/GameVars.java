package de.teammt.mobiletennis.game;


/**
 * Variables and Properties of the actual game
 */
public class GameVars {

    private GameVars(){}

    // the staring values
    public static int WinningPoints = 10;                           // how many points/goals to win the game
    public static float BallSpeed = 5;                            // the starting speed of the ball

    // The string to the sprites
    public static String BallSprite = "tennisball";
    public static String WallSprite = "wall";
    public static String PaddleSprite = "paddle";

    // The properties of the body
    public static int PaddleWidth = 8;
    public static int PaddleHeight = 2;
    public static int PaddleDist = 4;

    public static int BallRadius = 2;

    public static int BorderWidth = 1;
    public static int BorderHeight = MobileTennis.V_HEIGHT;


    //TODO: maybe use the variables in the game screen
    // the current values
    public static int player1Goals = 0, player2Goals = 0;           // the current goals of the players
    public static float player1Accel = 0, player2Accel = 0;         // the current x-value of the players paddles
}
