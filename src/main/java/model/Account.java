package model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;
import java.util.ArrayList;

@Getter
@Setter
public class Account extends Duelist {

    @Getter
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
    private int money = 100000;

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

//    class AccountDeserializer implements JsonDeserializer<Account> {
//
//        @Override
//        public Account deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
//            return new Account(jsonElement.getAsJsonObject().keySet().stream().findAny(o -> (String) o));
//        }
//    } //TODO fix this sexy son of a bitch, remove the current solution to false canDraw() problem, in readAccount in ImportAndExport
}
