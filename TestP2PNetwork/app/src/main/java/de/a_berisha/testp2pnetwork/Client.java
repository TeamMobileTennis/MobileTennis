package de.a_berisha.testp2pnetwork;

import android.net.wifi.p2p.WifiP2pInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import static de.a_berisha.testp2pnetwork.Constants.*;
import static de.a_berisha.testp2pnetwork.Constants.CMD.*;

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

public class Client extends Thread implements ConnectionHandler{

    private Socket server;
    private ViewPeerInterface view;
    private InetAddress host;
    private int port;

    private String playerName;          // Name of this Client

    private BufferedReader reader;
    private PrintWriter writer;

    private boolean close = false;
    private boolean game = false;       // Is this Client a Game Client

    public Client(Socket server, ViewPeerInterface view, String playerName){
        this.server = server;
        this.playerName = playerName;
        this.view = view;
    }

    public Client(WifiP2pInfo wifiInfo, int port, ViewPeerInterface view, String playerName){
        this.view = view;
        this.playerName = playerName;
        this.host = wifiInfo.groupOwnerAddress;
        this.port = port;
    }

    public void sendMessage(String message){
        writer.println(message);
    }

    public void closeConn()throws IOException{
        close = true;
        reader.close();
        writer.close();
        server.close();
        this.interrupt();
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

                if(message.equals("exit") || message.isEmpty() ) {   // Connection will close
                    break;
                }
                try {
                    String cmd = Messages.getCommand(message);

                    if(!cmd.isEmpty()){

                        if(cmd.equalsIgnoreCase(RESP)){
                            int code = Integer.parseInt(Messages.getDataMap(message).get(CODE));
                            if(code == 0) {
                                game = true;
                            }else {
                                game = false;
                                Log.d("INFO", "Lobby is full");
                            }
                        }else if(cmd.equalsIgnoreCase(INFO)){
                            view.passInformation(new Information(message));
                        }else if(cmd.equalsIgnoreCase(START)){

                        }else if(cmd.equalsIgnoreCase(PAUSE)){

                        }else if(cmd.equalsIgnoreCase(END)){

                        }

                    }else {
                        view.passMessage(message);
                    }

                }catch(Exception e){
                    Log.d("ERROR",e.getMessage());

                }

            }

            Log.d("INFO","Connection with Server closed");
            closeConn();

        }catch(Exception io){
            Log.d("ERROR",io.getMessage());
        }
    }

}
