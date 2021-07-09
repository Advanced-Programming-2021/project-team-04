package yugioh.controller;


import yugioh.model.Account;
import yugioh.view.IO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ProfileController {
    private static ProfileController singleInstance = null;


    public static ProfileController getInstance() {
        if (singleInstance == null)
            singleInstance = new ProfileController();
        return singleInstance;
    }


    public void changeNickname(String name) {
        if (isChangingNicknameValid(name)) {
            Account.getAllNicknames().remove(MainController.getInstance().getLoggedIn().getNickname());
            MainController.getInstance().getLoggedIn().setNickname(name);
            Account.getAllNicknames().add(name);
            IO.getInstance().nicknameChanged();
        }
    }


    public void changePassword(String oldPassword, String newPassword) {
        if (isChangingPasswordValid(oldPassword, newPassword)) {
            MainController.getInstance().getLoggedIn().setPassword(newPassword);
            IO.getInstance().passwordChanged();
        }
    }


    private boolean isChangingPasswordValid(String oldPassword, String newPassword) {
        Account player = MainController.getInstance().getLoggedIn();
        if (!player.getPassword().equals(oldPassword)) {
            IO.getInstance().invalidCurrentPassword();
            return false;
        }
        else if (oldPassword.equals(newPassword)) {
            IO.getInstance().enterANewPassword();
            return false;
        }
        return true;
    }


    private boolean isChangingNicknameValid(String nickname) {
        if (Account.getAllNicknames().contains(nickname)) {
            IO.getInstance().userWithNicknameExists(nickname);
            return false;
        }
        return true;
    }

    public boolean isPhotoValid(String address) {
        return address.endsWith(".jpg") || address.endsWith(".png") || address.endsWith(".gif");
    }

    public void setProfilePhoto(File picture) throws IOException {
        var pattern = Pattern.compile("(\\d+)\\.(?:jpg|png|gif)");
        Matcher matcher;
        var max = 0;
        var folder = new File("src/main/resources/yugioh/view/profiles");
        for (File photo : Objects.requireNonNull(folder.listFiles())) {
            matcher = pattern.matcher(photo.getName());
            if (matcher.find() && Integer.parseInt(matcher.group(1)) > max)
                max = Integer.parseInt(matcher.group(1));
        }
        max++;
        var extension = "." + ImportAndExport.getInstance().getFileExtension(picture.getName());
        Files.copy(picture.toPath(), Paths.get("src/main/resources/yugioh/view/profiles/" + max + extension));
        Files.copy(picture.toPath(), Paths.get("target/classes/yugioh/view/profiles/" + max + extension));
        MainController.getInstance().getLoggedIn().setProfilePictureNumber(max);
        MainController.getInstance().getLoggedIn().setProfilePictureExtension(extension);
    }
}