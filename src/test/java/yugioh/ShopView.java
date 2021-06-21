package yugioh;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import yugioh.controller.ShopController;
import yugioh.view.IO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class ShopView {
    @Test
    public void showCard() {
        InputStream backup = System.in;
        ByteArrayInputStream input = new ByteArrayInputStream(("""
                card show Artie\r
                menu exit\r
                """).getBytes());
        System.setIn(input);
        IO.getInstance().resetScanner();
        System.setIn(backup);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        yugioh.view.ShopView.getInstance().run();
        Assertions.assertEquals("there is no card with this name\r\n", outputStream.toString());
    }
}
