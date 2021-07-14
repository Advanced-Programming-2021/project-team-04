package controller;

import model.Account;

public class ProfileController {

    public String changeNickname(String[] command) {
        String name = command[4];
        Account loggedIn = MainController.getLoggedInAccounts().get(command[1]);
        String result = isChangingNicknameValid(name);
        if (result.equals("success")) {
            Account.getAllNicknames().remove(loggedIn.getNickname());
            loggedIn.setNickname(name);
            Account.getAllNicknames().add(name);
        }
        return result;
    }


    public String changePassword(String[] command) {
        String oldPassword = command[4];
        String newPassword = command[5];
        Account loggedIn = MainController.getLoggedInAccounts().get(command[1]);
        String result = isChangingPasswordValid(oldPassword, newPassword, loggedIn);
        if (result.equals("success"))
            loggedIn.setPassword(newPassword);
        return result;
    }


    private String isChangingPasswordValid(String oldPassword, String newPassword, Account player) {
        if (!player.getPassword().equals(oldPassword))
            return "current password is incorrect";
        if (oldPassword.equals(newPassword))
            return "please enter a new password";
        return "success";
    }


    private String isChangingNicknameValid(String nickname) {
        if (Account.getAllNicknames().contains(nickname))
            return "user with nickname " + nickname + " already exists";
        return "success";
    }
}
