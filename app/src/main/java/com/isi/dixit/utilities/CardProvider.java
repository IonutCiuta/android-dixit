package com.isi.dixit.utilities;


import com.isi.dixit.models.Card;

import java.util.ArrayList;
import java.util.List;

public class CardProvider {
    public static List<Card> getPlayerHand() {
        List<Card> result = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            result.add(new Card(i, i));
        }
        return result;
    }
}
