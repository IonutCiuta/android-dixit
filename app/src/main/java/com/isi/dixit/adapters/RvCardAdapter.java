package com.isi.dixit.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.isi.dixit.GameActivity;
import com.isi.dixit.R;
import com.isi.dixit.models.Card;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RvCardAdapter extends RecyclerView.Adapter<RvCardAdapter.RvCardVH> {
    private List<Card> mCards;
    private Context mContext;
    private GameActivity.CardSelector mSelector;

    public RvCardAdapter(@NonNull List<Card> cards,
                         @NonNull Context context,
                         @NonNull GameActivity.CardSelector selector) {
        this.mCards = cards;
        this.mContext = context;
        this.mSelector = selector;
    }

    @Override
    public RvCardVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RvCardVH(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.item_card, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(RvCardVH holder, int position) {
        holder.configure(mCards.get(position), mSelector, mContext);
    }

    @Override
    public int getItemCount() {
        return mCards.size();
    }

    public void setCards(List<Card> cards) {
        mCards = cards;
        notifyDataSetChanged();
    }

    static class RvCardVH extends RecyclerView.ViewHolder {
        private ImageView mCardIv;

        RvCardVH(View itemView) {
            super(itemView);
            mCardIv = (ImageView) itemView.findViewById(R.id.cardIv);
        }

        void configure(final Card card, final GameActivity.CardSelector selector, Context context) {
            //Picasso.with(context).load(card.getCardSrcId()).into(mCardIv);
            Picasso.with(context).load(R.drawable.card).into(mCardIv);
            mCardIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selector.onCardSelected(card);
                }
            });
        }
    }
}
