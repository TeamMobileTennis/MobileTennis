package de.a_berisha.testp2pnetwork;


import android.os.AsyncTask;
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

public class ServerHandler extends Thread {
    private Socket client;
    private MainActivity activity;

    private String name;

    private BufferedReader reader;
    private PrintWriter writer;

    public ServerHandler(Socket client, MainActivity activity){
        this.client = client;
        this.activity = activity;
    }

    public void sendMessage(final String message) throws IOException{
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
        client.close();
        activity.stopServerHandler(this);
    }

    @Override
    public void run() {
        super.run();

        try {
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            writer = new PrintWriter(client.getOutputStream(), true);

            activity.logAll("Connection established successful");

            String message;
            while (true) {
                message = reader.readLine();
                if(message.equals("exit") || message.isEmpty() ) { // Connection will close
                    break;
                }
                if(message.startsWith("GET_INFO")) {
                    activity.logAll("Get Information for the Game Lobby");
                }else if(message.startsWith("CONN")) {
                    activity.logAll("Device want to connect as Game Client");
                }else {
                    activity.logAll(message);
                }

            }
            activity.logAll(activity.INFO, "Connection with Client closed");
            activity.stopServerHandler(this);

        }catch(IOException io){
            activity.logAll(activity.ERROR, io.getMessage());
        }
    }
}
