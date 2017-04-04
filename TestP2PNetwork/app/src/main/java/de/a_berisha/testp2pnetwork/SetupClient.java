package de.a_berisha.testp2pnetwork;

import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by Adrian Berisha on 29.03.2017.
 */

public class SetupClient extends Thread {

    private WifiP2pInfo wifiInfo;
    private int port;
    private MainActivity activity;
    private Socket socket;

    SetupClient(WifiP2pInfo info, int port, MainActivity activity){
        this.wifiInfo = info;
        this.port = port;
        this.activity = activity;
    }

    @Override
    public void run() {
        super.run();

        try {
            socket = new Socket(wifiInfo.groupOwnerAddress, port);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
