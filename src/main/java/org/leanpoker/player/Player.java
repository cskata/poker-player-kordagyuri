package org.leanpoker.player;

import com.google.gson.JsonElement;

import java.util.Map;

public class Player {

    static final String VERSION = "Ghost Protocol";

    public static int betRequest(JsonElement request) {
        return 2;
    }

    public static void showdown(JsonElement game) {
    }
}
