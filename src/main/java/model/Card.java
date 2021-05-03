package model;

import java.util.ArrayList;

public class Card {
    protected static ArrayList<Card> allCards = new ArrayList<>();
    protected String name;
    protected int price;
    protected String description;
    protected int allowedNumber;
    protected boolean isAttacked = false;
    protected boolean isAttacking = false;
    //TODO probably should handle more than this, maybe make an entry set for both cards *or a hashmap idk
    protected Account Owner;
    protected boolean hasBeenUsedInThisTurn = false;
    protected boolean hasBeenSetOrSummoned = false;

    public Card() {
        allCards.add(this);
    }

    public boolean isHasBeenSetOrSummoned() {
        return hasBeenSetOrSummoned;
    }

    public void setHasBeenSetOrSummoned(boolean hasBeenSetOrSummoned) {
        this.hasBeenSetOrSummoned = hasBeenSetOrSummoned;
    }

    public boolean isHasBeenUsedInThisTurn() {
        return hasBeenUsedInThisTurn;
    }

    public void setHasBeenUsedInThisTurn(boolean hasBeenUsedInThisTurn) {
        this.hasBeenUsedInThisTurn = hasBeenUsedInThisTurn;
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    public void setAttacking(boolean attacking) {
        isAttacking = attacking;
    }

    public boolean isAttacked() {
        return isAttacked;
    }

    public void setAttacked(boolean attacked) {
        isAttacked = attacked;
    }

    public static ArrayList<Card> getAllCards() {
        return allCards;
    }

    public static void setAllCards(ArrayList<Card> allCards) {
        Card.allCards = allCards;
    }

    public Account getOwner() {
        return Owner;
    }

    public void setOwner(Account owner) {
        Owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public int getAllowedNumber() {
        return allowedNumber;
    }

    public void setAllowedNumber(int allowedNumber) {
        this.allowedNumber = allowedNumber;
    }

    public static Card getCardByName(String name) {
        for (Card card : allCards)
            if (card.getName().equals(name))
                return card;
        return null;
    }

    public void reset() {

    }



}
