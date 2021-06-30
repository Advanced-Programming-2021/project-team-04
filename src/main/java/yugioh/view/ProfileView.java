package yugioh.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import yugioh.controller.MainController;
import yugioh.controller.ProfileController;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ProfileView{

    public static void run() {
        MainController.getInstance().setRandomProfile();
        setProfilePicture();
        ((Label) LoginView.profileScene.lookup("#username"))
                .setText(MainController.getInstance().getLoggedIn().getUsername());
        ((Label) LoginView.profileScene.lookup("#nickname"))
                .setText(MainController.getInstance().getLoggedIn().getNickname());
    }


    public static void setProfilePicture() {
        ImageView profilePicture = (ImageView) LoginView.profileScene.lookup("#profilePicture");
        Image image = new Image(ProfileView.class.getResourceAsStream("profiles/" +
                MainController.getInstance().getLoggedIn().getProfilePictureNumber() + ".jpg"));
        profilePicture.setImage(image);
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
