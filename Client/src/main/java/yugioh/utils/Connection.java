package yugioh.utils;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.Socket;

public class Connection {
    @Getter
    private static Socket socket;
    @Getter
    private static DataInputStream dataInputStream;
    @Getter
    private static DataOutputStream dataOutputStream;
    @Getter
    private static ObjectInputStream objectInputStream;
    @Getter
    private static BufferedReader input;
    @Setter
    private static String token = "-";

    public static void initiateConnection() {
        try {
            socket = new Socket("localhost", 4444);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            input = new BufferedReader(new InputStreamReader(System.in));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getResult(String input) {
        try {
            dataOutputStream.writeUTF("string@" + token + "@" + input);
            dataOutputStream.flush();
            return dataInputStream.readUTF();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Object getObject(String input) {
        try {
            dataOutputStream.writeUTF("object@" + token + "@" + input);
           dataOutputStream.flush();
            return objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
