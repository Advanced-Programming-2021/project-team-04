package view;

import java.util.Scanner;

public class Input {
    private static final Scanner scanner = new Scanner(System.in);
    //TODO add .toLowerCase() and change related regex
    public static String getInputMessage(){
        return scanner.nextLine().trim();
    }
}
