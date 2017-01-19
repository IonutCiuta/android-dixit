package com.isi.dixit.game;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
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
}
