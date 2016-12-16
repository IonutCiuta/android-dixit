package com.isi.dixit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.isi.dixit.adapters.RvCardAdapter;
import com.isi.dixit.adapters.RvScoreAdapter;
import com.isi.dixit.decorators.CardSeparator;
import com.isi.dixit.models.Card;
import com.isi.dixit.models.GameState;
import com.isi.dixit.utilities.DataProvider;

import java.util.ArrayList;


public class GameActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = this.getClass().getSimpleName();
    private RvCardAdapter mCardAdapter;
    private RvScoreAdapter mScoreAdapter;
    private GameState mGameState = new GameState();
    private Card mSelectedCard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setupUI();
        setupGame();
    }

    private void setupUI() {
        final FrameLayout mZoomLayout = (FrameLayout) findViewById(R.id.zoomFl);
        final ImageView mZoomCardIv = (ImageView) findViewById(R.id.zoomIv);

        final FrameLayout mSelectedLayout = (FrameLayout) findViewById(R.id.selectedLl);
        final ImageView mSelectedCardIv = (ImageView) findViewById(R.id.selectedIv);

        mSelectedCardIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Recording. . .");
            }
        });

        ImageButton mSelectBtn = (ImageButton) findViewById(R.id.selectBtn);
        mSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mZoomLayout.setVisibility(View.GONE);
                mSelectedLayout.setVisibility(View.VISIBLE);
                mSelectedCardIv.setImageResource(getCardResource(mSelectedCard));
            }
        });

        ImageButton mCancelBtn = (ImageButton) findViewById(R.id.cancelBtn);
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mZoomLayout.setVisibility(View.GONE);
            }
        });

        mCardAdapter = new RvCardAdapter(new ArrayList<Card>(), this, new CardSelector() {
            @Override
            public void onCardSelected(Card card) {
                mSelectedCard = card;
                mSelectedLayout.setVisibility(View.GONE);
                mZoomLayout.setVisibility(View.VISIBLE);
                mZoomCardIv.setImageResource(getCardResource(card));
            }
        });

        RecyclerView mRvCards = (RecyclerView) findViewById(R.id.cardRv);
        mRvCards.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRvCards.addItemDecoration(new CardSeparator(20));
        mRvCards.setAdapter(mCardAdapter);

        mScoreAdapter = new RvScoreAdapter(DataProvider.getLeaderboard());
        RecyclerView mRvScore = (RecyclerView) findViewById(R.id.scoreRv);
        mRvScore.setLayoutManager(new LinearLayoutManager(this));
        mRvScore.setAdapter(mScoreAdapter);
    }

    private int getCardResource(Card card) {
        return getResources().getIdentifier(card.getCardSrcId(), "drawable", getPackageName());
    }

    private void setupGame() {
        mGameState.cards = DataProvider.getPlayerHand();
        mCardAdapter.setCards(mGameState.cards);
    }

    @Override
    public void onClick(View view) {

    }

    public interface CardSelector {
        void onCardSelected(Card card);
    }
}
