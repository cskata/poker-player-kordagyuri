package org.leanpoker.player;

import com.google.gson.JsonArray;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.leanpoker.player.card.PokerCard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Player {

    static final String VERSION = "Fallout 2.8";
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

        PokerCard hand1 = holeCards.get(0);
        PokerCard hand2 = holeCards.get(1);

        if (buyIn > 800) {
            if (hand1.getRank().equals(hand2.getRank()) || hand1.getRank().matches("[JQKA]") && hand2.getRank().matches("[JQKA]")) {
                return buyIn + 10;
            } else {
                return 0;
            }
        } else {
            return buyIn + 1;
        }
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
