package yugioh;

import yugioh.controller.ScoreboardController;
import yugioh.model.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class ScoreBoardTest {
    @BeforeAll
    public static void createUsers() {
        ArrayList<Account> allAccounts = new ArrayList<>(Account.getAllAccounts());
        for (Account account : allAccounts)
            Account.removeAccount(account);
        Account first = new Account("Adelard Dekker", "Priest", "Jesus Christ");
        first.setScore(37);
        Account second = new Account("Agnes Montague", "Red Hair", "Crush Material");
        second.setScore(69);
        Account third = new Account("Annabelle Cane", "Spider", "Mother of Puppets");
        third.setScore(85);
        Account fourth = new Account("Hope", "Breekon", "Breekon and Hope");
        fourth.setScore(69);
    }

    @Test
    public void printScores() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        ScoreboardController.getInstance().run();
        Assertions.assertEquals("""
                1- Mother of Puppets: 85
                2- Breekon and Hope: 69
                2- Crush Material: 69
                4- Jesus Christ: 37
                \r
                """, outputStream.toString());
    }


}
