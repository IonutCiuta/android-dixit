package com.isi.dixit.models;


import java.io.Serializable;

public class Score implements Serializable{
    private String player;
    private int points;

    public Score() {
    }

    public Score(String player, int points) {
        this.player = player;
        this.points = points;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
