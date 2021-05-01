package view;

public class Output {
    private static Output singleInstance = null;
    private Output() {

    }
    public static Output getInstance() {
        if (singleInstance == null)
            singleInstance = new Output();
        return singleInstance;
    }
    private static String forNow = "";

    public static String getForNow() {
        return forNow;
    }

    public static void setForNow(String forNow) {
        Output.forNow = forNow;
    }

    public void printLoginMenuName() {
        System.out.println("Login Menu");
    }

    public void printMainMenuName() {
        System.out.println("Main Menu");
    }

    public void printDuelMenuName() {
        System.out.println("Duel Menu");
    }

    public void printDeckMenuName() {
        System.out.println("Deck Menu");
    }

    public void printScoreboardMenuName() {
        System.out.println("Scoreboard Menu");
    }

    public void printProfileMenuName() {
        System.out.println("Profile Menu");
    }

    public void printShopMenuName() {
        System.out.println("Shop Menu");
    }
}
