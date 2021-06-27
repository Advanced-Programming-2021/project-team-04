package yugioh;

import yugioh.controller.MainController;
import yugioh.controller.ShopController;
import yugioh.model.Account;
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
        Assertions.assertEquals("""
                Advanced Ritual Art:3000
                Alexandrite Dragon:2600
                Axe Raider:3100
                Baby dragon:1600
                Battle OX:2900
                Battle warrior:1300
                Beast King Barbaros:9200
                Bitron:1000
                Black Pendant:4300
                BlueEyes white dragon:11300
                Call of The Haunted:3500
                Change of Heart:2500
                Closed Forest:4300
                Command Knight:2100
                Crab Turtle:10200
                Crawling dragon:3900
                Curtain of the dark ones:700
                Dark Blade:3500
                Dark Hole:2500
                Dark magician:8300
                Exploder Dragon:1000
                Feral Imp:2800
                Fireyarou:2500
                Flame manipulator:1500
                Forest:4300
                Gate Guardian:20000
                Haniwa:600
                Harpie's Feather Duster:2500
                Herald of Creation:2700
                Hero of the east:1700
                Horn Imp:2500
                Leotron:2500
                Magic Cylinder:2000
                Magic Jamamer:3000
                Magnum Shield:4300
                ManEater Bug:600
                Marshmallon:700
                Messenger of peace:4000
                Mind Crush:2000
                Mirage Dragon:2500
                Mirror Force:2000
                Monster Reborn:2500
                Mystical space typhoon:3500
                Negate Attack:3000
                Pot of Greed:2500
                Raigeki:2500
                Ring of defense:3500
                Scanner:8000
                Silver Fang:1700
                Skull Guardian:7900
                Slot Machine:7500
                Solemn Warning:3000
                Spell Absorption:4000
                Spiral Serpent:11700
                Suijin:8700
                Supply Squad:4000
                Sword of dark destruction:4300
                Swords of Revealing Light:2500
                Terraforming:2500
                Terratiger, the Empowered Warrior:3200
                Texchanger:200
                The Calculator:8000
                The Tricky:4300
                Time Seal:2000
                Torrential Tribute:2000
                Trap Hole:2000
                Twin Twisters:3500
                Umiiruka:4300
                United We Stand:4300
                Vanity's Emptiness:3500
                Wall of Revealing Light:3500
                Warrior Dai Grepher:3400
                Wattaildragon:5800
                Wattkid:1300
                Yami:4300
                Yomi Ship:1700\r
                """, outputStream.toString());
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
        ShopController.getInstance().showCard("Crime Scene");
        Assertions.assertEquals("not a valid card name\r\n", outputStream.toString());
    }

    @Test
    public void buyCardTest() {
        Account account = new Account("Timothy Stoker", "stalker", "smoothie");
        MainController.getInstance().setLoggedIn(account);
        ShopController.getInstance().buyCard("Magic Cylinder");
        Assertions.assertTrue(account.hasCard("Magic Cylinder"));
    }
}
