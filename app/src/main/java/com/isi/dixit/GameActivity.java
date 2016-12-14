package com.isi.dixit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.isi.dixit.adapters.RvCardAdapter;
import com.isi.dixit.decorators.CardSeparator;
import com.isi.dixit.models.Card;
import com.isi.dixit.models.GameState;
import com.isi.dixit.utilities.CardProvider;

import java.util.ArrayList;


public class GameActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = this.getClass().getSimpleName();
    private RvCardAdapter mCardAdapter;
    private GameState mGameState = new GameState();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
        Intent intent = getIntent();
        //String message = intent.getStringExtra("message");
        //name.setText("my text");

        setupUI();
        setupGame();
        Log.d(TAG, "onCreate() Restoring previous state");

    }

    public void setupUI() {
        mCardAdapter = new RvCardAdapter(new ArrayList<Card>(), this, new CardSelector() {
            @Override
            public void onCardSelected(Card card) {
                Log.i(TAG, "Card: " + card.getCardId());
            }
        });

        RecyclerView mRvCards = (RecyclerView) findViewById(R.id.cardRv);
        mRvCards.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRvCards.setAdapter(mCardAdapter);
        mRvCards.addItemDecoration(new CardSeparator(20));
    }

    public void setupGame() {
        mGameState.cards = CardProvider.getPlayerHand();
        mCardAdapter.setCards(mGameState.cards);
    }

    @Override
    public void onClick(View view) {

    }

    public interface CardSelector {
        void onCardSelected(Card card);
    }
}
