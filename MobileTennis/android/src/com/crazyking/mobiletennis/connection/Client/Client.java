package com.crazyking.mobiletennis.connection.Client;

import android.app.Notification;
import android.net.wifi.p2p.WifiP2pInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.crazyking.mobiletennis.connection.Constants;

import static com.crazyking.mobiletennis.connection.Constants.*;
import static com.crazyking.mobiletennis.connection.Constants.CMD.*;
import com.crazyking.mobiletennis.connection.Information;
import com.crazyking.mobiletennis.connection.Messages;
import com.crazyking.mobiletennis.connection.ViewPeerInterface;

/**
 * Created by Adrian Berisha on 03.04.2017.
 */

/*
    Requests to Server

    GET_INFO                    => Get Information about the Game Lobby
    CONN{Name of Player}        => Connect to Server as Game Client

    INFO{"Name of the Lobby", "Name of Player 1", "Name of Player 2"}   Also need MAC-ADDRESS
        (If Player 1 or 2 or both not exists, but a empty String in it)
    CONN_RESP{NUMBER}
        => Answer from Server, with Information
            0 => Connection success
            1 => Lobby full

 */

public class Client extends Thread{

    private Socket server;
    private ViewPeerInterface view;
    private InetAddress host;
    private int port;

    private String playerName;          // Name of this Client

    private BufferedReader reader = null;
    private PrintWriter writer = null;

    private boolean close = false;
    private boolean game = false;       // Is this Client a Game Client

    private Information information = new Information();


    public Client(WifiP2pInfo wifiInfo, int port, ViewPeerInterface view, String playerName){
        this.view = view;
        this.playerName = playerName;
        this.host = wifiInfo.groupOwnerAddress;
        this.port = port;
        Log.d("INFO", "Created Client");
    }

    public void sendMessage(final String message) {
        if(checkConn()) {
            writer.println(message);
        }
        else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(!checkConn());
                    writer.println(message);
                }
            }).start();
        }
    }

    public void closeConn()throws IOException{
        if(!close) {
            requestClose();
            Log.d("INFO","Request to close");
        }
    }

    private void stopConn()throws IOException{
        close = true;
        reader.close();
        writer.close();
        server.close();
        if(this.isAlive())
            this.interrupt();
    }

    public void requestClose(){
        sendMessage(Messages.getDataStr(Constants.CMD.CLOSE, Constants.CODE, Integer.toString(CLOSE_REQ)));
    }
    private void acceptClose()throws IOException{
        sendMessage(Messages.getDataStr(Constants.CMD.CLOSE, Constants.CODE, Integer.toString(Constants.CLOSE_ACCEPT)));
        stopConn();
    }
    private boolean handleClose(int code)throws IOException{
        if(code == CLOSE_REQ)
            acceptClose();
        else if(code == CLOSE_ACCEPT){
            stopConn();
        }else if(code == CLOSE_REFUSE){
            return false;
        }else {
            return false;
        }
        return true;
    }

    public boolean checkConn(){
        if(server == null)
            return false;
        if(!server.isConnected())
            return false;
        if(server.isClosed())
            return false;
        if(reader == null)
            return false;
        if(writer == null)
            return false;

        return true;
    }

    public void setView(ViewPeerInterface view){
        this.view = view;
    }


    @Override
    public void run() {
        super.run();

        try {
            server = new Socket();
            server.setReuseAddress(true);
            server.connect(new InetSocketAddress(host, port));

            reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
            writer = new PrintWriter(server.getOutputStream(), true);

            String message;
            while (!close) {

                message = reader.readLine();

                if (message != null) {

                    try {
                        handleCommands(message);

                    } catch (Exception e) {
                        Log.d("ERROR", e.getMessage() + "");

                    }
                }

            }

            Log.d("INFO","Connection with Server closed");
            stopConn();

        }catch(Exception io){
            Log.d("ERROR",io.getMessage()+"");
        }
    }

    private void handleCommands(String message)throws Exception{
        String cmd = Messages.getCommand(message);

        if (!cmd.isEmpty()) {

            switch (cmd){
                case INFO:
                    information.strToInfo(message);
                    view.passInformation(information);
                    break;

                case CLOSE:
                    if(Messages.getDataMap(message).size()<=1){
                        acceptClose();
                    }else {
                        String strCode = Messages.getValue(message, CODE);
                        if(!strCode.isEmpty()){
                            int code = -1;
                            try {
                                code = Integer.parseInt(strCode);
                            }catch (NumberFormatException e){
                                e.printStackTrace();
                            }
                            if(!handleClose(code)){
                                Log.d("INFO","Connection failed to closed");
                            }else {
                                view.passMessage(Messages.getDataStr(CLOSE));
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

}
