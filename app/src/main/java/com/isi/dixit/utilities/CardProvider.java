package com.isi.dixit.utilities;


import com.isi.dixit.models.Card;
import com.isi.dixit.models.Score;

import java.util.ArrayList;
import java.util.List;

public class CardProvider {
    private static final int CARD_COUNT = 108;

    public static List<Card> getPlayerHand() {
        List<Card> result = new ArrayList<>();
        for(int i = 1; i <= CARD_COUNT; i++) {
            result.add(new Card("c" + i, i));
        }
        return result;
    }

    public static List<Score> getLeaderboard() {
        List<Score> result = new ArrayList<>(6);
        for(int i = 1; i < 7; i++) {
            result.add(new Score("Player " + i, i * 10));
        }
        return result;
    }
}
