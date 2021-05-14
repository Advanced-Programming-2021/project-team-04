package view;

import java.util.Scanner;

public class Input {
    private static Scanner scanner = new Scanner(System.in);
    //TODO add .toLowerCase() and change related regex
    public static String getInputMessage(){
        return scanner.nextLine().trim();
    }

    public static void resetScanner() {
        //used only for unit tests
        scanner = new Scanner(System.in);
    }
}
