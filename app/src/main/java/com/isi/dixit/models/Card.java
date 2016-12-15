package com.isi.dixit.models;


import java.io.Serializable;

public class Card implements Serializable {
    String cardSrcId;
    Integer cardId;

    public Card() {
    }

    public Card(String cardSrcId, Integer cardId) {
        this.cardSrcId = cardSrcId;
        this.cardId = cardId;
    }

    public String getCardSrcId() {
        return cardSrcId;
    }

    public void setCardSrcId(String cardSrcId) {
        this.cardSrcId = cardSrcId;
    }

    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }
}
