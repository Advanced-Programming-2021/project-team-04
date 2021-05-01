package view;

import java.util.Scanner;

public class Input {
    private static Scanner scanner = new Scanner(System.in);
    public static String getInputMessage(){
        return scanner.nextLine().trim();
    }
}
