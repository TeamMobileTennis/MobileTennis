package de.a_berisha.testp2pnetwork;

import java.io.IOException;

/**
 * Created by Adrian Berisha on 07.04.2017.
 */

public interface ConnectionHandler {
    void sendMessage(String message);
    void closeConn() throws IOException;
}
