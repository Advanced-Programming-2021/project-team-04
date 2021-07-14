import java.io.*;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(4444);
            while (true) {
                Socket socket = serverSocket.accept();
                newThread(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void newThread(Socket socket) {
        new Thread(() -> {
            try {
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                String command = dataInputStream.readUTF();
                while (!command.equals("terminate")) {
                    if (command.startsWith("s")) dataOutputStream.writeUTF((String) processString(command));
                    else objectOutputStream.writeObject(processString(command));
                    dataOutputStream.flush();
                    command = dataInputStream.readUTF();
                }
                socket.close();
                dataInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static Object processString(String command) {
        String[] split = command.split(" ");
        try {
            Class<?> clazz = Class.forName(split[2]);
            Method method = clazz.getDeclaredMethod(split[3]);
            return method.invoke(split);
        } catch (Exception ignored) {
            return "";
        }
    }
}
