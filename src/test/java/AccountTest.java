import controller.LoginController;
import controller.MainController;
import model.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class AccountTest {

    @Test
    public void loginSuccessfullyTest() {
        Account account = new Account("Rosemary Standley", "NothingButThieves", "Dom La Nena");
        LoginController.getInstance().loginUser("Rosemary Standley", "NothingButThieves");
        Assertions.assertTrue(MainController.getInstance().getLoggedIn().equals(account));
    }

    @Test
    public void createUserTest() {
        LoginController.getInstance().createUser("Jonathan Sims", "Martin", "Jonny");
        Assertions.assertNotNull(Account.getAccountByUsername("Jonathan Sims"));
    }

    @Test
    public void errorsForLogin() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        new Account("Rosemary Standley", "NothingButThieves", "Dom La Nena");
        LoginController.getInstance().loginUser("Rosemary Standley", "bullshit");
        Assertions.assertEquals("Username and password didn’t match!\r\n", outputStream.toString());
    }

    @Test
    public void errorNickNameExists() {
        new Account("Gertrude Robinson", "Hozier", "Son Of A Robin");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        LoginController.getInstance().createUser("Elias Bouchard", "Girl In Red", "Son Of A Robin");
        Assertions.assertEquals("user with nickname Son Of A Robin already exists\r\n", outputStream.toString());
    }

    @Test
    public void errorUserNameExists() {
        new Account("Martin Blackwood", "Jonny", "dark oak tree");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        LoginController.getInstance().createUser("Martin Blackwood", "Jonny My Love", "dark oak trees");
        Assertions.assertEquals("user with username Martin Blackwood already exists\r\n", outputStream.toString());
    }
}
