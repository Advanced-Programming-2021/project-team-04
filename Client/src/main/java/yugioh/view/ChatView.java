package yugioh.view;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import yugioh.controller.ChatRoom;
import yugioh.controller.MainController;
import yugioh.controller.ReadFromServer;
import yugioh.utils.Connection;

import java.io.IOException;

public class ChatView {

    private static Scene chatRoom;
    public static final TextFlow chat = new TextFlow();
    private static final TextArea chatInput = new TextArea();
    private static final Button sendButton = new Button("Send");

    public static void run() {
        GridPane grid = createScene();
        LoginView.stage.setScene(chatRoom);
        try {
            String serverMessage = Connection.getDataInputStream().readUTF();
            Text text = new Text(serverMessage);
            chat.getChildren().add(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ScrollPane scrollPane = chatUI();
        Circle statusCircle = statusC();
        Label statusLabel = statusL();
        Label usernameLabel = setNickname();
        input();
        alignment(grid, scrollPane, statusCircle, statusLabel, usernameLabel);
        setUpButtonSend();
        ChatRoom.getMessage();
        ChatRoom.sendMyMessage();
        ReadFromServer server = new ReadFromServer();
        new Thread(server).start();
    }

    private static void alignment(GridPane grid, ScrollPane scrollPane, Circle statusCircle, Label statusLabel, Label usernameLabel) {
        GridPane.setHgrow(sendButton, Priority.ALWAYS);
        GridPane.setVgrow(sendButton, Priority.ALWAYS);
        GridPane.setHalignment(sendButton, HPos.RIGHT);
        GridPane.setValignment(sendButton, VPos.TOP);
        GridPane.setConstraints(sendButton, 0, 2);
        grid.getChildren().addAll(scrollPane, chatInput, sendButton, statusCircle, statusLabel, usernameLabel);
    }

    public static void setUpButtonSend(){
        sendButton.setOnAction(e -> {
            String message = chatInput.getText();
            MainController.getInstance().getLoggedIn().getMESSAGES_BY_CLIENT().add(message);
            chatInput.setText("");
        });
    }

    private static void input() {
        chatInput.setPromptText("Enter message...");
        GridPane.setHgrow(chatInput, Priority.ALWAYS);
        GridPane.setVgrow(chatInput, Priority.ALWAYS);
        GridPane.setConstraints(chatInput, 0, 1);
    }

    private static GridPane createScene() {
        GridPane grid = new GridPane();
        grid.setVgap(8);
        grid.setHgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));
        ColumnConstraints column0 = new ColumnConstraints();
        column0.setPercentWidth(80);
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(20);
        RowConstraints row0 = new RowConstraints();
        row0.setPercentHeight(75);
        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(20);
        RowConstraints row2 = new RowConstraints();
        row1.setPercentHeight(20);
        grid.getRowConstraints().addAll(row0, row1, row2);
        grid.getColumnConstraints().addAll(column0, column1);
        chatRoom = new Scene(grid, 600, 520);
        chatRoom.getStylesheets().add("stylesheets/Chat.css");
        return grid;
    }

    private static Label setNickname() {
        Label usernameLabel = new Label("User: " + MainController.getInstance().getLoggedIn().getNickname());
        GridPane.setValignment(usernameLabel, VPos.TOP);
        GridPane.setHalignment(usernameLabel, HPos.LEFT);
        GridPane.setHgrow(usernameLabel, Priority.ALWAYS);
        GridPane.setVgrow(usernameLabel, Priority.ALWAYS);
        usernameLabel.setPadding(new Insets(30, 0, 0, 0));
        GridPane.setConstraints(usernameLabel,1, 0 );
        return usernameLabel;
    }

    private static Label statusL() {
        Label statusLabel = new Label("Online");
        GridPane.setValignment(statusLabel, VPos.TOP);
        GridPane.setHalignment(statusLabel, HPos.CENTER);
        GridPane.setHgrow(statusLabel, Priority.ALWAYS);
        GridPane.setVgrow(statusLabel, Priority.ALWAYS);
        GridPane.setConstraints(statusLabel,1, 0 );
        return statusLabel;
    }

    private static Circle statusC() {
        Circle statusCircle = new Circle(0, 0, 10);
        GridPane.setValignment(statusCircle, VPos.TOP);
        GridPane.setHgrow(statusCircle, Priority.ALWAYS);
        GridPane.setVgrow(statusCircle, Priority.ALWAYS);
        GridPane.setConstraints(statusCircle, 1, 0);
        return statusCircle;
    }

    private static ScrollPane chatUI() {
        chat.setPadding(new Insets(5, 5, 5, 5));
        GridPane.setHgrow(chat, Priority.ALWAYS);
        GridPane.setVgrow(chat, Priority.ALWAYS);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(chat);
        GridPane.setConstraints(scrollPane, 0, 0);
        return scrollPane;
    }


}
