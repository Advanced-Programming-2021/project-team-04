package yugioh.controller;

import javafx.application.Platform;

import yugioh.utils.Connection;
import yugioh.view.ChatView;
import yugioh.view.LoginView;


public class ChatRoom {

    public static void sendMyMessage() {
        Thread userInput = new Thread(() -> {
            String message = "";
            while (!message.equals("leave")) {
                try {
                    message = MainController.getInstance().getLoggedIn().getMESSAGES_BY_CLIENT().take();
                    Connection.getResult("ChatRoom@sendMyMessage@" + MainController.getInstance().getLoggedIn().getUsername() + "@" + message);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LoginView.stage.setScene(LoginView.mainScene);
        });
        userInput.start();
    }

    public static void getMessage() {
        Thread readMessagesToClient = new Thread(() -> {
            String message = "";
            while(true){
                try{
                    message = MainController.getInstance().getLoggedIn().getMESSAGES().take();
                    addMessageToChat(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        readMessagesToClient.start();
    }

    private static void addMessageToChat(String message) {
        Platform.runLater(() -> ChatView.chatInput.appendText(message));
    }

}
