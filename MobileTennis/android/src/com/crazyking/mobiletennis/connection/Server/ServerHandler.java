package com.crazyking.mobiletennis.connection.Server;


import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import com.crazyking.mobiletennis.connection.Constants;
import com.crazyking.mobiletennis.connection.Messages;
import com.crazyking.mobiletennis.connection.ViewPeerInterface;

import static com.crazyking.mobiletennis.connection.Constants.CMD.*;
import static com.crazyking.mobiletennis.connection.Constants.*;

/**
 * Created by Adrian Berisha on 03.04.2017.
 */

public class ServerHandler extends Thread{
    private Socket client;
    private GameLobby gameLobby;
    private ViewPeerInterface view;

    private String playerName;

    private BufferedReader reader;
    private PrintWriter writer;

    private boolean close = false;


    /**
     * Constructor for Server-Handler (handle connection with clients)
     * @param client    The socket for client connection
     * @param gameLobby The current game lobby
     * @param view      The view to pass messages and info
     */
    public ServerHandler(Socket client, GameLobby gameLobby, ViewPeerInterface view){
        this.client = client;
        this.gameLobby = gameLobby;
        this.view = view;
        Log.d("INFO","Created Connection with "+ this.client.getInetAddress().getHostAddress());
    }

    /**
     * Send Message to client
     * @param message   A String that send to client
     */
    public void sendMessage(String message){
        if(writer!=null) {
            writer.println(message);
        }else
            Log.d("ERROR","Cannot send message");
    }

    /**
     * request close with client
     */
    public void closeConn(){
        if(!close) {
            requestClose();
        }
    }

    /**
     * Stop connection with client
     * @throws IOException  If an error occurs to close reader and writer
     */
    private void stopConn()throws IOException{
        close = true;
        reader.close();
        writer.close();
        client.close();
        gameLobby.stopServerHandler(this);
        if(this.isAlive())
            this.interrupt();
    }

    /**
     * Request to close connection with client
     */
    public void requestClose(){
        sendMessage(Messages.getDataStr(Constants.CMD.CLOSE, Constants.CODE, Integer.toString(Constants.CLOSE_REQ)));
    }

    /**
     * Accepts the close request
     * @throws IOException  if connection cannot be stopped
     */
    private void acceptClose()throws IOException{
        sendMessage(Messages.getDataStr(Constants.CMD.CLOSE, Constants.CODE, Integer.toString(Constants.CLOSE_ACCEPT)));
        stopConn();
    }

    /**
     * Handle the close codes
     * @param code          The code receives from client
     * @return              If it failed or not
     * @throws IOException  If connection cannot be stopped
     */
    private boolean handleClose(int code)throws IOException{
        if(code == CLOSE_REQ)
            acceptClose();
        else if(code == CLOSE_ACCEPT){
            stopConn();
        }else if(code == CLOSE_REFUSE){
            return false;
        }else {
            return false;
        }
        return true;
    }

    /**
     * Updates view to pass any messages
     * @param view  ViewPeerInterface which implements Methods
     *              for connection and game information
     */
    public void setView(ViewPeerInterface view) {
        this.view = view;
    }

    @Override
    public void run() {
        super.run();

        try {
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            writer = new PrintWriter(client.getOutputStream(), true);



            String message;
            while (!close) {
                message = reader.readLine();
                Log.d("INFO", message+"");
                if(message != null) {

                    try {
                        handleCommands(message);

                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
            Log.d("INFO","Connection with a Client closed");
            stopConn();

        }catch(Exception io){
            Log.d("ERROR",io.getMessage()+"");
        }
    }

    /**
     * Handles incoming Messages from client
     * @param message       txtRecord with the right syntax
     * @throws Exception    if error occurs with handling messages
     */
    private void handleCommands(String message)throws Exception{
        String cmd = Messages.getCommand(message);
        Log.d("String_serve", message);

        if (!cmd.isEmpty()) {
            switch (cmd) {
                case GETINFO:
                    sendMessage(gameLobby.getInformation().toString());
                    break;
                case CONN:
                    playerName = Messages.getValue(message, NAME);
                    if (gameLobby.connectAsGameClient(this, playerName)) {
                        sendMessage(Messages.getDataStr(RESP, CODE, Integer.toString(CONN_SUCCESS)));
                        gameLobby.sendMessage(gameLobby.getInformation().toString());


                        // Send only to view, if connection accepted
                        view.passMessage(addPlayerCode(message));

                    } else
                        sendMessage(Messages.getDataStr(RESP, CODE, Integer.toString(CONN_FULL)));
                    break;
                case CLOSE:
                    if (Messages.getDataMap(message).size() <= 1) {
                        acceptClose();
                    } else {
                        String strCode = Messages.getValue(message, CODE);
                        if (!strCode.isEmpty()) {
                            int code = -1;
                            try {
                                code = Integer.parseInt(strCode);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                            if (!handleClose(code)) {
                                Log.d("INFO", "Connection failed to closed");
                            }else {
                                view.passMessage(addPlayerCode(Messages.getDataStr(CLOSE)));
                            }

                        }
                    }
                    break;

                default:
                    view.passMessage(addPlayerCode(message));
            }
        }else {
            view.passMessage(addPlayerCode(message));

        }
    }
    private String addPlayerCode(String message){
        if(Messages.getCommand(message).isEmpty()){
            // Only message without Commands
            // Convert to Data String and add PlayerCode
            // with 1 or 2 to identify the players at the view
            return Messages.getDataStr(OTHER,MESSAGE,message,PLAYER_CODE,Integer.toString(gameLobby.getPlayerPos(this)));
        }else {
            // Add the PlayerCode to the other Key-Value-Pairs
            // to identify the players at the view
            HashMap<String, String> map = Messages.getDataMap(message);
            map.put(PLAYER_CODE,Integer.toString(gameLobby.getPlayerPos(this)));
            return Messages.getDataStr(map);
        }
    }
    /**
     * Count Game from ten to zero
     */
    public void countGame(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 10; i > 0; i--) {
                        sendMessage("Game starts in "+i+" seconds");
                        Thread.sleep(1000);
                    }
                    sendMessage(Messages.getDataStr(Constants.CMD.START));
                    sendMessage("Game has started");
                }catch (InterruptedException e){
                    Log.d("ERROR",e.getMessage()+"");
                }
            }
        }).start();
    }
}
