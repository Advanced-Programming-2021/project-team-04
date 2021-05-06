package model;

import controller.DuelController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Game {
    //TODO a AI game Class or updates for this one?
    private HashMap<Duelist, Integer> maxLifePoint;
    private Duelist currentPlayer, theOtherPlayer;
    private int rounds;
    private int currentRound;
    private Phases currentPhase;
    private gameRounds totalRounds;
    private Card selectedCard;
    private boolean hasSummonedInThisTurn;
    private ArrayList<Card> cardsWithChangedPositions;
    private ArrayList<Duelist> roundWinners;
    private Card lastSetCard;
    private ArrayList<Card> cardsWhichAttacked;
    boolean isGameFinished = false;
    private Duelist[] winnerOfEachRound = new Duelist[3];

    public Game(Duelist firstPlayer, Duelist secondPlayer, int rounds) {
        setCurrentPlayer(firstPlayer);
        setRounds(rounds);
        setTheOtherPlayer(secondPlayer);
        initializeGame();
        setCurrentPhase(Phases.DRAW_PHASE);
        setCurrentRound(1);
    }

    public Phases getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(Phases currentPhase) {
        this.currentPhase = currentPhase;
    }

    public gameRounds getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(gameRounds totalRounds) {
        this.totalRounds = totalRounds;
    }

    public Card getLastSetCard() {
        return lastSetCard;
    }

    public void setLastSetCard(Card lastSetCard) {
        this.lastSetCard = lastSetCard;
    }

    public ArrayList<Card> getCardsWithChangedPositions() {
        return cardsWithChangedPositions;
    }

    public void setCardsWithChangedPositions(ArrayList<Card> cardsWithChangedPositions) {
        this.cardsWithChangedPositions = cardsWithChangedPositions;
    }

    public boolean isHasSummonedInThisTurn() {
        return hasSummonedInThisTurn;
    }

    public void setHasSummonedInThisTurn(boolean hasSummonedInThisTurn) {
        this.hasSummonedInThisTurn = hasSummonedInThisTurn;
    }

    public ArrayList<Duelist> getRoundWinners() {
        return roundWinners;
    }

    public void setRoundWinners(ArrayList<Duelist> roundWinners) {
        this.roundWinners = roundWinners;
    }

    public Card getSelectedCard() {
        return selectedCard;
    }

    public void setSelectedCard(Card selectedCard) {
        this.selectedCard = selectedCard;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public HashMap<Duelist, Integer> getMaxLifePoint() {
        return maxLifePoint;
    }

    public void setMaxLifePoint(HashMap<Duelist, Integer> maxLifePoint) {
        this.maxLifePoint = maxLifePoint;
    }

    public Duelist getCurrentPlayer() {
        //TODO what are the fucking related problems
        return currentPlayer;
    }

    public void setCurrentPlayer(Duelist currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public ArrayList<Card> getCardsWhichAttacked() {
        return cardsWhichAttacked;
    }

    public void setCardsWhichAttacked(ArrayList<Card> cardsWhichAttacked) {
        this.cardsWhichAttacked = cardsWhichAttacked;
    }

    public Duelist getTheOtherPlayer() {
        //TODO what are the fucking related problems
        return theOtherPlayer;
    }

    public void setTheOtherPlayer(Duelist theOtherPlayer) {
        this.theOtherPlayer = theOtherPlayer;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    private void initializeGame() {
        currentPlayer.setField(new Field(currentPlayer.getActiveDeck().getMainDeck()));
        theOtherPlayer.setField(new Field(theOtherPlayer.getActiveDeck().getMainDeck()));
        shuffleDeck();
        for (int i = 0; i < 5; i++) {
            currentPlayer.getField().getHand().add(currentPlayer.getField().getDeckZone().get(i));
            theOtherPlayer.getField().getHand().add(theOtherPlayer.getField().getDeckZone().get(i));
        }
    }

    public void shuffleDeck() {
        Collections.shuffle(currentPlayer.getActiveDeck().getMainDeck());
        Collections.shuffle(theOtherPlayer.getActiveDeck().getMainDeck());
    }

    public void changeTurn() {
        Duelist temp = currentPlayer;
        currentPlayer = theOtherPlayer;
        theOtherPlayer = temp;
        hasSummonedInThisTurn = false;
    }

    public boolean isGameFinished() {
        return isGameFinished;
    }

    //TODO from here, update the class to handle AI game as well.

    public void finishWithOneRound(Account loser, Account winner) {
        winner.setMoney(winner.getMoney() + 1000 + winner.getLP());
        loser.setMoney(loser.getMoney() + 100);
        isGameFinished = true;
        winner.reset();
        loser.reset();
    }

    public void finishWithThreeRounds(Account loser, Account winner) {
        switch (currentRound) {
            case 1:
                winnerOfEachRound[0] = winner;
                winner.setMaxLPofThreeRounds(winner.getLP());
                loser.setMaxLPofThreeRounds(loser.getLP());
                break;
            case 2:
                winner.checkMaxLPofThreeRounds();
                loser.checkMaxLPofThreeRounds();
                if (winnerOfEachRound[0] == winner) {
                    finishMultipleRoundGame(loser, winner);
                    return;
                }
                winnerOfEachRound[1] = winner;
                break;
            case 3:
                winner.checkMaxLPofThreeRounds();
                loser.checkMaxLPofThreeRounds();
                finishMultipleRoundGame(loser, winner);
                return;
        }
        DuelController.getInstance().chooseStarter(winner.getUsername());
        initializeGame();
        winner.reset();
        loser.reset();
    }

    private void finishMultipleRoundGame(Account loser, Account winner) {
        winner.setMoney(winner.getMoney() + 3000 + 3 * winner.getMaxLPofThreeRounds());
        loser.setMoney(loser.getMoney() + 300);
        isGameFinished = true;
    }

    public void finishGame(Account loser) {
        //TODO is this method ok AI-wise?
        Account winner = null;
        if (currentPlayer.equals(loser)) winner = theOtherPlayer;
        else winner = currentPlayer;
        winner.setScore(winner.getScore() + 1000);
        if (rounds == 1) finishWithOneRound(loser, winner);
        else finishWithThreeRounds(loser, winner);
    }
}
