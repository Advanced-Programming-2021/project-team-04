package controller;

import lombok.Getter;
import model.Account;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class LoginController {

    private static final Random RANDOM = new Random();

    public static String loginUser(String[] command) {
        String username = command[4];
        String password = command[5];
        String result = isLoggingInValid(username, password);
        if (result.equals("success")) {
            String token = UUID.randomUUID().toString();
            MainController.getLoggedInAccounts().put(token, Account.getAccountByUsername(username));
            result = "success " + token;
        }
        return result;
    }


    public static String createUser(String[] command) {
        String username = command[4];
        String password = command[5];
        String nickname = command[6];
        String result = errorsForCreatingUser(username, nickname);
        if (result.equals("success")) {
            setRandomProfile(new Account(username, password, nickname));
        }
        return result;
    }

    private static void setRandomProfile(Account account) {
        if (account.getProfilePictureExtension() == null)
            account.setProfilePictureExtension(".jpg");
        if (account.getProfilePictureNumber() != 0) return;
        account.setProfilePictureNumber(RANDOM.nextInt(11) + 1);
    }


    private static String errorsForCreatingUser(String username, String nickname) {
        if (model.Account.getAllUsernames().contains(username) || username.equals(model.AI.AI_USERNAME)) {
            return "user with username " + username + " already exists";
        } else if (model.Account.getAllNicknames().contains(nickname)) {
            return "user with nickname " + nickname + " already exists";
        }
        return "success";
    }


    public static String isLoggingInValid(String username, String password) {
        if (Account.getAccountByUsername(username) == null || !Account.getAccountByUsername(username).getPassword().equals(password)) {
            return "username and password do not match";
        }
        return "success";
    }
}
