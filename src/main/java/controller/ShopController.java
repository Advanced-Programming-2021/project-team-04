package controller;

import model.*;
import view.Output;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ShopController {
    private static ShopController singleInstance = null;
    private static ArrayList<Card> allCards;
    private final Account thisPlayer = MainController.getInstance().getLoggedIn();

    static {
        allCards = new ArrayList<>();
    }

    public ShopController() {
        createCardForShop();
    }

    public static ShopController getInstance() {
        if (singleInstance == null)
            singleInstance = new ShopController();
        return singleInstance;
    }

    public void showAllCards() {
        String toPrint = "";
        for (Card card : allCards)
            toPrint += card.getName() + ":" + card.getPrice() + "\n";
        toPrint = toPrint.substring(0, toPrint.length() - 2);
        Output.getForNow();
    }

    public boolean isCardNameValid(String name) {
        String cardName = null;
        for (Card card : allCards)
            if (card.getName().equals(name)) {
                cardName = name;
                break;
            }
        return cardName != null;
    }

    public void buyCard(String cardName) {
        if (isCardNameValid(cardName)) {
            switch (cardName) {
                case "Change of Heart": {
                    thisPlayer.addCard(new ChangeOfHeart());
                    break;
                }
                case "Command Knight": {
                    thisPlayer.addCard(new CommandKnight());
                    break;
                }
                case "Man-Eater Bug": {
                    thisPlayer.addCard(new ManEaterBug());
                    break;
                }
                case "Messenger of peace": {
                    thisPlayer.addCard(new MessengerOfPeace());
                    break;
                }
                case "Scanner": {
                    thisPlayer.addCard(new Scanner());
                    break;
                }
                case "Suijin": {
                    thisPlayer.addCard(new Suijin());
                    break;
                }
                case "The Calculator": {
                    thisPlayer.addCard(new TheCalculator());
                    break;
                }
                case "Swords of Revealing Light": {
                    thisPlayer.addCard(new SwordsOfRevealingLight());
                    break;
                }
                case "United We Stand": {
                    thisPlayer.addCard(new UnitedWeStand());
                    break;
                }
                case "Sword of dark destruction": {
                    thisPlayer.addCard(new SwordOfDarkDestruction());
                    break;
                }
                case "Magnum Shield": {
                    thisPlayer.addCard(new MagnumShield());
                    break;
                }
                case "Black Pendant": {
                    thisPlayer.addCard(new BlackPendant());
                    break;
                }
                default: {
                    MonsterCard monsterCard = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/" + cardName + ".JSON");
                    if (monsterCard != null)
                        thisPlayer.addCard(monsterCard);
                    else {
                        SpellAndTrapCard spellAndTrapCard = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/monsters/" + cardName + ".JSON");
                        thisPlayer.addCard(spellAndTrapCard);
                    }
                }
            }
        }
    }

    public void createCardForShop() {
        addSpecialCards();
        addMonsterCards();
        addSpellAndTrap();
        sort();
    }

    private void addSpecialCards() {
        ChangeOfHeart changeOfHeart = new ChangeOfHeart();
        allCards.add(changeOfHeart);
        CommandKnight commandKnight = new CommandKnight();
        allCards.add(commandKnight);
        ManEaterBug manEaterBug = new ManEaterBug();
        allCards.add(manEaterBug);
        MessengerOfPeace messengerOfPeace = new MessengerOfPeace();
        allCards.add(messengerOfPeace);
        Suijin suijin = new Suijin();
        allCards.add(suijin);
        TheCalculator calculator = new TheCalculator();
        allCards.add(calculator);
        UnitedWeStand unitedWeStand = new UnitedWeStand();
        allCards.add(unitedWeStand);
        SwordsOfRevealingLight swordsOfRevealingLight = new SwordsOfRevealingLight();
        allCards.add(swordsOfRevealingLight);
        SwordOfDarkDestruction swordOfDarkDestruction = new SwordOfDarkDestruction();
        allCards.add(swordOfDarkDestruction);
        BlackPendant blackPendant = new BlackPendant();
        allCards.add(blackPendant);
        MagnumShield magnumShield = new MagnumShield();
        allCards.add(magnumShield);
    }

    private void addMonsterCards() {
        MonsterCard texChanger = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Texchanger.JSON");
        texChanger.reset();
        allCards.add(texChanger);
        MonsterCard alexandriteDragon = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Alexandrite Dragon.JSON");
        alexandriteDragon.reset();
        allCards.add(alexandriteDragon);
        MonsterCard axeRaider = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Axe Raider.JSON");
        axeRaider.reset();
        allCards.add(axeRaider);
        MonsterCard babyDragon = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Baby dragon.JSON");
        babyDragon.reset();
        allCards.add(babyDragon);
        MonsterCard battleOX = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Battle OX.JSON");
        battleOX.reset();
        allCards.add(battleOX);
        MonsterCard battleWarrior = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Battle warrior.JSON");
        battleWarrior.reset();
        allCards.add(battleWarrior);
        MonsterCard beastKingBarbaros = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Beast King Barbaros.JSON");
        beastKingBarbaros.reset();
        allCards.add(beastKingBarbaros);
        MonsterCard bitron = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Bitron.JSON");
        bitron.reset();
        allCards.add(bitron);
        MonsterCard blueEyesWhiteDragon = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Blue-Eyes white dragon.JSON");
        blueEyesWhiteDragon.reset();
        allCards.add(blueEyesWhiteDragon);
        MonsterCard crabTurtle = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Crab Turtle.JSON");
        crabTurtle.reset();
        allCards.add(crabTurtle);
        MonsterCard crawlingDragon = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Crab Turtle.JSON");
        crawlingDragon.reset();
        allCards.add(crawlingDragon);
        MonsterCard curtainOfTheDarkOnes = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Curtain of the dark ones.JSON");
        curtainOfTheDarkOnes.reset();
        allCards.add(curtainOfTheDarkOnes);
        MonsterCard darkBlade = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Dark Blade.JSON");
        darkBlade.reset();
        allCards.add(darkBlade);
        MonsterCard darkMagician = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Dark magician.JSON");
        darkMagician.reset();
        allCards.add(darkMagician);
        MonsterCard exploderDragon = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Exploder Dragon.JSON");
        exploderDragon.reset();
        allCards.add(exploderDragon);
        MonsterCard feralImp = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Feral Imp.JSON");
        feralImp.reset();
        allCards.add(feralImp);
        MonsterCard fireyarou = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Fireyarou.JSON");
        fireyarou.reset();
        allCards.add(fireyarou);
        MonsterCard flameManipulator = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Flame manipulator.JSON");
        flameManipulator.reset();
        allCards.add(flameManipulator);
        MonsterCard gateGuardian = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Gate Guardian.JSON");
        gateGuardian.reset();
        allCards.add(gateGuardian);
        MonsterCard haniwa = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Haniwa.JSON");
        haniwa.reset();
        allCards.add(haniwa);
        MonsterCard heraldOfCreation = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Herald of Creation.JSON");
        heraldOfCreation.reset();
        allCards.add(heraldOfCreation);
        MonsterCard heroOfTheEast = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Hero of the east.JSON");
        heroOfTheEast.reset();
        allCards.add(heroOfTheEast);
        MonsterCard hornImp = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Horn Imp.JSON");
        hornImp.reset();
        allCards.add(hornImp);
        MonsterCard leotron = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Leotron .JSON");
        leotron.reset();
        allCards.add(leotron);
        MonsterCard marshmallon = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Marshmallon.JSON");
        marshmallon.reset();
        allCards.add(marshmallon);
        MonsterCard mirageDragon = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Mirage Dragon.JSON");
        mirageDragon.reset();
        allCards.add(mirageDragon);
        MonsterCard silverFang = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Silver Fang.JSON");
        silverFang.reset();
        allCards.add(silverFang);
        MonsterCard skullGuardian = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Skull Guardian.JSON");
        skullGuardian.reset();
        allCards.add(skullGuardian);
        MonsterCard slotMachine = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Slot Machine.JSON");
        slotMachine.reset();
        allCards.add(slotMachine);
        MonsterCard spiralSerpent = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Spiral Serpent.JSON");
        spiralSerpent.reset();
        allCards.add(spiralSerpent);
        MonsterCard terratiger = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Terratiger, the Empowered Warrior.JSON");
        terratiger.reset();
        allCards.add(terratiger);
        MonsterCard theTricky = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/The Tricky.JSON");
        theTricky.reset();
        allCards.add(theTricky);
        MonsterCard warrior = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Warrior Dai Grepher.JSON");
        warrior.reset();
        allCards.add(warrior);
        MonsterCard wattailDragon = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Wattaildragon.JSON");
        wattailDragon.reset();
        allCards.add(wattailDragon);
        MonsterCard wattkid = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Wattkid.JSON");
        wattkid.reset();
        allCards.add(wattkid);
        MonsterCard yomiShip = ImportAndExport.getInstance().readMonsterCard("src/main/resources/monsters/Yomi Ship.JSON");
        yomiShip.reset();
        allCards.add(yomiShip);
    }

    private void addSpellAndTrap() {
        SpellAndTrapCard callOfTheHaunted = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Call of The Haunted.JSON");
        allCards.add(callOfTheHaunted);
        SpellAndTrapCard closedForest = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Closed Forest.JSON");
        allCards.add(closedForest);
        SpellAndTrapCard darkHole = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Dark Hole.JSON");
        allCards.add(darkHole);
        SpellAndTrapCard forest = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Forest.JSON");
        allCards.add(forest);
        SpellAndTrapCard harpiesFeatherDuster = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Harpie's Feather Duster.JSON");
        allCards.add(harpiesFeatherDuster);
        SpellAndTrapCard magicCylinder = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Magic Cylinder.JSON");
        allCards.add(magicCylinder);
        SpellAndTrapCard magicJamamer = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Magic Jamamer.JSON");
        allCards.add(magicJamamer);
        SpellAndTrapCard mindCrush = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Mind Crush.JSON");
        allCards.add(mindCrush);
        SpellAndTrapCard mirrorForce = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Mirror Force.JSON");
        allCards.add(mirrorForce);
        SpellAndTrapCard monsterReborn = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Monster Reborn.JSON");
        allCards.add(monsterReborn);
        SpellAndTrapCard mysticalSpaceTyphoon = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Mystical space typhoon.JSON");
        allCards.add(mysticalSpaceTyphoon);
        SpellAndTrapCard negateAttack = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Negate Attack.JSON");
        allCards.add(negateAttack);
        SpellAndTrapCard potOfGreed = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Pot of Greed.JSON");
        allCards.add(potOfGreed);
        SpellAndTrapCard raigeki = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Raigeki.JSON");
        allCards.add(raigeki);
        SpellAndTrapCard ringOfDefense = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Ring of defense.JSON");
        allCards.add(ringOfDefense);
        SpellAndTrapCard solemnWarning = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Solemn Warning.JSON");
        allCards.add(solemnWarning);
        SpellAndTrapCard spellAbsorption = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Spell Absorption.JSON");
        allCards.add(spellAbsorption);
        SpellAndTrapCard supplySquad = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Supply Squad.JSON");
        allCards.add(supplySquad);
        SpellAndTrapCard terraForming = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Terraforming.JSON");
        allCards.add(terraForming);
        SpellAndTrapCard timeSeal = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Time Seal.JSON");
        allCards.add(timeSeal);
        SpellAndTrapCard torrentialTribute = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Torrential Tribute.JSON");
        allCards.add(torrentialTribute);
        SpellAndTrapCard trapHole = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Trap Hole.JSON");
        allCards.add(trapHole);
        SpellAndTrapCard twinTwisters = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Twin Twisters.JSON");
        allCards.add(twinTwisters);
        SpellAndTrapCard umiiruka = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Umiiruka.JSON");
        allCards.add(umiiruka);
        SpellAndTrapCard vanityEmptiness = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Vanity's Emptiness.JSON");
        allCards.add(vanityEmptiness);
        SpellAndTrapCard wallOfRevealingLight = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Wall of Revealing Light.JSON");
        allCards.add(wallOfRevealingLight);
        SpellAndTrapCard yami = ImportAndExport.getInstance().readSpellAndTrapCard("src/main/resources/spellandtraps/Yami.JSON");
        allCards.add(yami);
    }

    private void sort() {
        allCards.sort(new Comparator<Card>() {
            @Override
            public int compare(Card o1, Card o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }
}
