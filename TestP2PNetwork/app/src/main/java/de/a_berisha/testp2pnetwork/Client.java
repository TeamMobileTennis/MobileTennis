package de.a_berisha.testp2pnetwork;

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

            activity.logAll(activity.INFO, "Connection established successful");

            String message;
            while (true) {

                message = reader.readLine();


                if(message.equals("exit") || message.isEmpty() ) {   // Connection will close
                    break;
                }else {
                    final String tmp = message;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.logAll(activity.INFO,"Received Message: " +  tmp);
                        }
                    });

                }
            }

            activity.logAll(activity.INFO, "Connection with Server closed.");
            closeAll();

        }catch(IOException io){
            activity.logAll(activity.ERROR, io.getMessage());
        }
    }

}
