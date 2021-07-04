package yugioh.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty
    private String password;
    @JsonProperty
    private int score;
    @JsonProperty
    private int money = STARTING_MONEY;
    @JsonProperty
    private int profilePictureNumber;
    @JsonProperty
    private String profilePictureExtension;

    public Account(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        allAccounts.add(this);
        allUsernames.add(username);
        allNicknames.add(nickname);
    }

    public Account() { }

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

    public String getProfilePictureExtension() {
        return profilePictureExtension;
    }

    public void setProfilePictureExtension(String profilePictureExtension) {
        this.profilePictureExtension = profilePictureExtension;
    }

    public void setProfilePictureNumber(int profilePicture) {
        this.profilePictureNumber = profilePicture;
    }
}