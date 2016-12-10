package com.isi.dixit.models;


import java.io.Serializable;

public class Card implements Serializable {
    Integer cardSrcId;
    Integer cardId;

    public Card() {
    }

    public Card(Integer cardSrcId, Integer cardId) {
        this.cardSrcId = cardSrcId;
        this.cardId = cardId;
    }

    public Integer getCardSrcId() {
        return cardSrcId;
    }

    public void setCardSrcId(Integer cardSrcId) {
        this.cardSrcId = cardSrcId;
    }

    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }
}
