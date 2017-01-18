package com.isi.dixit.game;


import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

public class DixitState {
    public static boolean leadingPlayer = false;
    public static int currentCard;

    public static byte[] persistState(DixitTurn dixitTurn) {
        return new Gson().toJson(dixitTurn).getBytes();
    }

    public static DixitTurn unpersistState(byte[] state) {
        if(state == null) {
            return new DixitTurn();
        }

        String stringState = null;
        try {
            stringState = new String(state, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return new Gson().fromJson(stringState, DixitTurn.class);
    }
}
