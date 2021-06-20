package yugioh;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import yugioh.controller.MainController;
import yugioh.model.Account;
import yugioh.view.IO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class LoginView {
    @Test
    public void createUser() {
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("user create --username Hernando --nickname Lito --password Q\r\nmenu exit\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        yugioh.view.LoginView.getInstance().run();
        Assertions.assertTrue(Account.getAllAccounts().contains(Account.getAccountByUsername("Hernando")));
        Account.removeAccount(Account.getAccountByUsername("Hernando"));
    }

    @Test
    public void loginUser() {
        new Account("Hernando", "Q", "Lito");
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("user login --username Hernando --password Q\r\nlogout\r\nmenu exit\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        yugioh.view.LoginView.getInstance().run();
        Assertions.assertEquals("""
                user logged in successfully!\r
                user logged out successfully!\r
                """, outputStream.toString());
        Account.removeAccount(Account.getAccountByUsername("Hernando"));
    }

    @Test
    public void menuEnter() {
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream("menu enter main\r\nmenu exit\r\n".getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        yugioh.view.LoginView.getInstance().run();
        Assertions.assertEquals("please login first\r\n", outputStream.toString());
    }
}
