package com.isi.dixit.utilities;


import com.isi.dixit.models.Card;
import com.isi.dixit.models.Score;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class DataProvider {
    private static final int CARD_COUNT = 108;
    private static final int HAND_COUNT = 6;

    public static List<Card> getAllCards() {
        List<Card> result = new ArrayList<>();
        for(int i = 1; i <= CARD_COUNT; i++) {
            result.add(new Card("c" + i, i));
        }
        return result;
    }

    public static List<Card> getPlayerHand() {
        List<Card> cards = getAllCards();
        List<Card> hand = new ArrayList<>();
        Set<Integer> selectedCards = new HashSet<>();
        Random random = new Random();

        for(int i = 0; i < HAND_COUNT; i++) {
            int cardIndex = random.nextInt(CARD_COUNT + 1);
            while(selectedCards.contains(cardIndex)) {
                cardIndex = random.nextInt(CARD_COUNT + 1);
            }
            selectedCards.add(cardIndex);
            hand.add(cards.get(cardIndex));
        }

        return hand;
    }

    public static List<Score> getLeaderboard() {
        List<Score> result = new ArrayList<>(6);
        for(int i = 1; i < 7; i++) {
            result.add(new Score("Player " + i, i * 10));
        }
        return result;
    }
}
