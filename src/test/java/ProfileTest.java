import controller.LoginController;
import controller.MainController;
import controller.ProfileController;
import model.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ProfileTest {
    public static Account thisAccount = new Account("Gerard Keay", "bean$le", "Erard Ke");
    public static Account someoneElse = new Account("Jan Kilbride", "Astronaut", "vast");
    @BeforeAll
    public static void setLoggedIn() {
        MainController.getInstance().setLoggedIn(thisAccount);
    }
    @Test
    public void changeNicknameTest() {
        ProfileController.getInstance().changeNickname("yag");
        Assertions.assertEquals(thisAccount.getNickname(), "yag");
        ProfileController.getInstance().changeNickname("Erard Ke");
    }
    @Test
    public void changePasswordTest() {
        ProfileController.getInstance().changePassword("bean$le", "beanLee");
        Assertions.assertEquals(thisAccount.getPassword(), "beanLee");
        ProfileController.getInstance().changePassword( "beanLee","bean$le");
    }
    @Test
    public void invalidCurrentPasswordTest() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        ProfileController.getInstance().changePassword("beanle", "beanLee");
        Assertions.assertEquals("current password is invalid\r\n", outputStream.toString());
    }
    @Test
    public void oldEqualsNew() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        ProfileController.getInstance().changePassword("bean$le", "bean$le");
        Assertions.assertEquals("please enter a new password\r\n", outputStream.toString());
    }
    @Test
    public void errorNickNameExists() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        ProfileController.getInstance().changeNickname("vast");
        Assertions.assertEquals("user with nickname vast already exists\r\n", outputStream.toString());
    }
}
