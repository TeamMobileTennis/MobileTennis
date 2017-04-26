package de.a_berisha.testp2pnetwork.connection.Server;


import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import de.a_berisha.testp2pnetwork.connection.Constants;
import de.a_berisha.testp2pnetwork.connection.Messages;
import de.a_berisha.testp2pnetwork.connection.ViewPeerInterface;

import static de.a_berisha.testp2pnetwork.connection.Constants.CMD.*;
import static de.a_berisha.testp2pnetwork.connection.Constants.*;

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
        if(this.isAlive())
            this.interrupt();
    }

    public void requestClose(){
        sendMessage(Messages.getDataStr(Constants.CMD.CLOSE, Constants.CODE, Integer.toString(Constants.CLOSE_REQ)));
    }
    private void acceptClose(){
        sendMessage(Messages.getDataStr(Constants.CMD.CLOSE, Constants.CODE, Integer.toString(Constants.CLOSE_ACCEPT)));
    }
    private boolean handleClose(int code)throws IOException{
        if(code == CLOSE_REQ)
            acceptClose();
        else if(code == CLOSE_ACCEPT){
            closeConn();
        }else if(code == CLOSE_REFUSE){
            return false;
        }else {
            return false;
        }
        return true;
    }

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
            closeConn();

        }catch(Exception io){
            Log.d("ERROR",io.getMessage()+"");
        }
    }

    public void handleCommands(String message)throws Exception{
        String cmd = Messages.getCommand(message);

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

                        // Just for testing:
                        sendMessage("Welcome in the Lobby");
                        countGame();
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
                            }

                        }
                    }
                    break;

                default:
                    view.passMessage(message);
            }
        }else {
            view.passMessage(message);
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
