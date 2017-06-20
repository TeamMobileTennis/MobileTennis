package de.teammt.mobiletennis.connection.Client;

import android.net.wifi.p2p.WifiP2pInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import de.teammt.mobiletennis.connection.Constants;

import de.teammt.mobiletennis.connection.Information;
import de.teammt.mobiletennis.connection.Messages;
import de.teammt.mobiletennis.connection.ViewPeerInterface;

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


    /**
     * Constructor for Client
     * @param wifiInfo      WifiP2PInfo contains information for connection
     * @param port          Port which to bind to
     * @param view          View to pass messages and infos
     * @param playerName    Player name of device
     */
    public Client(WifiP2pInfo wifiInfo, int port, ViewPeerInterface view, String playerName){
        this.view = view;
        this.playerName = playerName;
        this.host = wifiInfo.groupOwnerAddress;
        this.port = port;
        Log.d("INFO", "Created Client");
    }

    /**
     *
     * @param message   A Message that send to server
     */
    public void sendMessage(final String message) {
        Log.d("INFO","Send "+ message + " to Server");
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

    /**
     * Close the connection with server
     */
    public void closeConn(){
        if(!close) {
            requestClose();
            Log.d("INFO","Request to close");
        }
    }


    /**
     * Stop the connection to server
     * @throws IOException  If an error occurs to close reader and writer
     */
    private void stopConn()throws IOException{
        close = true;
        reader.close();
        writer.close();
        server.close();
        if(this.isAlive())
            this.interrupt();
    }

    /**
     * Request to close.
     * Like TCP. Request to Close -> Other side accepts close -> Client receives and close Connection
     */
    public void requestClose(){
        sendMessage(Messages.getDataStr(Constants.CMD.CLOSE, Constants.CODE, Integer.toString(Constants.CLOSE_REQ)));
    }

    /**
     * Accepts the incoming request to close the connection
     * @throws IOException  if failed to stop the connection
     */
    private void acceptClose()throws IOException{
        sendMessage(Messages.getDataStr(Constants.CMD.CLOSE, Constants.CODE, Integer.toString(Constants.CLOSE_ACCEPT)));
        stopConn();
    }

    /**
     * Handle the incoming close request/accept
     * @param code  contains the code (0 request, 1 accept, 2 refuse)
     * @return      if its failed or not
     * @throws IOException  if it failed to stop connection
     */
    private boolean handleClose(int code)throws IOException{
        if(code == Constants.CLOSE_REQ)
            acceptClose();
        else if(code == Constants.CLOSE_ACCEPT){
            stopConn();
        }else if(code == Constants.CLOSE_REFUSE){
            return false;
        }else {
            return false;
        }
        return true;
    }

    /**
     * Check if a connection is still established
     * @return  the state if connection is established
     */
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

    /**
     * Update the view
     * @param view  The current view to pass messages and infos
     */
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
            Log.d("INFO","Connect to Host");

            reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
            writer = new PrintWriter(server.getOutputStream(), true);

            String message;
            while (!close) {

                message = reader.readLine();
                Log.d("INFO", message);

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

    /**
     * Handle the incoming commands from server
     * @param message       contains the current command
     * @throws Exception    if an error occurs
     */
    private void handleCommands(String message)throws Exception{
        String cmd = Messages.getCommand(message);

        if (!cmd.isEmpty()) {

            switch (cmd){
                case Constants.CMD.INFO:
                    information.strToInfo(message);
                    //view.passInformation(information);
                    break;

                case Constants.CMD.CLOSE:
                    if(Messages.getDataMap(message).size()<=1){
                        acceptClose();
                    }else {
                        String strCode = Messages.getValue(message, Constants.CODE);
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
                                view.passMessage(Messages.getDataStr(Constants.CMD.CLOSE));
                            }

                        }
                    }
                    break;
                case Constants.CMD.RESP:
                    if(Integer.parseInt(Messages.getValue(message, Constants.CODE)) == 0) {
                        // Connection accepted - send ACK
                        sendMessage(Messages.getDataStr(Constants.CMD.CONN_ACK));
                    }else {
                        // Connection refused
                        // TODO - Connection refused
                    }

                    view.passMessage(message);
                    break;
                default:
                    view.passMessage(message);
            }

        }else {
            view.passMessage(message);
        }
    }

}
