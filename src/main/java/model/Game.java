package model;

import controller.DuelController;

import java.util.*;

public class Game {
    boolean isGameFinished = false;
    private HashMap<Duelist, Integer> maxLifePoint;
    private Duelist currentPlayer, theOtherPlayer;
    private int rounds;
    private int currentRound = 0;
    private Phases currentPhase;
    private gameRounds totalRounds;
    private Card selectedCard;
    private boolean hasSummonedInThisTurn;
    private ArrayList<Card> cardsWithChangedPositions;
    private ArrayList<Duelist> roundWinners;
    private Card lastSetCard;
    private ArrayList<Card> cardsWhichAttacked;
    private Duelist[] winnerOfEachRound = new Duelist[3];
    private boolean isAI;

    public Game(Duelist firstPlayer, Duelist secondPlayer, int rounds, boolean isAI) {
        setCurrentPlayer(firstPlayer);
        setRounds(rounds);
        setTheOtherPlayer(secondPlayer);
        setAI(isAI);
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

    public boolean hasSummonedInThisTurn() {
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
        return currentPlayer;
    }

    public void setCurrentPlayer(Duelist currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Duelist getTheOtherPlayer() {
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

    public boolean isAI() {
        return isAI;
    }

    public void setAI(boolean AI) {
        isAI = AI;
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
        currentPlayer.getField().setHand((ArrayList<Card>) currentPlayer.getField().getDeckZone().subList(0, 5));
        currentPlayer.getField().getDeckZone().removeAll(currentPlayer.getField().getDeckZone().subList(0, 5));
        theOtherPlayer.getField().setHand((ArrayList<Card>) theOtherPlayer.getField().getDeckZone().subList(0, 5));
        theOtherPlayer.getField().getDeckZone().removeAll(theOtherPlayer.getField().getDeckZone().subList(0, 5));
        currentRound++;
    }

    private void initializeCards(Duelist duelist) {
        duelist.getField().getDeckZone().forEach(c -> {
            if (c instanceof MonsterCard) c.setOwner(duelist);
            c.reset();
        });
        duelist.getField().getSideDeck().forEach(c -> {
            if (c instanceof MonsterCard) c.setOwner(duelist);
            c.reset();
        });
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
        hasSummonedInThisTurn = false;
    }

    public boolean isGameFinished() {
        return isGameFinished;
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
        winner.reset();
        loser.reset();
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
        winner.reset();
        loser.reset();
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
        winner.reset();
        loser.reset();
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
}
