package model;

import java.util.ArrayList;

public class Card {
    protected static ArrayList<Card> allCards = new ArrayList<>();
    protected String name;
    protected int price;
    protected String description;
    protected int speed = 1;
    protected int allowedNumber;
    protected boolean isAttacked = false;
    protected boolean isAttacking = false;
    protected boolean isMonster = false;
    //probably should handle more than this, maybe make an entry set for both cards *or a hashmap idk
    protected Account Owner;
    public Card() {
        allCards.add(this);
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

    public boolean isValid() {
        return true;
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

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getAllowedNumber() {
        return allowedNumber;
    }

    public void setAllowedNumber(int allowedNumber) {
        this.allowedNumber = allowedNumber;
    }

    public void reset() {

    }

}
