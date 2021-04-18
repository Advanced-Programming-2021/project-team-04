package model;

public abstract class Card {
    protected String name;
    protected int price;
    protected String description;
    protected int speed;
    protected int allowedNumber;
    protected String id;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
