import controller.MainController;
import controller.ShopController;
import model.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ShopTest {
    @BeforeAll
    public static void createCards() {
        ShopController.getInstance();
    }

    @Test
    public void printAllCardsTest() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        ShopController.getInstance().showAllCards();
        Assertions.assertEquals("Advanced Ritual Art:3000\n" +
                "Alexandrite Dragon:2600\n" +
                "Axe Raider:3100\n" +
                "Baby dragon:1600\n" +
                "Battle OX:2900\n" +
                "Battle warrior:1300\n" +
                "Beast King Barbaros:9200\n" +
                "Bitron:1000\n" +
                "Black Pendant:4300\n" +
                "Blue-Eyes white dragon:11300\n" +
                "Call of The Haunted:3500\n" +
                "Change of Heart:2500\n" +
                "Closed Forest:4300\n" +
                "Command Knight:2100\n" +
                "Crab Turtle:10200\n" +
                "Crab Turtle:10200\n" +
                "Curtain of the dark ones:700\n" +
                "Dark Blade:3500\n" +
                "Dark Hole:2500\n" +
                "Dark magician:8300\n" +
                "Exploder Dragon:1000\n" +
                "Feral Imp:2800\n" +
                "Fireyarou:2500\n" +
                "Flame manipulator:1500\n" +
                "Forest:4300\n" +
                "Gate Guardian:20000\n" +
                "Haniwa:600\n" +
                "Harpie's Feather Duster:2500\n" +
                "Herald of Creation:2700\n" +
                "Hero of the east:1700\n" +
                "Horn Imp:2500\n" +
                "Leotron:2500\n" +
                "Magic Cylinder:2000\n" +
                "Magic Jamamer:3000\n" +
                "Magnum Shield:4300\n" +
                "Man-Eater Bug:600\n" +
                "Marshmallon:700\n" +
                "Messenger of peace:4000\n" +
                "Mind Crush:2000\n" +
                "Mirage Dragon:2500\n" +
                "Mirror Force:2000\n" +
                "Monster Reborn:2500\n" +
                "Mystical space typhoon:3500\n" +
                "Negate Attack:3000\n" +
                "Pot of Greed:2500\n" +
                "Raigeki:2500\n" +
                "Ring of defense:3500\n" +
                "Scanner:8000\n" +
                "Silver Fang:1700\n" +
                "Skull Guardian:7900\n" +
                "Slot Machine:7500\n" +
                "Solemn Warning:3000\n" +
                "Spell Absorption:4000\n" +
                "Spiral Serpent:11700\n" +
                "Suijin:8700\n" +
                "Supply Squad:4000\n" +
                "Sword of dark destruction:4300\n" +
                "Swords of Revealing Light:2500\n" +
                "Terraforming:2500\n" +
                "Terratiger, the Empowered Warrior:3200\n" +
                "Texchanger:200\n" +
                "The Calculator:8000\n" +
                "The Tricky:4300\n" +
                "Time Seal:2000\n" +
                "Torrential Tribute:2000\n" +
                "Trap Hole:2000\n" +
                "Twin Twisters:3500\n" +
                "Umiiruka:4300\n" +
                "United We Stand:4300\n" +
                "Vanity's Emptiness:3500\n" +
                "Wall of Revealing Light:3500\n" +
                "Warrior Dai Grepher:3400\n" +
                "Wattaildragon:5800\n" +
                "Wattkid:1300\n" +
                "Yami:4300\n" +
                "Yomi Ship:170\r\n", outputStream.toString());
    }

    @Test
    public void testMoney() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        Account account = new Account("Timothy Stoker", "stalker", "smoothie");
        account.setMoney(0);
        MainController.getInstance().setLoggedIn(account);
        ShopController.getInstance().buyCard("Raigeki");
        Assertions.assertEquals("not enough money\r\n", outputStream.toString());
    }

    @Test
    public void invalidCardNameTest() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        ShopController.getInstance().isCardNameValid("Crime Scene");
        Assertions.assertEquals("there is no card with this name\r\n", outputStream.toString());
    }
    @Test
    public void buyCardTest() {
        Account account = new Account("Timothy Stoker", "stalker", "smoothie");
        MainController.getInstance().setLoggedIn(account);
        ShopController.getInstance().buyCard("Magic Cylinder");
        Assertions.assertTrue(account.hasCard("Magic Cylinder"));
    }
}
