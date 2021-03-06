package model;


import lombok.Getter;
import lombok.Setter;
import controller.DuelController;
import model.cards.Card;

import java.util.ArrayList;
import java.util.Objects;


@Getter
@Setter
public class Game {


    boolean isGameFinished = false;
    private String currentPlayerUsername;
    private String theOtherPlayerUsername;
    private Duelist currentPlayer = null;
    private Duelist theOtherPlayer = null;
    private int rounds;
    private int currentRound = 0;
    private Phases currentPhase;
    private Card selectedCard;
    private boolean summonedInThisTurn;
    private Duelist[] winnerOfEachRound = new Duelist[3];
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
    }


    private void initializeGame() {
        selectedCard = null;
        summonedInThisTurn = false;
        currentPhase = Phases.DRAW_PHASE;
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
        currentPlayer.reset();
        theOtherPlayer.reset();
    }


    private void initializeCards(Duelist duelist) {
        duelist.getField().getDeckZone().stream().filter(Objects::nonNull).peek(c -> c.setOwnerUsername(duelist.getUsername())).forEach(Card::reset);
        duelist.getField().getSideDeck().stream().filter(Objects::nonNull).peek(c -> c.setOwnerUsername(duelist.getUsername())).forEach(Card::reset);
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