# MobileTennis
Tennis auf drei mobilen Geräten

## Messages ##

Syntax: {cmd:"A Command",data1:"value1",data2:"value2"} and so on...

### Commands ###
The Command which will be execute at the other site

- GET_INFO      Get the Lobby Information from the Server
- INFO          Message with Information Data
- CONN          Request to Connect with the player name
- CONN_RESP     Response from Server, if the connection was successful or not
- CLOSE         Notify to close the connection
- START         Start the Game
- END           End of Game
- PAUSE         Pause the Game
