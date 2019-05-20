package org.leanpoker.player;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public class Player {

    static final String VERSION = "Ghost Protocol 2.0";

    public static int betRequest(JsonElement request) {
        JsonObject jsonObject = request.getAsJsonObject();
        int buyIn = jsonObject.get("current_buy_in").getAsInt();
        return buyIn + 1;
    }

    public static void showdown(JsonElement game) {
    }
}
