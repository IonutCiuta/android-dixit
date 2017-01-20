package com.isi.dixit.game;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DixitTurn implements Serializable {
    public String leadingPlayerId;
    public String currentPlayer;
    public int turnCounter = 0;
    public int describedCard;
    public String cardDescription;
    public boolean selectionState;
    public boolean votingState;
    public List<String> playerIds = new ArrayList<>();
    public List<SelectedCard> selectedCards = new ArrayList<>();
    public List<CardVote> votes = new ArrayList<>();
    public List<Hand> hands = new ArrayList<>();
    public List<Score> leaderboard = new ArrayList<>();
}
