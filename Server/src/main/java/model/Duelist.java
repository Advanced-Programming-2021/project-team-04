package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import controller.DuelController;
import lombok.Getter;
import lombok.Setter;
import model.cards.MonsterCard;

import java.util.ArrayList;
import java.util.LinkedHashMap;

@Getter
@Setter
public abstract class Duelist {

    @JsonProperty
    protected ArrayList<model.PlayerDeck> allPlayerDecks = new ArrayList<>();
    @JsonProperty
    protected LinkedHashMap<String, Short> allCardsHashMap = new LinkedHashMap<>();
    @JsonProperty
    protected String activePlayerDeck;
    @JsonProperty
    protected String username;
    @JsonProperty
    protected String nickname;
    protected Field field;
    protected int LP;
    protected int countForRPS;
    protected int maxLPofThreeRounds;
    protected boolean isAbleToDraw = true;
    protected boolean isAbleToAttack = true;

    public void checkMaxLPofThreeRounds() {
        if (maxLPofThreeRounds < LP) maxLPofThreeRounds = LP;
    }

    public PlayerDeck getActiveDeck() {
        return getDeckByName(activePlayerDeck);
    }

    public boolean getCardInHand(String cardName) {
        return field.getHand().stream().anyMatch(c -> c.getName().equals(cardName));
    }

    public MonsterCard getMirageDragon() {
        return field.getMonsterCards().stream().filter(m -> m.getName().equals("Mirage Dragon")).findAny().orElse(null);
    }

    public PlayerDeck getDeckByName(String deckName) {
        return allPlayerDecks.stream().filter(d -> d.getDeckName().equals(deckName)).findAny().orElse(null);
    }

    public void addDeck(PlayerDeck playerDeck) {
        this.getAllPlayerDecks().add(playerDeck);
    }

    public void deleteDeck(PlayerDeck playerDeck) {
        this.getAllPlayerDecks().remove(playerDeck);
    }

    public void addCard(String cardName) {
        if (allCardsHashMap.containsKey(cardName)) allCardsHashMap.replace(cardName, (short) (allCardsHashMap.get(cardName) + 1));
        else allCardsHashMap.put(cardName, (short) 1);
    }

    public void deleteField() {
        field = null;
    }

    public void reset() {
        setLP(8000);
        countForRPS = 0;
        isAbleToDraw = true;
        isAbleToAttack = true;
    }

    public void changeLP(int amount) {
        this.LP += amount;
        if (this.LP <= 0) DuelController.getGame().finishGame(this);
//        DuelView.setLP(username, LP);
        //TODO make method for this
    }

    public String toString() {
        return "username: " + username + "\nnickname: " + nickname;
    }
}