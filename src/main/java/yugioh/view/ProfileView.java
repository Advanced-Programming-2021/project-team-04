package yugioh.view;

import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.util.Duration;
import yugioh.controller.MainController;
import yugioh.controller.ProfileController;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ProfileView {

    public static void run() {
        setProfilePictureFirstTime();
        ((Label) LoginView.profileScene.lookup("#username"))
                .setText(MainController.getInstance().getLoggedIn().getUsername());
        ((Label) LoginView.profileScene.lookup("#nickname"))
                .setText(MainController.getInstance().getLoggedIn().getNickname());
    }

    private static void setProfilePictureFirstTime() {
        ImageView profilePicture = (ImageView) LoginView.profileScene.lookup("#profilePicture");
        Image image = new Image(ProfileView.class.getResourceAsStream("profiles/" +
                MainController.getInstance().getLoggedIn().getProfilePictureNumber() +
                MainController.getInstance().getLoggedIn().getProfilePictureExtension()));
        profilePicture.setImage(image);
        Tooltip tooltip = new Tooltip("drag and drop a photo\nto change your avatar");
        tooltip.setShowDelay(Duration.seconds(0));
        Tooltip.install(profilePicture, tooltip);
        handleDragAndDrop(profilePicture);
    }

    private static void setProfilePicture() {
        ImageView profilePicture = (ImageView) LoginView.profileScene.lookup("#profilePicture");
        Image image = new Image(ProfileView.class.getResourceAsStream("profiles/" +
                MainController.getInstance().getLoggedIn().getProfilePictureNumber() +
                MainController.getInstance().getLoggedIn().getProfilePictureExtension()));
        profilePicture.setImage(image);
    }

    private static void handleDragAndDrop(ImageView dragTarget) {
        dragTarget.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != dragTarget && event.getDragboard().hasFiles())
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                event.consume();
            }
        });

        dragTarget.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    File picture = db.getFiles().get(0);
                    try {
                        if (ProfileController.getInstance().isPhotoValid(picture.toString())) {
                            ProfileController.getInstance().setProfilePhoto(picture);
                            setProfilePicture();
                        } else IO.getInstance().invalidPicture();
                    } catch (Exception e) {
                        IO.getInstance().invalidPicture();
                    }
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });
    }


    public void changeNickname() {
        TextField nickname = (TextField) LoginView.profileScene.lookup("#newNickname");
        ProfileController.getInstance().changeNickname(nickname.getText());
        nickname.clear();
        ((Label) LoginView.profileScene.lookup("#nickname"))
                .setText(MainController.getInstance().getLoggedIn().getNickname());
    }

    public void changePassword() {
        TextField currentPassword = (TextField) LoginView.profileScene.lookup("#currentPassword");
        TextField newPassword = (TextField) LoginView.profileScene.lookup("#newPassword");
        ProfileController.getInstance().changePassword(currentPassword.getText(), newPassword.getText());
        currentPassword.clear();
        newPassword.clear();
    }

    public void mainMenu() {
        LoginView.stage.setScene(LoginView.mainScene);
        LoginView.stage.centerOnScreen();
    }

}
