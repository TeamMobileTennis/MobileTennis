package de.a_berisha.testp2pnetwork;

import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Adrian Berisha on 29.03.2017.
 */

public class ReadData extends Thread {
    Socket socket;
    String data;
    TextView textView; // To Show Data

    public ReadData(Socket socket, TextView textView){
        this.socket = socket;
        this.textView = textView;
    }

    @Override
    public void run() {
        super.run();

        byte[] bytes = new byte[1024];

        try {
            InputStream is = socket.getInputStream();

            int n;
            while((n = is.read(bytes)) != -1){
                data = new String(bytes, 0, n);
                textView.append(data+'\n');
            }

        }catch (IOException io){
            io.printStackTrace();
        }
    }
}
