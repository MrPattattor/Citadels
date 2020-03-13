package com.montaury.citadels;

import com.montaury.citadels.character.Character;
import com.montaury.citadels.character.RandomCharacterSelector;
import com.montaury.citadels.district.Card;
import com.montaury.citadels.district.District;
import com.montaury.citadels.district.DistrictType;
import com.montaury.citadels.player.ComputerController;
import com.montaury.citadels.player.HumanController;
import com.montaury.citadels.player.Player;
import com.montaury.citadels.round.GameRoundAssociations;
import com.montaury.citadels.round.Group;
import com.montaury.citadels.round.action.DestroyDistrictAction;
import io.vavr.Tuple;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;

import java.util.Collections;
import java.util.Scanner;
import java.util.logging.Logger;

public class Citadels {
    static final String DRAW_TWO_CARDS_AND_KEEP_ONE ="Draw 2 cards and keep 1";
    static final String RECEIVE_TWO_COINS = "Receive 2 coins";
    static final String DRAW_THREE_CARDS_AND_KEEP_ONE = "Draw 3 cards and keep 1";
    static final String EXCHANGE_CARDS_WITH_PILE = "Exchange cards with pile";
    static final String RECEIVE_INCOME = "Receive income";
    static final String PICK_TWO_CARDS = "Pick 2 cards";
    static final String BUILD_DISTRICT = "Build district";
    static final String DESTROY_DISTRICT = "Destroy district";
    static final String DRAW_THREE_CARDS_FOR_TWO_COINS = "Draw 3 cards for 2 coins";
    static final String DISCARD_CARD_FOR_TWO_COINS = "Discard card for 2 coins";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Hello! Quel est votre nom ? ");
        String playerName = (scanner.next()).concat(" " + scanner.next());
        System.out.println("Quel est votre age ? ");
        int playerAge = scanner.nextInt();
        Board board = new Board();
        Player p = new Player(playerName, playerAge, new City(board), new HumanController());
        p.human = true;
        List<Player> players = List.of(p);
        System.out.println("Saisir le nombre de joueurs total (entre 2 et 8): ");
        int nbP;
        do {
            nbP = scanner.nextInt();
        } while (nbP < 2 || nbP > 8);
        for (int joueurs = 0; joueurs < nbP; joueurs += 1) {
            Player player = new Player("Computer " + joueurs, 35, new City(board), new ComputerController());
            player.computer = true;
            players = players.append(player);
        }
        CardPile pioche = new CardPile(Card.all().toList().shuffle());
        players.forEach(player -> {
            player.add(2);
            player.add(pioche.draw(2));
        });
        Player crown = players.maxBy(Player::age).get();

        List<Group> roundAssociations;
        do {
            java.util.List<Player> list = players.asJavaMutable();
            Collections.rotate(list, -players.indexOf(crown));
            List<Player> playersInOrder = List.ofAll(list);
            RandomCharacterSelector randomCharacterSelector = new RandomCharacterSelector();
            List<Character> availableCharacters = List.of(Character.ASSASSIN, Character.THIEF, Character.MAGICIAN, Character.KING, Character.BISHOP, Character.MERCHANT, Character.ARCHITECT, Character.WARLORD);

            List<Character> availableCharacters1 = availableCharacters;
            List<Character> discardedCharacters = List.empty();
            for (int i = 0; i < 1; i++) {
                Character discardedCharacter = randomCharacterSelector.among(availableCharacters1);
                discardedCharacters = discardedCharacters.append(discardedCharacter);
                availableCharacters1 = availableCharacters1.remove(discardedCharacter);
            }
            Character faceDownDiscardedCharacter = discardedCharacters.head();
            availableCharacters = availableCharacters.remove(faceDownDiscardedCharacter);

            List<Character> availableCharacters11 = availableCharacters.remove(Character.KING);
            List<Character> discardedCharacters1 = List.empty();
            for (int i = 0; i < 7 - playersInOrder.size() - 1; i++) {
                Character discardedCharacter = randomCharacterSelector.among(availableCharacters11);
                discardedCharacters1 = discardedCharacters1.append(discardedCharacter);
                availableCharacters11 = availableCharacters11.remove(discardedCharacter);
            }
            List<Character> faceUpDiscardedCharacters = discardedCharacters1;
            availableCharacters = availableCharacters.removeAll(faceUpDiscardedCharacters);

            List<Group> associations1 = List.empty();
            for (Player player : playersInOrder) {
                System.out.println(player.name() + " doit choisir un personnage");
                availableCharacters = availableCharacters.size() == 1 && playersInOrder.size() == 7 ? availableCharacters.append(faceDownDiscardedCharacter) : availableCharacters;
                Character selectedCharacter = player.controller.selectOwnCharacter(availableCharacters, faceUpDiscardedCharacters);
                availableCharacters = availableCharacters.remove(selectedCharacter);
                associations1 = associations1.append(new Group(player, selectedCharacter));
            }
            List<Group> associations = associations1;
            GameRoundAssociations groups = new GameRoundAssociations(associations);

            for (int iii = 0; iii < 8; iii++) {
                for (int ii = 0; ii < associations.size(); ii++) {
                    if (iii + 1 == associations.get(ii).character.number()) {
                        if (associations.get(ii).isMurdered()) {}
                        else
                        {
                            Group group = associations.get(ii);
                            associations.get(ii).thief().peek(thief -> thief.steal(group.player()));
                            Set<String> baseActions = HashSet.of(DRAW_TWO_CARDS_AND_KEEP_ONE, RECEIVE_TWO_COINS);
                            List<District> districts = group.player().city().districts();
                            Set<String> availableActions = baseActions;
                            for (District d : districts) {
                                if (d == District.OBSERVATORY) {
                                    availableActions = availableActions.replace(DRAW_TWO_CARDS_AND_KEEP_ONE, DRAW_THREE_CARDS_AND_KEEP_ONE);
                                }
                            }
                            // keep only actions that player can realize
                            List<String> possibleActions = List.empty();
                            for (String action : availableActions) {
                                if (action.equals(DRAW_TWO_CARDS_AND_KEEP_ONE)) {
                                    if (pioche.canDraw(2))
                                        possibleActions = possibleActions.append(DRAW_TWO_CARDS_AND_KEEP_ONE);
                                }
                                else if (action.equals(DRAW_THREE_CARDS_AND_KEEP_ONE)) {
                                    if (pioche.canDraw(3))
                                        possibleActions = possibleActions.append(DRAW_TWO_CARDS_AND_KEEP_ONE);
                                }
                                else {
                                    possibleActions = possibleActions.append(action);
                                }
                            }
                            String actionType = group.player().controller.selectActionAmong(possibleActions.toList());
                            // execute selected action
                            if (actionType.equals(DRAW_TWO_CARDS_AND_KEEP_ONE)) {
                                Set<Card> cardsDrawn = pioche.draw(2);
                                if (!group.player().city().has(District.LIBRARY)) {
                                    Card keptCard = group.player().controller.selectAmong(cardsDrawn);
                                    pioche.discard(cardsDrawn.remove(keptCard).toList());
                                    cardsDrawn = HashSet.of(keptCard);
                                }
                                group.player().add(cardsDrawn);
                            }
                            else if (actionType.equals(RECEIVE_TWO_COINS)) {
                                group.player().add(2);
                            }
                            else if (actionType.equals(DRAW_THREE_CARDS_AND_KEEP_ONE)) {
                                Set<Card> cardsDrawn = pioche.draw(3);
                                if (!group.player().city().has(District.LIBRARY)) {
                                    Card keptCard = group.player().controller.selectAmong(cardsDrawn);
                                    pioche.discard(cardsDrawn.remove(keptCard).toList());
                                    cardsDrawn = HashSet.of(keptCard);
                                }
                                group.player().add(cardsDrawn);
                            }
                            actionExecuted(group, actionType, associations);

                            // receive powers from the character
                            List<String> powers = null;
                            if (group.character == Character.ASSASSIN) {
                                powers = List.of("Kill");
                            }
                            else if (group.character == Character.THIEF) {
                                powers = List.of("Rob");
                            }
                            else if (group.character == Character.MAGICIAN) {
                                powers = List.of("Exchange cards with other player", EXCHANGE_CARDS_WITH_PILE);
                            }
                            else if (group.character == Character.KING) {
                                powers = List.of(RECEIVE_INCOME);
                            }
                            else if (group.character == Character.BISHOP) {
                                powers = List.of(RECEIVE_INCOME);
                            }
                            else if (group.character == Character.MERCHANT) {
                                powers = List.of(RECEIVE_INCOME, "Receive 1 gold");
                            }
                            else if (group.character == Character.ARCHITECT) {
                                powers = List.of(PICK_TWO_CARDS, BUILD_DISTRICT, BUILD_DISTRICT);
                            }
                            else if (group.character == Character.WARLORD) {
                                powers = List.of(RECEIVE_INCOME, DESTROY_DISTRICT);
                            }
                            else {
                                System.out.println("Uh oh");
                            }
                            List<String>  extraActions = List.empty();
                            for (District d : group.player().city().districts()) {
                                if (d == District.SMITHY) {
                                    extraActions = extraActions.append(DRAW_THREE_CARDS_FOR_TWO_COINS);
                                }
                                if (d == District.LABORATORY) {
                                    extraActions = extraActions.append(DISCARD_CARD_FOR_TWO_COINS);
                                }
                            }
                            Set<String> availableActions11 = Group.OPTIONAL_ACTIONS
                                    .addAll(powers)
                                    .addAll(extraActions);
                            String actionType11;
                            do {
                                Set<String> availableActions1 = availableActions11;
                                // keep only actions that player can realize
                                List<String> possibleActions2 = List.empty();
                                for (String action : availableActions1) {
                                    if (action.equals(BUILD_DISTRICT)) {
                                        if (!group.player().buildableDistrictsInHand().isEmpty())
                                            possibleActions2 = possibleActions2.append(BUILD_DISTRICT);
                                    }
                                    else if (action.equals(DESTROY_DISTRICT)) {
                                        if (DestroyDistrictAction.districtsDestructibleBy(groups, group.player()).exists(districtsByPlayer -> !districtsByPlayer._2().isEmpty())) {
                                            possibleActions2 = possibleActions2.append(DESTROY_DISTRICT);
                                        }
                                    }
                                    else if (action.equals(DISCARD_CARD_FOR_TWO_COINS)) {
                                        if (!group.player().cards().isEmpty()) {
                                            possibleActions2 = possibleActions2.append(DISCARD_CARD_FOR_TWO_COINS);
                                        }
                                    }
                                    else if (action.equals(DRAW_THREE_CARDS_FOR_TWO_COINS)) {
                                        if (pioche.canDraw(3) && group.player().canAfford(2))
                                            possibleActions2 = possibleActions2.append(DRAW_THREE_CARDS_FOR_TWO_COINS);
                                    }
                                    else if (action.equals(EXCHANGE_CARDS_WITH_PILE)) {
                                        if (!group.player().cards().isEmpty() && pioche.canDraw(1)) {
                                            possibleActions2 = possibleActions2.append(EXCHANGE_CARDS_WITH_PILE);
                                        }
                                    }
                                    else if (action.equals(PICK_TWO_CARDS)) {
                                        if (pioche.canDraw(2))
                                            possibleActions2 = possibleActions2.append(PICK_TWO_CARDS);
                                    }
                                    else
                                        possibleActions2 = possibleActions2.append(action);
                                }
                                String actionType1 = group.player().controller.selectActionAmong(possibleActions2.toList());
                                // execute selected action
                                if (actionType1.equals("End round"))
                                    {} else if (actionType1.equals(BUILD_DISTRICT)) {
                                    Card card = group.player().controller.selectAmong(group.player().buildableDistrictsInHand());
                                    group.player().buildDistrict(card);
                                    }
                                    else if (actionType1.equals(DISCARD_CARD_FOR_TWO_COINS)) {
                                    Player player = group.player();
                                    Card card = player.controller.selectAmong(player.cards());
                                    player.cards = player.cards().remove(card);
                                    pioche.discard(card);
                                    player.add(2);
                                    }
                                    else if (actionType1.equals(DRAW_THREE_CARDS_FOR_TWO_COINS)) {
                                    group.player().add(pioche.draw(3));
                                    group.player().pay(2);
                                    }
                                    else if (actionType1.equals(EXCHANGE_CARDS_WITH_PILE)) {
                                    Set<Card> cardsToSwap = group.player().controller.selectManyAmong(group.player().cards());
                                    group.player().cards = group.player().cards().removeAll(cardsToSwap);
                                    group.player().add(pioche.swapWith(cardsToSwap.toList()));
                                    }
                                    else if (actionType1.equals("Exchange cards with other player")) {
                                    Player playerToSwapWith = group.player().controller.selectPlayerAmong(groups.associations.map(Group::player).remove(group.player()));
                                    group.player().exchangeHandWith(playerToSwapWith);
                                    }
                                    else if (actionType1.equals("Kill")) {
                                    Character characterToMurder = group.player().controller.selectAmong(List.of(Character.THIEF, Character.MAGICIAN, Character.KING, Character.BISHOP, Character.MERCHANT, Character.ARCHITECT, Character.WARLORD));
                                    groups.associationToCharacter(characterToMurder).peek(Group::murder);
                                    }
                                    else if (actionType1.equals(PICK_TWO_CARDS)) {
                                    group.player().add(pioche.draw(2));
                                    }
                                    else if (actionType1.equals(RECEIVE_TWO_COINS)) {
                                    group.player().add(2);
                                    }
                                    else if (actionType1.equals("Receive 1 gold")) {
                                    group.player().add(1);
                                    }
                                    else if (actionType1.equals(RECEIVE_INCOME)) {
                                    DistrictType type = null;
                                    if (group.character == Character.BISHOP) {
                                            type = DistrictType.RELIGIOUS;
                                        }
                                    else if (group.character == Character.WARLORD) {
                                        type = DistrictType.MILITARY;
                                    }
                                    else if (group.character == Character.KING) {
                                        type = DistrictType.NOBLE;
                                    }
                                    else if (group.character == Character.MERCHANT) {
                                        type = DistrictType.TRADE;
                                    }
                                    if (type != null) {
                                        for (District d : group.player().city().districts()) {
                                            if (d.districtType() == type) {
                                                group.player().add(1);
                                            }
                                            if (d == District.MAGIC_SCHOOL) {
                                                group.player().add(1);
                                            }
                                        }
                                    }
                                    }
                                else if (actionType1.equals(DESTROY_DISTRICT)) {

                                }
                                    else if (actionType1.equals("Rob")) {
                                    Character character = group.player().controller.selectAmong(List.of(Character.MAGICIAN, Character.KING, Character.BISHOP, Character.MERCHANT, Character.ARCHITECT, Character.WARLORD)
                                            .removeAll(groups.associations.find(Group::isMurdered).map(Group::character)));
                                    groups.associationToCharacter(character).peek(association -> association.stolenBy(group.player()));
                                }
                                actionExecuted(group, actionType1, associations);
                                actionType11 = actionType1;
                                availableActions11 = availableActions11.remove(actionType11);
                            }
                            while (!availableActions11.isEmpty() && !actionType11.equals("End round"));
                        }
                    }
                }
            }
            roundAssociations = associations;
            crown = roundAssociations.find(a -> a.character == Character.KING).map(Group::player).getOrElse(crown);
        } while (!players.map(Player::city).exists(City::isComplete));

        // classe les joueurs par leur score
        // si ex-aequo, le premier est celui qui n'est pas assassiné
        // si pas d'assassiné, le gagnant est le joueur ayant eu le personnage avec le numéro d'ordre le plus petit au dernier tour
        System.out.println("Classement: " + roundAssociations.sortBy(a -> Tuple.of(a.player().score(), !a.isMurdered(), a.character))
                .reverse()
                .map(Group::player));
    }

    public static void actionExecuted(Group association, String actionType, List<Group> associations) {
        System.out.println("Player " + association.player().name() + " executed action " + actionType);
        associations.map(Group::player)
                .forEach(Citadels::displayStatus);
    }

    private static void displayStatus(Player player) {
        System.out.println("  Player " + player.name() + ":");
        System.out.println("    Gold coins: " + player.gold());
        System.out.println("    City: " + textCity(player));
        System.out.println("    Hand size: " + player.cards().size());
        if (player.controller instanceof HumanController) {
            System.out.println("    Hand: " + textHand(player));
        }
        System.out.println();
    }

    private static String textCity(Player player) {
        List<District> districts = player.city().districts();
        return districts.isEmpty() ? "Empty" : districts.map(Citadels::textDistrict).mkString(", ");
    }

    private static String textDistrict(District district) {
        return district.name() + "(" + district.districtType().name() + ", " + district.cost() + ")";
    }

    private static String textHand(Player player) {
        Set<Card> cards = player.cards();
        return cards.isEmpty() ? "Empty" : cards.map(Citadels::textCard).mkString(", ");
    }

    private static String textCard(Card card) {
        return textDistrict(card.district());
    }
}