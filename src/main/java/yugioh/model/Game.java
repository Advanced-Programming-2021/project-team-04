package yugioh.model;

import com.google.gson.annotations.Expose;
import yugioh.controller.DuelController;
import lombok.Getter;
import lombok.Setter;
import yugioh.model.cards.Card;

import java.util.*;

@Getter
@Setter
public class Game {

    @Expose // TODO: 6/20/2021 remove this @Expose after debug
    boolean isGameFinished = false;
    @Expose // TODO: 6/20/2021 remove this @Expose after debug
    private HashMap<Duelist, Integer> maxLifePoint;
    @Expose // TODO: 6/20/2021 remove this @Expose after debug
    private String currentPlayerUsername, theOtherPlayerUsername;
    private Duelist currentPlayer = null, theOtherPlayer = null;
    @Expose // TODO: 6/20/2021 remove this @Expose after debug
    private int rounds;
    @Expose // TODO: 6/20/2021 remove this @Expose after debug
    private int currentRound = 0;
    @Expose // TODO: 6/20/2021 remove this @Expose after debug
    private Phases currentPhase;
    @Expose // TODO: 6/20/2021 remove this @Expose after debug
    private GameRounds totalRounds;
    @Expose // TODO: 6/20/2021 remove this @Expose after debug
    private Card selectedCard;
    @Expose // TODO: 6/20/2021 remove this @Expose after debug
    private boolean summonedInThisTurn;
    @Expose // TODO: 6/20/2021 remove this @Expose after debug
    private ArrayList<Card> cardsWithChangedPositions;
    @Expose // TODO: 6/20/2021 remove this @Expose after debug
    private ArrayList<Duelist> roundWinners;
    @Expose // TODO: 6/20/2021 remove this @Expose after debug
    private Card lastSetCard;
    @Expose // TODO: 6/20/2021 remove this @Expose after debug
    private ArrayList<Card> cardsWhichAttacked;
    @Expose // TODO: 6/20/2021 remove this @Expose after debug
    private Duelist[] winnerOfEachRound = new Duelist[3];
    @Expose // TODO: 6/20/2021 remove this @Expose after debug
    private boolean isAI;

    public Game(Duelist firstPlayer, Duelist secondPlayer, int rounds, boolean isAI) {
        setRounds(rounds);
        setCurrentPlayer(firstPlayer);
        setCurrentPlayerUsername(firstPlayer.getUsername());
        setTheOtherPlayer(secondPlayer);
        setTheOtherPlayerUsername(secondPlayer.getUsername());
        setAI(isAI);
        initializeGame();
        setCurrentPhase(Phases.DRAW_PHASE);
        setCurrentRound(1);
        firstPlayer.reset();
        secondPlayer.reset();
    }

    private void initializeGame() {
//        shuffleDeck();
//        added the shuffle to GameDeck constructor so it should be ok.
        Field currentPlayerField = new Field(new GameDeck(currentPlayer.getActiveDeck()));
        Field theOtherPlayerField = new Field(new GameDeck(theOtherPlayer.getActiveDeck()));
        currentPlayer.setField(currentPlayerField);
        theOtherPlayer.setField(theOtherPlayerField);
        initializeCards(currentPlayer);
        initializeCards(theOtherPlayer);
        currentPlayer.getField().setHand(new ArrayList<>(currentPlayer.getField().getDeckZone().subList(0, 5)));
        currentPlayer.getField().getDeckZone().removeAll(currentPlayer.getField().getDeckZone().subList(0, 5));
        theOtherPlayer.getField().setHand(new ArrayList<>(theOtherPlayer.getField().getDeckZone().subList(0, 5)));
        theOtherPlayer.getField().getDeckZone().removeAll(theOtherPlayer.getField().getDeckZone().subList(0, 5));
        currentRound++;
    }

    private void initializeCards(Duelist duelist) {
        duelist.getField().getDeckZone().stream().filter(Objects::nonNull).peek(c -> c.setOwnerUsername(duelist.getUsername())).forEach(Card::reset);
        duelist.getField().getSideDeck().stream().filter(Objects::nonNull).peek(c -> c.setOwnerUsername(duelist.getUsername())).forEach(Card::reset);
    }

//    public void shuffleDeck() {
//        LinkedHashMap<String , Short> temp;
//        ArrayList<String> list;
//        list = new ArrayList<>(currentPlayer.getActiveDeck().getMainDeckCards().keySet());
//        Collections.shuffle(list);
//        temp = new LinkedHashMap<>();
//        list.forEach(c -> temp.put(c, currentPlayer.getActiveDeck().getMainDeckCards().get(c)));
//        currentPlayer.getActiveDeck().setMainDeckCards(new LinkedHashMap<>(temp));
//        list = new ArrayList<>(currentPlayer.getActiveDeck().getSideDeckCards().keySet());
//        Collections.shuffle(list);
//        temp = new LinkedHashMap<>();
//        list.forEach(c -> temp.put(c, currentPlayer.getActiveDeck().getSideDeckCards().get(c)));
//        currentPlayer.getActiveDeck().setSideDeckCards(new LinkedHashMap<>(temp));
//    }

    public void changeTurn() {
        Duelist temp = currentPlayer;
        currentPlayer = theOtherPlayer;
        theOtherPlayer = temp;
        summonedInThisTurn = false;
    }

    public void finishWithOneRound(Duelist loser, Duelist winner) {
        if (!isAI) {
            ((Account) winner).setMoney(((Account) winner).getMoney() + 1000 + winner.getLP());
            ((Account) loser).setMoney(((Account) loser).getMoney() + 100);
        } else if (winner instanceof Account)
            ((Account) winner).setMoney(((Account) winner).getMoney() + 1000 + winner.getLP());
        else if (loser instanceof Account)
            ((Account) loser).setMoney(((Account) loser).getMoney() + 100);
        isGameFinished = true;
//        winner.reset();
//        loser.reset();
        loser.deleteField();
        winner.deleteField();
    }

    public void finishWithThreeRounds(Duelist loser, Duelist winner) {
        switch (currentRound) {
            case 1 -> {
                winnerOfEachRound[0] = winner;
                winner.setMaxLPofThreeRounds(winner.getLP());
                loser.setMaxLPofThreeRounds(loser.getLP());
            }
            case 2 -> {
                winner.checkMaxLPofThreeRounds();
                loser.checkMaxLPofThreeRounds();
                if (winnerOfEachRound[0] == winner) {
                    finishMultipleRoundGame(loser, winner);
                    return;
                }
                winnerOfEachRound[1] = winner;
            }
            case 3 -> {
                winner.checkMaxLPofThreeRounds();
                loser.checkMaxLPofThreeRounds();
                finishMultipleRoundGame(loser, winner);
                return;
            }
        }
        if (winner instanceof Account)
            DuelController.getInstance().exchangeCardsWithSideDeck((Account) winner);
        if (loser instanceof Account)
            DuelController.getInstance().exchangeCardsWithSideDeck((Account) loser);
        DuelController.getInstance().chooseStarter(winner.getUsername());
//        winner.reset();
//        loser.reset();
        initializeGame();
    }

    private void finishMultipleRoundGame(Duelist loser, Duelist winner) {
        if (!isAI) {
            ((Account) winner).setMoney(((Account) winner).getMoney() + 3000 + winner.getLP());
            ((Account) loser).setMoney(((Account) loser).getMoney() + 300);
        } else if (winner instanceof Account)
            ((Account) winner).setMoney(((Account) winner).getMoney() + 3000 + winner.getLP());
        else if (loser instanceof Account)
            ((Account) loser).setMoney(((Account) loser).getMoney() + 300);
        isGameFinished = true;
        loser.deleteField();
        winner.deleteField();
//        winner.reset();
//        loser.reset();
    }

    public void finishGame(Duelist loser) {
        Duelist winner;
        if (currentPlayer.equals(loser)) winner = theOtherPlayer;
        else winner = currentPlayer;
        if (winner instanceof Account)
            ((Account) winner).setScore(((Account) winner).getScore() + 1000);
        if (rounds == 1) finishWithOneRound(loser, winner);
        else finishWithThreeRounds(loser, winner);
    }

    public Duelist getCurrentPlayer() {
        if (Objects.isNull(currentPlayer)) setCurrentPlayer(Account.getAccountByUsername(currentPlayerUsername));
        return currentPlayer;
    }

    public Duelist getTheOtherPlayer() {
        if (Objects.isNull(theOtherPlayer)) setTheOtherPlayer(Account.getAccountByUsername(theOtherPlayerUsername));
        return theOtherPlayer;
    }
}
