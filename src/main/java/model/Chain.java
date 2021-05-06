package model;

import view.Input;

import java.util.ArrayList;
import java.util.Map;

public class Chain {
    //TODO is this ArrayList ok AI-wise?
    private ArrayList<Map.Entry<Account, Card>> activatedCards;
    public void run(){
        String command = Input.getInputMessage();
        while(command.toLowerCase().matches("y|yes")) {
            String cardToActivate = Input.getInputMessage();

        }
    }
    private void activateAllCards() {

    }
}
