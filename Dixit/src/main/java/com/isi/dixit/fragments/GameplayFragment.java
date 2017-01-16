package com.isi.dixit.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.isi.dixit.R;
import com.isi.dixit.adapters.RvCardAdapter;
import com.isi.dixit.adapters.RvScoreAdapter;
import com.isi.dixit.decorators.CardSeparator;
import com.isi.dixit.models.Card;
import com.isi.dixit.models.GameState;
import com.isi.dixit.utilities.DataProvider;

import java.util.ArrayList;

public class GameplayFragment extends Fragment implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();

    public interface Listener {

    }

    public interface CardSelector {
        void onCardSelected(Card card);
    }

    private RvCardAdapter mCardAdapter;
    private RvScoreAdapter mScoreAdapter;
    private GameState mGameState = new GameState();
    private Card mSelectedCard, mZoomedCard;

    public static GameplayFragment getInstance() {
        GameplayFragment instance = new GameplayFragment();
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gameplay, container, false);
        setupUI(rootView);
        setupGame();
        return rootView;
    }

    private void setupUI(View rootView) {
        final FrameLayout mZoomLayout = (FrameLayout) rootView.findViewById(R.id.zoomFl);
        final ImageView mZoomCardIv = (ImageView) rootView.findViewById(R.id.zoomIv);

        final FrameLayout mSelectedLayout = (FrameLayout) rootView.findViewById(R.id.selectedLl);
        final ImageView mSelectedCardIv = (ImageView) rootView.findViewById(R.id.selectedIv);

        mSelectedCardIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Recording. . .");
            }
        });

        ImageButton mSelectBtn = (ImageButton) rootView.findViewById(R.id.selectBtn);
        mSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCardAdapter.removeCard(mZoomedCard);
                if(mSelectedCard != null) {
                    mCardAdapter.addCard(mSelectedCard);
                }
                mSelectedCard = mZoomedCard;

                mZoomLayout.setVisibility(View.GONE);
                mSelectedLayout.setVisibility(View.VISIBLE);
                mSelectedCardIv.setImageResource(getCardResource(mSelectedCard));
            }
        });

        ImageButton mCancelBtn = (ImageButton) rootView.findViewById(R.id.cancelBtn);
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mZoomLayout.setVisibility(View.GONE);
                if(mSelectedCard != null) {
                    mSelectedLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        mCardAdapter = new RvCardAdapter(new ArrayList<Card>(), getActivity(), new GameplayFragment.CardSelector() {
            @Override
            public void onCardSelected(Card card) {
                mZoomedCard = card;
                mSelectedLayout.setVisibility(View.GONE);
                mZoomLayout.setVisibility(View.VISIBLE);
                mZoomCardIv.setImageResource(getCardResource(card));
            }
        });

        RecyclerView mRvCards = (RecyclerView) rootView.findViewById(R.id.cardRv);
        mRvCards.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mRvCards.addItemDecoration(new CardSeparator(20));
        mRvCards.setAdapter(mCardAdapter);

        mScoreAdapter = new RvScoreAdapter(DataProvider.getLeaderboard());
        RecyclerView mRvScore = (RecyclerView) rootView.findViewById(R.id.scoreRv);
        mRvScore.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvScore.setAdapter(mScoreAdapter);
    }

    private int getCardResource(Card card) {
        return getResources().getIdentifier(card.getCardSrcId(), "drawable", getActivity().getPackageName());
    }

    private void setupGame() {
        mGameState.cards = DataProvider.getPlayerHand();
        mCardAdapter.setCards(mGameState.cards);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }
}
