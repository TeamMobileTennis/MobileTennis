package de.a_berisha.testp2pnetwork;

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
        public final static String RESP = "CONN_RESP";
        public final static String START = "START";
        public final static String PAUSE = "PAUSE";
        public final static String END = "END";
        public final static String CLOSE = "CLOSE";

    }
    public final static String CODE = "code";
    public final static String NAME = "name";

    public final static int CONN_SUCCESS = 0;
    public final static int CONN_FULL = 1;


}
