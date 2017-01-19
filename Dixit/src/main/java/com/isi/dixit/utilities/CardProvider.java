package com.isi.dixit.utilities;


import com.isi.dixit.models.Card;
import com.isi.dixit.models.Score;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
