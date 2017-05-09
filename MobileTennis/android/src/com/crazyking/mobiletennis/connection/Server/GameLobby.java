package com.crazyking.mobiletennis.connection.Server;

import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import com.crazyking.mobiletennis.connection.Information;
import com.crazyking.mobiletennis.connection.ViewPeerInterface;

/**
 * Created by Adrian Berisha on 06.04.2017.
 */

public class GameLobby extends Thread{

    private Information lobbyInformation;
    private ServerSocket serverSocket;
    private ViewPeerInterface view;

    private boolean close = false;

    // With P2P 254 Connections are allowed, because of the IP-Address.
    // The Default IP-Address Range is between 192.168.49.1 to 192.168.49.255
    private ServerHandler[] clients = new ServerHandler[254-2];         // Connection to Clients
    private ServerHandler[] gameClients = new ServerHandler[2];         // Only 2 Game Clients are allowed


    /**
     * Constructor of Game Lobby
     * @param lobbyName     Name of game lobby
     * @param port          Port for sockets
     * @param view          View to pass messages and info
     * @param address       Mac-Address of current device
     * @throws IOException  If serversocket binding failed
     */
    public GameLobby(String lobbyName, int port, ViewPeerInterface view, String address) throws IOException{
        lobbyInformation = new Information(lobbyName, "", "", address);

        serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true);             // ReUse Address
        serverSocket.bind(new InetSocketAddress(port));
    }

    /**
     * Send Messages to all Clients
     * @param message   The Message to be send to all Game-Clients
     */
    public void sendMessage(String message) {
        for(int i=0; i<gameClients.length; i++){
            if(gameClients[i] != null) {
                if(gameClients[i].isAlive()) {
                    gameClients[i].sendMessage(message);
                }
            }
        }
    }

    /**
     * Close connection with all game-clients
     * @throws IOException  Throws an IOException if a connection cannot be closed.
     */
    public void closeConn() throws IOException {
        for(int i=0; i<gameClients.length; i++){
            gameClients[i].closeConn();
        }
    }


    @Override
    public void run(){
        super.run();

        while(!close){
            try {
                int pos = getFreePosAtServerHandler(clients);

                if (pos >= 0) {
                    clients[pos] = new ServerHandler(serverSocket.accept(), this, view);
                    if(!close)      // Check here for close, because the accept-Method blocks the Thread
                        clients[pos].start();
                }else {
                    Log.d("ERROR", "Too many Connections");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    /**
     * @return  Returns Information about the Game-Lobby
     */
    public Information getInformation(){
        return lobbyInformation;
    }

    /**
     * Close this Game Lobby and notify all Clients and Game-Clients that the Lobby
     * get closed.
     *
     * @throws IOException      Throws a IOException if a connection cannot be closed
     */
    public void closeLobby()throws IOException {
        close = true;

        closeAllClients();
        for(int i=0; i<gameClients.length; i++){
            if(gameClients[i] != null){
                if(gameClients[i].isAlive())
                    gameClients[i].closeConn();
            }
        }
        serverSocket.close();
        this.interrupt();
    }

    /**
     * Update all with the new View
     *
     * @param view  The new view, to send messages and Co
     */
    public void setView(ViewPeerInterface view) {
        this.view = view;
        for(ServerHandler handler : clients){
            if(handler != null){
                handler.setView(view);
            }
        }
        for(ServerHandler handler : gameClients){
            if(handler != null){
                handler.setView(view);
            }
        }
    }

    /**
     *  Register the Client as Game-Client and check if lobby is full or not
     *
     * @param client        Object which connect to the GameClient
     * @param playerName    Player Name of the Game Client
     * @return              Returns true if connections is successful and false if the lobby is full
     */
    public boolean connectAsGameClient(ServerHandler client, String playerName){
        int pos = getFreePosAtServerHandler(gameClients);
        if(pos < 0){
            return false;
        }else {
            gameClients[pos] = client;
            clients[search(client)] = null;
            if(!lobbyInformation.setPlayerName(pos+1,playerName)){
                return false;
            }
            return true;
        }
    }

    /**
     *  Close all Connections to clients, which are no Game-Clients
     */
    public void closeAllClients(){
        for (int i = 0; i < clients.length; i++) {
            if(clients[i] != null)
                if(clients[i].isAlive())
                    clients[i].closeConn();
        }
    }

    /**
     *  Notify the Lobby that a Client close the connection
     *
     * @param client    The Clients which close the Connection
     */
    public void stopServerHandler(ServerHandler client){
        int pos = -1;
        for(int i=0;i<clients.length;i++){
            if(clients[i] == client){
                pos = i;
                break;
            }
        }
        if(pos >= 0){
            clients[pos] = null;
        }
        pos = -1;
        for(int i=0; i<gameClients.length; i++){
            if(gameClients[i] == client){
                pos = i;
                break;
            }
        }
        if(pos >=0){
            gameClients[pos] = null;
        }

    }

    /**
     *  Get a free Position to save a new client connection
     *
     * @param handler   An Array of ServerHandler's which includes clients connections
     * @return          Returns the first empty position in the array
     */
    private int getFreePosAtServerHandler(ServerHandler[] handler){
        int pos = -1;

        for(int i=0; i<handler.length; i++){
            if(handler[i] == null){
                pos = i;
                break;
            }
        }

        return pos;
    }

    /**
     * Search for the same ServerHandler in the Array "clients" and returns it's position.
     *
     * @param handler   The ServerHandler which to search in the array "clients"
     * @return          Returns the position of the ServerHandler in the array
     */
    private int search(ServerHandler handler){
        int pos = -1;
        for(int i=0; i<clients.length; i++){
            if(clients[i] == handler){
                pos = i;
                break;
            }
        }
        return pos;
    }


}
