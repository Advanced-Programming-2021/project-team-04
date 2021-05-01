package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Game {
    private HashMap<Account, Integer> maxLifePoint;
    private Account currentPlayer, theOtherPlayer, winnerOfRPS;
    private int rounds;
    private int currentRound;
    private Phases currentPhase;
    private gameRounds totalRounds;
    private Card selectedCard;
    private boolean hasSummonedInThisTurn;
    private ArrayList<Card> cardsWithChangedPositions;
    private ArrayList<Account> roundWinners;
    private Card lastSetCard;
    private ArrayList<Card> cardsWhichAttacked;

    public Game(Account firstPlayer, Account secondPlayer, int rounds) {
        setCurrentPlayer(firstPlayer);
        setRounds(rounds);
        setTheOtherPlayer(secondPlayer);
        initializeGame();
    }

    public Account getWinnerOfRPS() {
        return winnerOfRPS;
    }

    public void setWinnerOfRPS(Account winnerOfRPS) {
        this.winnerOfRPS = winnerOfRPS;
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

    public ArrayList<Account> getRoundWinners() {
        return roundWinners;
    }

    public void setRoundWinners(ArrayList<Account> roundWinners) {
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

    public HashMap<Account, Integer> getMaxLifePoint() {
        return maxLifePoint;
    }

    public void setMaxLifePoint(HashMap<Account, Integer> maxLifePoint) {
        this.maxLifePoint = maxLifePoint;
    }

    public Account getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Account currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public ArrayList<Card> getCardsWhichAttacked() {
        return cardsWhichAttacked;
    }

    public void setCardsWhichAttacked(ArrayList<Card> cardsWhichAttacked) {
        this.cardsWhichAttacked = cardsWhichAttacked;
    }

    public Account getTheOtherPlayer() {
        return theOtherPlayer;
    }

    public void setTheOtherPlayer(Account theOtherPlayer) {
        this.theOtherPlayer = theOtherPlayer;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    private void initializeGame() {
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

    public void addDecks() {

    }


    public boolean isRoundFinished() {
        return true;
    }

    public void setRoundFinished(boolean roundFinished) {

    }

    public boolean getIsRoundFinished() {
        return true;
    }

    public void changeTurn() {
        Account temp = currentPlayer;
        currentPlayer = theOtherPlayer;
        theOtherPlayer = temp;
        hasSummonedInThisTurn = false;
    }

    public boolean isRoundValid(int round) {
        return true;
    }

    public boolean hasSelectedCard() {
        return false;
    }

    public void handleEndingWithOneRound() {

    }

    public void handleEndingWithThreeRound() {

    }

    public void drawCard() {

    }

    public void switchTurn() {

    }

    public void switchMonsterMode() {

    }

    public void activateSpell() {

    }

    public boolean canBeSummoned() {
        return false;
    }

    public void addToCardsWhosePositionHasChanged() {

    }

    public void initializeCardsWhosePositionHasChanged() {

    }

    public void setPosition(boolean isAttack) {

    }

    public void setLastCard(Card card) {

    }

    public boolean isCardSetLast() {
        return false;
    }

    public void addToCardsWhichAttacked() {

    }

    public void initializeCardsWhichAttacked() {

    }

    public void attackCard(Card cardToBeAttackedTo) {

    }

    public void directAttack() {

    }

    public void ritualSummon() {

    }

    public void showSelectedCard() {

    }

    public boolean isGameFinished() {
        return true;
    }

    public void finishGame() {

    }
}
