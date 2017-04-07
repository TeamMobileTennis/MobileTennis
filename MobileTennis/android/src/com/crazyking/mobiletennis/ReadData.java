package com.crazyking.mobiletennis;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Adrian Berisha on 29.03.2017.
 */

public class ReadData extends AsyncTask<Void, Void, Void> {


    private int port;
    private AndroidLauncher activity;

    private boolean run = true;

    ReadData(int port, AndroidLauncher activity){
        this.port = port;
        this.activity = activity;
    }

    public void setRun(boolean run){
        this.run = run;
    }
    public boolean getRun(){
        return run;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            ServerSocket socket = new ServerSocket();
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(port));

            while(run) {

                activity.logAll(activity.INFO, "Im ready to receive data");
                Socket client = socket.accept();

                InputStream is = client.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String message;
                char[] buffer = new char[1024];
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    message = new String(buffer, 0, n);
                    activity.logAll(activity.INFO, message);
                }

                reader.close();
                is.close();
                client.close();
            }
            socket.close();
            activity.logAll(activity.INFO, "Close the Server Socket");
        }catch(IOException io){
            io.printStackTrace();
            activity.logAll(activity.ERROR,"IOException: "+io.getMessage());
        }catch(Exception e){
            e.printStackTrace();
//                    activity.logAll(activity.ERROR,"Exception: " +e.getMessage());
            Log.d(activity.ERROR, "Error in receive-function");
        }

        return null;
    }
}
