package de.a_berisha.testp2pnetwork;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Adrian Berisha on 29.03.2017.
 */

public class SendData extends Thread {

    Socket socket;
    String data;

    public SendData(Socket socket, String data){
        this.socket = socket;
        this.data = data;
    }

    @Override
    public void run() {
        super.run();

        byte[] bytes = data.getBytes();

        try {
            OutputStream os = socket.getOutputStream();
            os.write(bytes);
            os.flush();

        }catch (IOException io){
            io.printStackTrace();
        }
    }
}
