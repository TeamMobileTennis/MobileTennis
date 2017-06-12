package com.crazyking.mobiletennis.connection;

/**
 * Created by Adrian Berisha on 07.04.2017.
 */

public final class Constants {
    private Constants(){}

    public final class CMD {
        private CMD(){}

        public final static String KEY = "cmd";             // Key for Command - Values for Command:
        public final static String INFO = "INFO";           // Identify information
        public final static String GETINFO = "GET_INFO";    // Getting information
        public final static String CONN = "CONN";           // Connection request
        public final static String RESP = "CONN_RESP";      // Connection response (0 => successful  1 => Lobby full  2 => already connected)
        public final static String START = "START";         // Game starts
        public final static String PAUSE = "PAUSE";         // Game paused
        public final static String END = "END";             // Game ends
        public final static String CLOSE = "CLOSE";         // Lobby/Game closed
        public final static String OTHER = "OTHER";         // Received all other things, like Messages

        public final static String ACCEL = "ACCEL";         // Info about Accelerometer
        public final static String START_GAME = "START_GAME";
        public final static String GAME_INFO = "GAME_INFO"; // information about the current lobby/game

    }
    public final static String CODE = "code";               // Key for code, like connection response code or close response code
    public final static String NAME = "name";               // Key for a name

    public final static int CONN_SUCCESS = 0;               // If the connection is successful
    public final static int CONN_FULL = 1;                  // If the lobby is full
    public final static int CONN_EXIST = 2;                 // If a connection with this device already exists

    public final static int CLOSE_REQ = 0;                  // Send a request to close the connection. (Just notify the other client to close)
    public final static int CLOSE_ACCEPT = 1;               // Like 3-way handshake, if a device close the connection, the other send a close, too. But with code 1
    public final static int CLOSE_REFUSE = 2;               // Refuse the connection request

    public final static String INFO_LOBBY = "lobbyname";    // Lobby name
    public final static String INFO_P1 = "player1";         // Name of player 1
    public final static String INFO_P2 = "player2";         // Name of player 2
    public final static String INFO_ADR = "adr";            // Mac-Address of host


    public final static String PLAYER_CODE = "pcode";       // Code for identify the two players
    public final static String MESSAGE = "MSG";             // Key for received Message

    public final static String ACCX = "xAxis";              // the x value of the accelerometer

    public static final String WINNING_POINTS = "WP";       // the points/goal needed to win the game
    public static final String SELECTED_BALL = "SB";        // the currently selected ball sprite
    public static final String BALL_SPEED = "BS";           // the starting speed of the ball
}
