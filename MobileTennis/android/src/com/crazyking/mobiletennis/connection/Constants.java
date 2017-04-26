package com.crazyking.mobiletennis.connection;

/**
 * Created by Adrian Berisha on 07.04.2017.
 */

public final class Constants {
    private Constants(){}

    public final class CMD {
        private CMD(){}

        public final static String KEY = "cmd";
        public final static String INFO = "INFO";
        public final static String GETINFO = "GET_INFO";
        public final static String CONN = "CONN";
        public final static String RESP = "CONN_RESP";     // Codes:    0 => successful  1 => Lobby full  2 => already connected
        public final static String START = "START";
        public final static String PAUSE = "PAUSE";
        public final static String END = "END";
        public final static String CLOSE = "CLOSE";

    }
    public final static String CODE = "code";
    public final static String NAME = "name";

    public final static int CONN_SUCCESS = 0;   // If the connection is successful
    public final static int CONN_FULL = 1;      // If the lobby is full
    public final static int CONN_EXIST = 2;     // If a connection with this device already exists

    public final static int CLOSE_REQ = 0;      // Send a request to close the connection. (Just notify the other client to close)
    public final static int CLOSE_ACCEPT = 1;   // Like 3-way handshake, if a device close the connection, the other send a close, too. But with code 1
    public final static int CLOSE_REFUSE = 2;   // Refuse the connection request



}
