package com.crazyking.mobiletennis.connection.Server;


import android.util.Log;

import com.crazyking.mobiletennis.connection.Constants;
import com.crazyking.mobiletennis.connection.Messages;
import com.crazyking.mobiletennis.connection.ViewPeerInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


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


    public ServerHandler(Socket client, GameLobby gameLobby, ViewPeerInterface view){
        this.client = client;
        this.gameLobby = gameLobby;
        this.view = view;
        Log.d("INFO","Created Connection with "+ this.client.getInetAddress().getHostAddress());
    }

    public void sendMessage(String message){
        if(writer!=null) {
            writer.println(message);
        }else
            Log.d("ERROR","Cannot send message");
    }

    public void closeConn()throws IOException{
        close = true;
        reader.close();
        writer.close();
        client.close();
        gameLobby.stopServerHandler(this);
        this.interrupt();
    }

    public void requestClose(){
        sendMessage(Messages.getDataStr(Constants.CMD.CLOSE, Constants.CODE, Integer.toString(Constants.CLOSE_REQ)));
    }

    @Override
    public void run() {
        super.run();

        try {
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            writer = new PrintWriter(client.getOutputStream(), true);



            String message = "";
            while (!close) {
                message = reader.readLine();
                Log.d("INFO", message+"");
                if(message != null) {

                    if(message.equalsIgnoreCase(Constants.CMD.CLOSE)){
                        requestClose();
                    }
                    if (message.equals("exit") || message.isEmpty()) {      // Connection will close
                        break;
                    }
                    try {
                        String cmdStr = Messages.getCommand(message);
                        Log.d("INFO", "Command: " + cmdStr);
                        if (!cmdStr.isEmpty()) {
                            if (cmdStr.equalsIgnoreCase(Constants.CMD.GETINFO)) {
                                sendMessage(gameLobby.getInformation().toString());
                            } else if (cmdStr.equalsIgnoreCase(Constants.CMD.CONN)) {
                                playerName = Messages.getValue(message, Constants.NAME);
                                if (gameLobby.connectAsGameClient(this, playerName)) {
                                    sendMessage(Messages.getDataStr(Constants.CMD.RESP, Constants.CODE, Integer.toString(Constants.CONN_SUCCESS)));
                                    Log.d("INFO", playerName + "connected.");

                                    sendMessage("Welcome in the Lobby");
                                    countGame();

                                    // Information changed. Send new Information
                                    gameLobby.sendMessage(gameLobby.getInformation().toString());
                                } else
                                    sendMessage(Messages.getDataStr(Constants.CMD.RESP, Constants.CODE, Integer.toString(Constants.CONN_FULL)));
                            } else if (cmdStr.equalsIgnoreCase(Constants.CMD.CLOSE)) {

                                if (Messages.getDataMap(message).size() <= 1) {
                                    sendMessage(Messages.getDataStr(Constants.CMD.CLOSE, Constants.CODE, Integer.toString(Constants.CLOSE_ACCEPT)));
                                } else {

                                    String strCode = Messages.getValue(message, Constants.CODE);
                                    if (!strCode.isEmpty()) {
                                        int code = Integer.parseInt(strCode);
                                        if (code == Constants.CLOSE_REQ) {
                                            sendMessage(Messages.getDataStr(Constants.CMD.CLOSE, Constants.CODE, Integer.toString(Constants.CLOSE_ACCEPT)));
                                        } else if (code == Constants.CLOSE_ACCEPT) {
                                            view.passMessage("Log: " + "Connection closed.");
                                            break;
                                        } else if (code == Constants.CLOSE_REFUSE) {
                                            view.passMessage("Cannot close the connection");
                                        }
                                    }
                                }
                            }
                        } else {
                            view.passMessage(message);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        Log.d("ERROR", e.getMessage() + "");
                    }
                }

            }
            Log.d("INFO","Connection with a Client closed");
            closeConn();

        }catch(Exception io){
            Log.d("ERROR",io.getMessage()+"");
        }
    }

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
