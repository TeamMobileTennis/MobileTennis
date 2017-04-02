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

public class SendData extends AsyncTask<String, Void, Void> {

    private WifiP2pInfo wifiInfo;
    private int port;
    private MainActivity activity;

    SendData(WifiP2pInfo info, int port, MainActivity activity){
        this.wifiInfo = info;
        this.port = port;
        this.activity = activity;
    }

    public void setWifiInfo(WifiP2pInfo info){
        this.wifiInfo = info;
    }

    @Override
    protected Void doInBackground(String... params) {

        try {
            if(params != null) {
                for (String message : params) {

                    if (wifiInfo != null) {
                        Socket socket = new Socket(wifiInfo.groupOwnerAddress, port);
                        Log.d(activity.INFO, "Sending this message to the Server: " + message);

                        OutputStream os = socket.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
                        writer.write(message);

                        writer.close();
                        os.close();
                        socket.close();
                        Log.d(activity.INFO, "Sending finished");
                    } else {
                        Log.d(activity.INFO, "info is null");
                    }
                }
            }else {
                activity.logAll(activity.INFO, "No Data to send");
            }

        }catch(IOException io){
            io.printStackTrace();
            activity.logAll(activity.ERROR,"IOException: "+io.getMessage());
        }catch(IllegalArgumentException ia){
            ia.printStackTrace();
            activity.logAll(activity.ERROR,"IllegalArgumentException: "+ia.getMessage());
        }catch(NullPointerException np){
            np.printStackTrace();
            activity.logAll(activity.ERROR,"NullPointerException: "+np.getMessage());
        }catch(Exception e){
            e.printStackTrace();
            activity.logAll(activity.ERROR,"Exception: " +e.getMessage());
        }


        return null;
    }
}
