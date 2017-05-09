package com.crazyking.mobiletennis.connection;

import android.content.Context;
import android.graphics.Path;
import android.view.View;

import com.crazyking.mobiletennis.connection.Client.ClientPeerConn;
import com.crazyking.mobiletennis.connection.Server.ServerPeerConn;

/**
 * Created by Adrian Berisha on 09.05.2017.
 */

public class ConnectionFactory {
    private ConnectionFactory(){}

    /**
     * Creates a Client or Server/Host
     * @param type      Type which to create in String (HOST,SERVER,CLIENT)
     * @param context   Application Context
     * @param view      ViewPeerInterface of this application
     * @return          Returns the right Operator
     */
    public static boolean createConnection(String type, Context context, ViewPeerInterface view){
        if(type.equalsIgnoreCase("HOST")) {
            ServerPeerConn.getInstance(context, view).createLobby("");
            return true;
        }else if(type.equalsIgnoreCase("SERVER")) {
            ServerPeerConn.getInstance(context, view).createLobby("");
            return true;
        }else if(type.equalsIgnoreCase("CLIENT")) {
            ClientPeerConn.getInstance(context, view).startPeerDiscover();
            return true;
        }
        return false;
    }

    /**
     * Static method that create a server peer connection
     * @param context       Application Context
     * @param view          ViewPeerInterface of Application
     * @param lobbyName     name of Game Lobby
     * @return              Returns operator
     */
    public static Operator createHost(Context context, ViewPeerInterface view, String lobbyName){
        ServerPeerConn serverPeerConn = ServerPeerConn.getInstance(context, view);
        serverPeerConn.createLobby(lobbyName);
        return serverPeerConn;
    }

    /**
     * Static method that create a client peer connection
     * @param context       Application Context
     * @param view          ViewPeerInterface of Application
     * @return              Returns operator
     */
    public static Operator createClient(Context context, ViewPeerInterface view){
        ClientPeerConn clientPeerConn = ClientPeerConn.getInstance(context,view);
        clientPeerConn.startPeerDiscover();
        return clientPeerConn;
    }

    /**
     * Static method that connects to a p2p device with the specific mac-address
     * @param operator      The operator which should be a Client Peer connection
     * @param adr          The Mac-Address of host in string
     * @throws Exception    Throws Exception operator is not a client peer connection
     */
    public static void connectToGame(Operator operator, String adr)throws Exception{
        ClientPeerConn cl = (ClientPeerConn)operator;
        cl.connectToGame(adr);
    }

}
