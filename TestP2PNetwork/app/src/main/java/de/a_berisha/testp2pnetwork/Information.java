package de.a_berisha.testp2pnetwork;

/**
 * Created by Adrian Berisha on 05.04.2017.
 */

public class Information extends MessageEncode{
    private String lobbyName;
    private String player1;
    private String player2;

    private final String DELIMITER = ",";
    
    public Information(String lobbyName, String player1, String player2){
        this.lobbyName = lobbyName;
        this.player1 = player1;
        this.player2 = player2;
    }

    /*
        INFO{Name of the Lobby, Name of Player 1, Name of Player 2}
        The Name of the Lobby cannot be empty
     */
    public Information(String info) throws Exception{
    	strToInfo(info);
    }

    public String toString(){
    	return "INFO{"+lobbyName + DELIMITER + (player1.isEmpty()?" ":player1) + DELIMITER + (player2.isEmpty()?" ":player2)+ "}";
    }
    public void strToInfo(String info)throws Exception{
    	String[] data = getData(info, DELIMITER);
        
    	if(data.length != 3)
    		throw new Exception("Message is not correct");
    	
        lobbyName = data[0];
        player1 = data[1];
        player2 = data[2];
    }
    
    public String getLobbyName() {
        return lobbyName;
    }
    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
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
}
