package controller;

import model.Account;

import java.util.concurrent.LinkedBlockingQueue;

public class ChatRoom {
    private static final LinkedBlockingQueue<String> messages = new LinkedBlockingQueue<>();

    public static void sendMyMessage(String[] command) {
        String username = command[5];
        String message = command[6];
        try {
            messages.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (message.equals("leave")) {
            String goodbyeMessage = "Server: Farewell " + username + ", may our souls meet again!";
            try {
                messages.put(goodbyeMessage);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getMessage() {
        final String[] string = {""};
        Thread writeMessages = new Thread(() -> {
            while (true) {
                try {
                    String message = messages.take();
                    for (Account client : MainController.getLoggedInAccounts().values()) {
                        string[0] = ") " + message;
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        writeMessages.start();
        return string[0];
    }
}
