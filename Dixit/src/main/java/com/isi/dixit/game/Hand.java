package com.isi.dixit.game;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Hand implements Serializable {
    public String playerId;
    public List<Integer> cards = new ArrayList<>();
}
