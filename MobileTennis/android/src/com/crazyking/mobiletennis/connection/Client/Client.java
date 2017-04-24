package com.crazyking.mobiletennis.connection.Client;

import android.net.wifi.p2p.WifiP2pInfo;
import android.util.Log;

import com.crazyking.mobiletennis.connection.Constants;
import com.crazyking.mobiletennis.connection.Information;
import com.crazyking.mobiletennis.connection.Messages;
import com.crazyking.mobiletennis.connection.ViewPeerInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

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
        Log.d("INFO", "Host: " + host.getHostAddress() + " Port: " + port);
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
        close = true;
        reader.close();
        writer.close();
        server.close();
        this.interrupt();
    }
    public void requestClose(){
        sendMessage(Messages.getDataStr(Constants.CMD.CLOSE, Constants.CODE, Integer.toString(Constants.CLOSE_REQ)));
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


    @Override
    public void run() {
        super.run();

        try {
            server = new Socket();
            server.setReuseAddress(true);
            server.connect(new InetSocketAddress(host, port));

            Log.d("INFO", "Created Socket");

            reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
            writer = new PrintWriter(server.getOutputStream(), true);

            String message;
            while (!close) {

                message = reader.readLine();

                if (message != null) {
                    if(message.equalsIgnoreCase(Constants.CMD.CLOSE)){
                        requestClose();
                    }

                    if (message.equals("exit") || message.isEmpty()) {   // Connection will close
                        break;
                    }
                    try {
                        String cmd = Messages.getCommand(message);

                        if (!cmd.isEmpty()) {

                            if (cmd.equalsIgnoreCase(Constants.CMD.RESP)) {
                                int code = Integer.parseInt(Messages.getValue(message, Constants.CODE));
                                if (code == 0) {
                                    game = true;
                                    view.passMessage("Log: " + "Connecting to Game successful");
                                } else {
                                    game = false;
                                    view.passMessage("Log: " + "Lobby is full! Try another one.");
                                    Log.d("INFO", "Lobby is full");
                                }
                            } else if (cmd.equalsIgnoreCase(Constants.CMD.INFO)) {
                                information.strToInfo(message);
                                view.passInformation(information);
                                view.passMessage(information.getLobbyName() + " Dev-Addr: " + information.getAddress());
                            } else if (cmd.equalsIgnoreCase(Constants.CMD.START)) {
                                view.passMessage("Log: " + "Game starts!");
                            } else if (cmd.equalsIgnoreCase(Constants.CMD.PAUSE)) {
                                view.passMessage("Log: " + "Game paused!");
                            } else if (cmd.equalsIgnoreCase(Constants.CMD.END)) {
                                view.passMessage("Log: " + "Game ends!");
                            } else if (cmd.equalsIgnoreCase(Constants.CMD.CLOSE)) {
                                if (Messages.getDataMap(message).size() <= 1) {
                                    sendMessage(Messages.getDataStr(Constants.CMD.CLOSE, Constants.CODE, Integer.toString(Constants.CLOSE_ACCEPT)));
                                } else {

                                    String strCode = Messages.getValue(message, Constants.CODE);
                                    if (!strCode.isEmpty()) {
                                        int code = -1;
                                        try {
                                            code = Integer.parseInt(strCode);
                                        } catch (NumberFormatException e) {
                                            e.printStackTrace();
                                        }
                                        if (code == Constants.CLOSE_REQ) {
                                            sendMessage(Messages.getDataStr(Constants.CMD.CLOSE, Constants.CODE, Integer.toString(Constants.CLOSE_ACCEPT)));
                                        } else if (code == Constants.CLOSE_ACCEPT) {
                                            view.passMessage("Log: " + "Connection closed.");
                                            break;
                                        } else if (code == Constants.CLOSE_REFUSE) {
                                            view.passMessage("Cannot close the connection");
                                        }
                                    }
                                }

                            }

                        } else {
                            view.passMessage(message);
                        }

                    } catch (Exception e) {
                        Log.d("ERROR", e.getMessage() + "");

                    }
                }

            }

            Log.d("INFO","Connection with Server closed");
            closeConn();

        }catch(Exception io){
            Log.d("ERROR",io.getMessage()+"");
        }
    }

}
