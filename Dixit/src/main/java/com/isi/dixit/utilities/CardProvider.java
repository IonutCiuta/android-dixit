package com.isi.dixit.utilities;


import com.isi.dixit.game.Score;

import java.util.ArrayList;
import java.util.List;

public class CardProvider {
    private static final int HAND_COUNT = 6;
    private static int LAST_CARD_ID = 1;

    public static List<Integer> getPlayerHandIds() {
        List<Integer> handIds = new ArrayList<>();

        for (int i = 0; i < HAND_COUNT; i++) {
            handIds.add(i + LAST_CARD_ID);
        }

        LAST_CARD_ID += HAND_COUNT;
        return handIds;
    }

    public static List<Score> getLeaderboard() {
        List<Score> result = new ArrayList<>(6);
        for(int i = 1; i < 7; i++) {
            result.add(new Score("Player " + i, i * 10));
        }
        return result;
    }
}
