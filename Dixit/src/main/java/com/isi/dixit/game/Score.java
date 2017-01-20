package com.isi.dixit.game;


import java.io.Serializable;

public class Score implements Serializable{
    public String player;
    public int points;
    public String name;

    public Score() {
    }

    public Score(String player, int points) {
        this.player = player;
        this.points = points;
    }
}
