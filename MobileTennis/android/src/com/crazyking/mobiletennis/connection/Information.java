package com.crazyking.mobiletennis.connection;

import java.util.HashMap;
import static com.crazyking.mobiletennis.connection.Constants.*;

/**
 * Created by Adrian Berisha on 05.04.2017.
 */

public class Information extends Messages{
    private String lobbyName;
    private String player1;
    private String player2;
    private String address;


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

    /**
     * Invert information to a data string
     * @return  the right data string
     */
    public String toString(){
        HashMap<String, String> data = new HashMap<>();
        data.put(INFO_LOBBY, lobbyName);
        data.put(INFO_P1, player1);
        data.put(INFO_P2, player2);
        data.put(INFO_ADR, address);
    	return getDataStr(Constants.CMD.INFO, data);
    }

    /**
     * Convert a information string to a information object
     * @param info          A information string, which should be convert to a Information
     *                      Object
     */
    public void strToInfo(String info){
        HashMap<String, String> data = getDataMap(info);

        lobbyName = data.get(INFO_LOBBY);
        player1 = data.get(INFO_P1);
        player2 = data.get(INFO_P2);
        address = data.get(INFO_ADR);
    }

    /**
     * Get Lobby name
     * @return  Lobby Name
     */
    public String getLobbyName() {
        return lobbyName;
    }

    /**
     *
     * @param lobbyName Set a new lobby name
     */
    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }


    /**
     * Set a player name to the position from integer (1-2)
     * @param player        A integer value which specify the position for the player
     * @param playerName    A string for the player name
     * @return              If successful or not
     */
    public boolean setPlayerName(int player, String playerName){
        if(player == 1)
            this.player1 = playerName;
        else if (player == 2)
            this.player2 = playerName;
        else
            return false;
        return true;
    }

    /**
     * Compare two information objects
     * @param obj   the other information object which should be compared
     * @return      if is equal or not
     */
    public boolean equals(Information obj) {
        if(this.lobbyName.equals(obj.getLobbyName()) && this.player1.equals(obj.getPlayer1())
                && this.player2.equals(obj.getPlayer2()) && this.address.equals(obj.getAddress()))
            return true;
        return false;

    }

    /**
     * Get Player 1
     * @return  String of player name 1
     */
    public String getPlayer1() {
        return player1;
    }

    /**
     * Set Player 1
     * @param player1   String of player name 1
     */
    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    /**
     * Get Player 2
     * @return  String of player name 2
     */
    public String getPlayer2() {
        return player2;
    }

    /**
     * Set Player 2
     * @param player2   String of player name 2
     */
    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    /**
     * Get Mac-Address
     * @return  String of mac-address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set Mac-Address
     * @param address   String of mac-address
     */
    public void setAddress(String address) {
        this.address = address;
    }
}
