package yugioh;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import yugioh.controller.MainController;
import yugioh.model.Account;
import yugioh.view.IO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class ProfileView {
    private static Account Auguste;
    @BeforeAll
    public static void createAccount() {
        Auguste = new Account("Auguste", "Captive", "Laurent");
        MainController.getInstance().setLoggedIn(Auguste);
    }

    @Test
    public void changeNickname() {
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream(("""
                profile change --nickname BabyLaurent\r
                menu exit\r
                """).getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        yugioh.view.ProfileView.getInstance().run();
        Assertions.assertEquals(Auguste.getNickname(), "BabyLaurent");
    }

    @Test
    public void changePassword() {
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream(("""
                profile change --password --current Captive --new Prince\r
                menu exit\r
                """).getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        yugioh.view.ProfileView.getInstance().run();
        Assertions.assertEquals("Prince",Auguste.getPassword());
    }
}
