package com.crazyking.mobiletennis.connection;

import java.util.HashMap;

/**
 * Created by Adrian Berisha on 05.04.2017.
 */

public class Information extends Messages{
    private String lobbyName;
    private String player1;
    private String player2;
    private String address;

    private final String lName = "lobbyname";
    private final String p1 = "player1";
    private final String p2 = "player2";
    private final String mac = "mac";


    public Information(String lobbyName, String player1, String player2, String address){
        this.lobbyName = lobbyName;
        this.player1 = player1;
        this.player2 = player2;
        this.address = address;
    }

    public Information(){
        this.lobbyName = "";
        this.player1 = "";
        this.player2 = "";
        this.address = "";
    }

    /**
     *  {cmd:"INFO",lobbyname: "Name of the Lobby",player1:"Name of Player 1",player2:"Name of Player 2",mac:"mac-address"}
     */
    public Information(String info) throws Exception{
    	strToInfo(info);
    }

    public String toString(){
        HashMap<String, String> data = new HashMap<>();
        data.put(lName, lobbyName);
        data.put(p1, player1);
        data.put(p2, player2);
        data.put(mac, address);
    	return getDataStr(Constants.CMD.INFO, data);
    }
    public void strToInfo(String info)throws Exception{
        HashMap<String, String> data = getDataMap(info);

        lobbyName = data.get(lName);
        player1 = data.get(p1);
        player2 = data.get(p2);
        address = data.get(mac);
    }
    
    public String getLobbyName() {
        return lobbyName;
    }
    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }


    public boolean setPlayerName(String playerName){
        if(player1.isEmpty()) {
            player1 = playerName;
            return true;
        }else if(player2.isEmpty()) {
            player2 = playerName;
            return true;
        }
        return false;
    }

    public boolean equals(Information obj) {
        if(this.lobbyName.equals(obj.getLobbyName()) && this.player1.equals(obj.getPlayer1())
                && this.player2.equals(obj.getPlayer2()) && this.address.equals(obj.getAddress()))
            return true;
        return false;

    }

    public String getPlayer1() {
        return player1;
    }
    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }
    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
}
