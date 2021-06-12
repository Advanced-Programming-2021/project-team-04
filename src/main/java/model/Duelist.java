package model;

import com.google.gson.annotations.Expose;
import controller.DuelController;
import lombok.Getter;
import lombok.Setter;
import model.cards.Card;
import model.cards.MonsterCard;

import java.util.*;

@Getter
@Setter
public abstract class Duelist {

    @Expose
    protected ArrayList<PlayerDeck> allPlayerDecks = new ArrayList<>();
    @Expose
    protected LinkedHashMap<String, Short> allCardsHashMap = new LinkedHashMap<>();
    @Expose
    protected String activePlayerDeck;
    @Expose
    protected String username;
    @Expose
    protected String nickname;
    protected ArrayList<Card> allCardsArrayList = new ArrayList<>();
    protected Field field;
    protected int LP, countForRPS;
    protected int maxLPofThreeRounds;
    protected int countOfRoundsWon; //TODO should reset this
    protected boolean isAbleToDraw = true; //TODO should reset this
    protected boolean isAbleToAttack = true;

//    public Duelist() {
//        allCardsHashMap.keySet().forEach(n -> {
//            for (var i = 0; i < allCardsHashMap.get(n); i++)
//                allCardsArrayList.add(Objects.requireNonNull(ImportAndExport.getInstance().readCard(n)));
//        });
//    }

    public void checkMaxLPofThreeRounds() {
        if (maxLPofThreeRounds < LP) maxLPofThreeRounds = LP;
    }

    public void increaseCountForRPS() {
        this.countForRPS++;
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

//    public Card getCardByName(String cardName) {
//        return allCardsArrayList.stream().filter(c -> c.getName().equals(cardName)).findAny().orElse(null);
//    }

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

    public boolean hasDeck(String deckName) {
        return allPlayerDecks.stream().anyMatch(d -> d.getDeckName().equals(deckName));
    }

    public boolean hasCard(String cardName) {
        return allCardsHashMap.containsKey(cardName);
    }

    private void activateDeck(String deckName) {
        activePlayerDeck = deckName;
    }

    private boolean hasActiveDeck() {
        return activePlayerDeck != null;
    }

    private boolean hasEnoughCardInHand(int amount) { //TODO check while writing the code
        return true;
    }

    public void deleteField() {
        field = null;
    }

    public void reset() {
        setLP(8000);
        countForRPS = 0;
    }

    public void changeLP(int amount) {
        this.LP += amount;
        if (this.LP <= 0) DuelController.getInstance().getGame().finishGame(this);
    }
}
