package de.a_berisha.testp2pnetwork;


import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import static de.a_berisha.testp2pnetwork.Constants.*;
import static de.a_berisha.testp2pnetwork.Constants.CMD.*;

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
    }

    public void sendMessage(String message){
        writer.println(message);
    }

    public void closeConn()throws IOException{
        close = true;
        reader.close();
        writer.close();
        client.close();
        gameLobby.stopServerHandler(this);
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
                if(message.equals("exit") || message.isEmpty() ) {      // Connection will close
                    break;
                }
                try {
                    String cmdStr = Messages.getCommand(message);
                    if (!cmdStr.isEmpty()) {
                        if (message.equalsIgnoreCase(GETINFO)) {
                            sendMessage(gameLobby.getInformation().toString());
                        } else if (message.equalsIgnoreCase(CONN)) {
                            playerName = Messages.getDataMap(message).get(NAME);
                            if(gameLobby.connectAsGameClient(this, playerName)) {
                                sendMessage(Messages.getDataStr(RESP, CODE, Integer.toString(CONN_SUCCESS)));
                                gameLobby.sendMessage(gameLobby.getInformation().toString());
                            }else
                                sendMessage(Messages.getDataStr(RESP, CODE, Integer.toString(CONN_FULL)));
                        } else if (message.equalsIgnoreCase(CLOSE)){
                            closeConn();
                        }
                    } else {
                        view.passMessage(message);
                    }
                }catch (Exception e){
                    Log.d("ERROR", e.getMessage());
                }

            }
            Log.d("INFO","Connection with a Client closed");
            closeConn();

        }catch(IOException io){
            Log.d("ERROR",io.getMessage());
        }
    }
}
