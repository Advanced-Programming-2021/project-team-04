package yugioh.model;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Account extends Duelist {

    public static final int STARTING_MONEY = 100000;
    @Getter @Setter
    private static ArrayList<Account> allAccounts;
    @Getter
    private static ArrayList<String> allUsernames;
    @Getter
    private static ArrayList<String> allNicknames;

    static {
        allAccounts = new ArrayList<>();
        allNicknames = new ArrayList<>();
        allUsernames = new ArrayList<>();
    }

    @Expose
    private String password;
    @Expose
    private int score;
    @Expose
    private int money = STARTING_MONEY;
    @Expose
    private int profilePictureNumber;

    public Account(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        allAccounts.add(this);
        allUsernames.add(username);
        allNicknames.add(nickname);
    }

    public static void addAccount(Account account) {
        allAccounts.add(account);
        allUsernames.add(account.getUsername());
        allNicknames.add(account.getNickname());
    }

    public boolean hasEnoughMoney(int price) {
        return this.money >= price;
    }

    public static void removeAccount(Account account) {
        allAccounts.remove(account);
        allNicknames.remove(account.getNickname());
        allUsernames.remove(account.getUsername());
    }

    public static Account getAccountByUsername(String username) {
        return allAccounts.stream().filter(a -> a.getUsername().equals(username)).findAny().orElse(null);
    }

    public int getProfilePictureNumber() {
        return profilePictureNumber;
    }

    public void setProfilePictureNumber(int profilePicture) {
        this.profilePictureNumber = profilePicture;
    }
}