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

    static final String VERSION = "Fallout 2.7";

    public static int betRequest(JsonElement request) {
        JsonObject jsonObject = request.getAsJsonObject();
        int buyIn = jsonObject.get("current_buy_in").getAsInt();

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        JsonArray communityCards = jsonObject.getAsJsonArray("community_cards");
        List<PokerCard> community = new ArrayList<>();

        for(int i=0; i<communityCards.size(); i++){
            community.add(gson.fromJson(communityCards.get(i), PokerCard.class));
        }

        System.err.println(community);


        JsonArray players = jsonObject.get("players").getAsJsonArray();

        List<JsonObject> playersAsString = IntStream.range(0, players.size())
                .mapToObj(index -> (players.get(index)).getAsJsonObject())
                .filter(object -> object.get("name").getAsString().equals("KordaGyuri"))
                .collect(Collectors.toList());

        JsonObject currentPlayer = playersAsString.get(0);

        JsonArray playerCards = currentPlayer.getAsJsonArray("hole_cards");
        List<PokerCard> holeCards = new ArrayList<>();

        for(int i=0; i<playerCards.size(); i++){
            holeCards.add(gson.fromJson(playerCards.get(i), PokerCard.class));
        }

        System.err.println(currentPlayer.get("name").getAsString());

        System.err.println(holeCards);

        PokerCard hand1 = holeCards.get(0);
        PokerCard hand2 =holeCards.get(1);

        if(buyIn > 800) {
            if (hand1.getRank().equals(hand2.getRank()) || hand1.getRank().matches("[JQKA]") && hand2.getRank().matches("[JQKA]")) {
                return buyIn + 10;
            } else {
                return 0;
            }
        } else {
            return buyIn + 1;
        }
    }

    public static void showdown(JsonElement game) {
    }
}
