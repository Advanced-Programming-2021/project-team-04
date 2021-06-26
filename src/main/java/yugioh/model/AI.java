package yugioh.model;

import yugioh.controller.DuelController;
import yugioh.controller.ImportAndExport;
import lombok.Getter;
import lombok.Setter;
import yugioh.model.cards.MonsterCard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

@Getter
@Setter
public class AI extends Duelist {

    private static AI singleInstance = null;

    private AI() {
        username = "AI";
        nickname = "The Mechanisms";
        setAllPlayerDecks(ImportAndExport.getInstance().readAllDecks("src/main/resources/decks/"));
        setActivePlayerDeck("deck");
    }

    public static AI getInstance() {
        if (singleInstance == null)
            singleInstance = new AI();
        return singleInstance;
    }

    public void attack(Duelist opponent) {
        MonsterCard strongestAIMonster = getStrongestMonsterCardInZone();
        if (Objects.isNull(strongestAIMonster)) return;
        DuelController.getInstance().selectCard(true, CardStatusInField.MONSTER_FIELD,
                getField().getMonsterCards().indexOf(strongestAIMonster));
        MonsterCard opponentsWeakestAttackCard = getOpponentsWeakestAttackCard(opponent);
        MonsterCard opponentsWeakestDefenseCard = getOpponentsWeakestDefenseCard(opponent);
        if (Objects.isNull(opponentsWeakestAttackCard) && Objects.isNull(opponentsWeakestDefenseCard))
            DuelController.getInstance().directAttack();
        else if (Objects.nonNull(opponentsWeakestAttackCard) &&
                strongestAIMonster.getThisCardAttackPower() > opponentsWeakestAttackCard.getThisCardAttackPower())
            DuelController.getInstance().attack(opponent.getField().getMonsterCards().indexOf(opponentsWeakestAttackCard));
        else if (Objects.nonNull(opponentsWeakestDefenseCard) &&
                strongestAIMonster.getThisCardAttackPower() > opponentsWeakestDefenseCard.getThisCardDefensePower())
            DuelController.getInstance().attack(opponent.getField().getMonsterCards().indexOf(opponentsWeakestDefenseCard));
        else return;
        attack(opponent);
    }

    public MonsterCard getStrongestMonsterCardInZone() {
        return getField().getMonsterCards().stream()
                .filter(m -> m.getMonsterCardModeInField().equals(MonsterCardModeInField.ATTACK_FACE_UP))
                .filter(m -> m.isAbleToAttack() && !m.isAttacked()).max(Comparator.comparing(MonsterCard::getThisCardAttackPower))
                .orElse(null);
    }

    public MonsterCard getOpponentsWeakestAttackCard(Duelist opponent) {
        return opponent.getField().getMonsterCards().stream()
                .filter(m -> m.getMonsterCardModeInField().equals(MonsterCardModeInField.ATTACK_FACE_UP))
                .min(Comparator.comparing(MonsterCard::getThisCardAttackPower)).orElse(null);
    }

    public MonsterCard getOpponentsWeakestDefenseCard(Duelist opponent) {
        return opponent.getField().getMonsterCards().stream()
                .filter(m -> m.getMonsterCardModeInField().equals(MonsterCardModeInField.DEFENSE_FACE_DOWN) ||
                        m.getMonsterCardModeInField().equals(MonsterCardModeInField.DEFENSE_FACE_UP))
                .min(Comparator.comparing(MonsterCard::getThisCardDefensePower)).orElse(null);
    }

    public MonsterCard getStrongestMonsterCardInHandWithTwoTributes() {
        return getField().getHand().stream().filter(c -> c instanceof MonsterCard).map(c -> (MonsterCard) c)
                .filter(m -> m.getLevel() > 6).max(Comparator.comparing(MonsterCard::getThisCardAttackPower)).orElse(null);
    }

    public MonsterCard getStrongestMonsterCardInHandWithOneTribute() {
        return getField().getHand().stream().filter(c -> c instanceof MonsterCard).map(c -> (MonsterCard) c)
                .filter(m -> m.getLevel() > 4 && m.getLevel() < 7).max(Comparator.comparing(MonsterCard::getThisCardAttackPower)).orElse(null);
    }


    public MonsterCard getStrongestMonsterCardInHandWithNoTributes() {
        return getField().getHand().stream().filter(c -> c instanceof MonsterCard).map(c -> (MonsterCard) c)
                .filter(m -> m.getLevel() < 5).max(Comparator.comparing(MonsterCard::getThisCardAttackPower)).orElse(null);
    }

    public void summonMonster() {
        var cardToSummon = getStrongestMonsterCardInHandWithTwoTributes();
        if (Objects.nonNull(cardToSummon) && canTributeTwoMonsters()) {
            DuelController.getInstance().getGame().setSelectedCard(cardToSummon);
            DuelController.getInstance().summon();
            return;
        }
        cardToSummon = getStrongestMonsterCardInHandWithOneTribute();
        if (Objects.nonNull(cardToSummon) && canTributeOneMonster()) {
            DuelController.getInstance().getGame().setSelectedCard(cardToSummon);
            DuelController.getInstance().summon();
            return;
        }
        cardToSummon = getStrongestMonsterCardInHandWithNoTributes();
        if (Objects.nonNull(cardToSummon)) {
            DuelController.getInstance().getGame().setSelectedCard(cardToSummon);
            DuelController.getInstance().summon();
        }
    }

    public boolean canTributeOneMonster() {
        return getField().getMonsterCards().stream().anyMatch(m -> m.getLevel() < 5);
    }

    public boolean canTributeTwoMonsters() {
        return getField().getMonsterCards().stream().filter(m -> m.getLevel() < 5).count() > 1;
    }

    public int getWeakestMonsterCardInZone() {
        return getField().getMonsterCards().indexOf(getField().getMonsterCards().stream().filter(m -> m.getLevel() < 5)
                .min(Comparator.comparing(MonsterCard::getThisCardAttackPower)).get());
    }

    public int getSecondWeakestMonsterCardInZone() {
        return getField().getMonsterCards().indexOf(getField().getMonsterCards().stream().filter(m -> m.getLevel() < 5)
                .sorted(Comparator.comparing(MonsterCard::getThisCardAttackPower)).skip(1).findFirst().get());
    }
}