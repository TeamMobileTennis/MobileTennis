package de.a_berisha.testp2pnetwork;

import android.icu.text.IDNA;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Adrian Berisha on 03.04.2017.
 */

/*
    Requests to Server

    GET_INFO    => Get Information about the Game Lobby
    CONN        => Connect to Server as Game Client

    INFO{"Name of the Lobby", "Name of Player 1", "Name of Player 2"}
        (If Player 1 or 2 or both not exists, but a empty String in it)
    CONN_RESP{NUMBER}
        => Answer from Server, with Information
            0 => Connection success
            1 => Lobby full

 */

public class Client extends Thread {

    private Socket server;
    private MainActivity activity;


    private BufferedReader reader;
    private PrintWriter writer;

    public Client(Socket server, MainActivity activity){
        this.server = server;
        this.activity = activity;
    }

    public void sendMessage(final String message){
        new Thread(new Runnable() {
            @Override
            public void run() {
                writer.println(message);
            }
        }).start();
    }

    public void closeAll()throws IOException{
        reader.close();
        writer.close();
        server.close();
        activity.stopClient(this);
    }


    @Override
    public void run() {
        super.run();

        try {

            reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
            writer = new PrintWriter(server.getOutputStream(), true);

            activity.logAll("Connection established successful");

            String message;
            while (true) {

                message = reader.readLine();

                if(message.equals("exit") || message.isEmpty() ) {   // Connection will close
                    break;
                }
                try {
                    if (message.startsWith("CONN_RESP")) {
                        int code = Integer.parseInt(MessageEncode.getData(message));
                        activity.logAll(code == 0 ? "Connection successful" : "Lobby is full");

                    } else if (message.startsWith("INFO")) {
                        Information info = new Information(message);
                        activity.logAll("Lobby-Name: "+info.getLobbyName()+"\nPlayer 1: "+info.getPlayer1()+"\nPlayer 2: "+info.getPlayer2());

                    } else if (message.startsWith("START")) {
                        activity.logAll("Game starts");

                    } else if (message.startsWith("END")) {
                        activity.logAll("Game finished");

                    } else {
                        activity.logAll("Received Message: "+message);

                    }
                }catch(Exception e){
                    activity.logAll("ERROR",e.getMessage());
                }

            }

            activity.logAll("Connection with Server closed.");
            closeAll();

        }catch(IOException io){
            activity.logAll(activity.ERROR, io.getMessage());
        }
    }

}
