package org.leanpoker.player;

import com.google.gson.JsonArray;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.leanpoker.player.card.PokerCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Player {

    static final String VERSION = "Rouge Nation 2.5";
    private static List<PokerCard> communityCards = new ArrayList<>();
    private static Gson gson = new GsonBuilder().create();
    private static JsonObject gyuri;
    private static List<PokerCard> holeCards = new ArrayList<>();

    public static int betRequest(JsonElement request) {
        JsonObject jsonObject = request.getAsJsonObject();
        getGyuri(jsonObject);
        getHoleCards();
        getCommunityCards(jsonObject);

        return evaluateCards(jsonObject);
    }

    private static int evaluateCards(JsonObject jsonObject) {
        int buyIn = jsonObject.get("current_buy_in").getAsInt();
        int betToCall = buyIn - gyuri.get("bet").getAsInt();
        int raise = 0;

        Map<String, Integer> cardRanks = new HashMap<>();
        Map<String, Integer> cardSuits = new HashMap<>();

        if (hasBigBoysInHand(cardRanks) && hasPairInHand()) {
            return 1000;
        }

        if (hasBigBoysInHand(cardRanks) && communityCards.size() == 0) {
            raise = 10;
        }

        if (communityCards.size() >= 3) {
            for (PokerCard communityCard : communityCards) {
                cardRanks.merge(communityCard.getRank(), 1, Integer::sum);
                cardSuits.merge(communityCard.getSuit(), 1, Integer::sum);
            }
            for (PokerCard holeCard : holeCards) {
                cardRanks.merge(holeCard.getRank(), 1, Integer::sum);
                cardSuits.merge(holeCard.getSuit(), 1, Integer::sum);
            }
            if (cardSuits.containsValue(5)) {
                return 1000;
            }
            if (cardRanks.containsValue(4)) {
                return 1000;
            } else if (cardRanks.containsValue(3)) {
                raise = 100;
            } else if (cardRanks.containsValue(2)) {
                raise = 50;
            } else {
                return 0;
            }
        }

        if (buyIn > 800) {
            boolean hasTwoPairs = cardRanks.keySet().stream().filter(key -> cardRanks.get(key) > 1).count() > 1;
            if (hasBigBoysInHand(cardRanks) && communityCards.size() == 0) {
                return betToCall;
            }
            if (hasTwoPairs) {
                return betToCall + raise;
            } else {
                return 0;
            }
        } else {
            return betToCall;
        }
    }

    private static boolean hasPairInHand() {
        return holeCards.get(0).getRank().equals(holeCards.get(1).getRank());
    }

    private static boolean hasBigBoysInHand(Map<String, Integer> cardRanks) {
        for (PokerCard holeCard : holeCards) {
            cardRanks.merge(holeCard.getRank(), 1, Integer::sum);
        }

        boolean hasTwoDifferent = cardRanks.keySet().stream()
                .filter(key -> key.matches("[JQKA]"))
                .count() > 1;
        boolean hasSameBiggies = cardRanks.keySet().stream()
                .filter(key -> key.matches("[JQKA]")).anyMatch(key -> cardRanks.get(key) > 1);

        return (hasTwoDifferent || hasSameBiggies);
    }

    private static void getHoleCards() {
        holeCards.clear();
        JsonArray playerCards = gyuri.getAsJsonArray("hole_cards");
        for (int i = 0; i < playerCards.size(); i++) {
            holeCards.add(gson.fromJson(playerCards.get(i), PokerCard.class));
        }
        System.err.println(holeCards);
    }

    private static void getGyuri(JsonObject jsonObject) {
        JsonArray players = jsonObject.get("players").getAsJsonArray();
        List<JsonObject> playersAsString = IntStream.range(0, players.size())
                .mapToObj(index -> (players.get(index)).getAsJsonObject())
                .filter(object -> object.get("name").getAsString().equals("KordaGyuri"))
                .collect(Collectors.toList());
        gyuri = playersAsString.get(0);
        System.err.println(gyuri.get("name").getAsString());
    }

    private static void getCommunityCards(JsonObject jsonObject) {
        communityCards.clear();
        JsonArray communityCards = jsonObject.getAsJsonArray("community_cards");
        for (int i = 0; i < communityCards.size(); i++) {
            Player.communityCards.add(gson.fromJson(communityCards.get(i), PokerCard.class));
        }
        System.err.println(Player.communityCards);
    }

    public static void showdown(JsonElement game) {
    }
}
