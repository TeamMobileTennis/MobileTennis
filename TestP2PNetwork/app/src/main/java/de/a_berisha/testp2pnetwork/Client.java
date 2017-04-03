package de.a_berisha.testp2pnetwork;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by Adrian Berisha on 03.04.2017.
 */

public class Client extends Thread {

    private Socket server;
    private MainActivity activity;


    private BufferedReader reader;
    private BufferedWriter writer;

    public Client(Socket server, MainActivity activity){
        this.server = server;
        this.activity = activity;
    }

    public void sendMessage(String message) throws IOException{
        writer.write(message);
    }


    @Override
    public void run() {
        super.run();

        try {

            reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));

            activity.logAll(activity.INFO, "Connection established successful");

            String message;
            while (true) {
                message = reader.readLine();
                if(message == "exit" || message == "" || message == null) {
                    activity.logAll(activity.INFO, "Connection will close");
                    break;
                }else {
                    activity.logAll(activity.INFO,"Received Message: " +  message);
                }
            }

            activity.logAll(activity.INFO, "Connection with Server closed.");
            server.close();

        }catch(IOException io){
            activity.logAll(activity.ERROR, io.getMessage());
        }
    }
}
