package org.leanpoker.player;

import com.google.gson.JsonArray;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.leanpoker.player.card.PokerCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Player {

    static final String VERSION = "Ghost Protocol 2.7.34";

    public static int betRequest(JsonElement request) {
        JsonObject jsonObject = request.getAsJsonObject();
        int buyIn = jsonObject.get("current_buy_in").getAsInt();

        List<PokerCard> community = getCommunityCards(jsonObject);
        JsonObject gyuri = getGyuri(jsonObject);

        return buyIn + 1;
    }

    private static JsonObject getGyuri(JsonObject jsonObject) {
        JsonArray players = jsonObject.get("players").getAsJsonArray();

        List<JsonObject> playersAsString = IntStream.range(0, players.size())
                .mapToObj(index -> (players.get(index)).getAsJsonObject())
                .filter(object -> object.get("name").getAsString().equals("KordaGyuri"))
                .collect(Collectors.toList());

        JsonObject currentPlayer = playersAsString.get(0);

        System.err.println(currentPlayer.get("name").getAsString());
        return currentPlayer;
    }

    private static List<PokerCard> getCommunityCards(JsonObject jsonObject) {
        List<PokerCard> community = new ArrayList<>();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        JsonArray communityCards = jsonObject.getAsJsonArray("community_cards");

        for (int i = 0; i < communityCards.size(); i++) {
            community.add(gson.fromJson(communityCards.get(i), PokerCard.class));
        }

        System.err.println(community.toString());
        return community;
    }

    public static void showdown(JsonElement game) {
    }
}
