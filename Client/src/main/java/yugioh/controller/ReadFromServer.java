package yugioh.controller;

import yugioh.utils.Connection;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ReadFromServer implements Runnable {
    DataInputStream in = null;
    DataOutputStream out = null;
    Socket socket;

    public ReadFromServer() {
        this.socket = Connection.getSocket();
        run();
    }

    public void run() {
        try {
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());
            while (true) {
                try {
                    String line = in.readUTF();
                    MainController.getInstance().getLoggedIn().getMESSAGES().put(line);
                } catch (IOException | InterruptedException ignored) {
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}