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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.games.Games;
import com.isi.dixit.R;
import com.isi.dixit.activities.MainActivity;
import com.isi.dixit.adapters.RvCardAdapter;
import com.isi.dixit.adapters.RvScoreAdapter;
import com.isi.dixit.decorators.CardSeparator;
import com.isi.dixit.game.CardVote;
import com.isi.dixit.game.DixitTurn;
import com.isi.dixit.game.Hand;
import com.isi.dixit.game.SelectedCard;
import com.isi.dixit.models.Card;
import com.isi.dixit.models.GameState;
import com.isi.dixit.utilities.CardProvider;

import java.util.ArrayList;
import java.util.List;

public class GameplayFragment extends Fragment implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();

    public interface Listener {
        void onSubmitClicked();
        void onStartVoteClicked();
        void onPlayCardClicked();
        void onVoteCardClicked();
    }

    public interface CardSelector {
        void onCardSelected(Card card);
    }

    private Listener mListener;
    private DixitTurn mTurnData;
    private List<Card> mHandCards;
    private List<Card> mCandidatesCards;

    private RvCardAdapter mCardAdapter;
    private RvScoreAdapter mScoreAdapter;
    private Button mSubmitBtn;

    private Card mSelectedCard, mZoomedCard;
    private String mCardDescription = "Something like a bird";

    public static GameplayFragment getInstance(Listener listener, DixitTurn turn) {
        GameplayFragment instance = new GameplayFragment();
        instance.mListener = listener;
        instance.mTurnData = turn;
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gameplay, container, false);
        setupUI(rootView);
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

        mSubmitBtn = (Button) rootView.findViewById(R.id.submitBtn);
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTurnData.describedCard = mSelectedCard.getCardId();
                mTurnData.cardDescription = mCardDescription;
                mListener.onSubmitClicked();
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
                mSubmitBtn.setVisibility(View.VISIBLE);
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

        mScoreAdapter = new RvScoreAdapter(CardProvider.getLeaderboard());
        RecyclerView mRvScore = (RecyclerView) rootView.findViewById(R.id.scoreRv);
        mRvScore.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvScore.setAdapter(mScoreAdapter);

        updateUI();
    }

    private int getCardResource(Card card) {
        return getResources().getIdentifier(card.getCardSrcId(), "drawable", getActivity().getPackageName());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }

    public void updateTurn(DixitTurn mTurnData) {
        this.mTurnData = mTurnData;
        updateUI();
    }

    private void updateUI() {
        final String myId = mTurnData.currentPlayer;
        initializeHand(myId);

        if (mTurnData.leadingPlayerId.equals(myId)) {
            logMsg("I'M THE LEADER");
            if (!mTurnData.selectionState && !mTurnData.votingState) {
                logMsg("I MUST CHOOSE CARD");
                mTurnData.selectionState = true;
                mTurnData.votingState = false;

                mCardAdapter.setCards(mHandCards);

                mSubmitBtn.setText("PLAY CARD");
                mSubmitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mTurnData.describedCard = mSelectedCard.getCardId();
                        mTurnData.cardDescription = mCardDescription;
                        mListener.onSubmitClicked();
                    }
                });
                return;
            }

            if(mTurnData.selectionState) {
                logMsg("EVERYBODY HAS SELECTED CARD. THEY MUST VOTE.");
                mTurnData.selectionState = false;
                mTurnData.votingState = true;

                initializeCandidates();
                mCardAdapter.setCards(mCandidatesCards);

                mSubmitBtn.setText("START VOTE");
                mSubmitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onStartVoteClicked();
                    }
                });
                return;
            }

            if(mTurnData.votingState) {
                logMsg("EVERYBODY HAS VOTED. I MUST DECIDE WINNER.");
                return;
            }
        } else {
            logMsg("I'M JUST SOME PLAYER");
            if (mTurnData.selectionState) {
                logMsg("I MUST SELECT CARD");
                mCardAdapter.setCards(mCandidatesCards);

                mSubmitBtn.setText("SELECT CARD");
                mSubmitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SelectedCard selectedCard = new SelectedCard();
                        selectedCard.card = mSelectedCard.getCardId();
                        selectedCard.playerId = myId;

                        mTurnData.selectedCards.add(selectedCard);
                        mListener.onPlayCardClicked();
                    }
                });
                return;
            }

            if (mTurnData.votingState) {
                logMsg("I MUST VOTE CARD");
                initializeCandidates();
                mCardAdapter.setCards(mCandidatesCards);

                mSubmitBtn.setText("VOTE");
                mSubmitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CardVote cardVote = new CardVote();
                        cardVote.card = mSelectedCard.getCardId();
                        cardVote.playerId = myId;

                        mTurnData.votes.add(cardVote);
                        mListener.onVoteCardClicked();
                    }
                });
                return;
            }
        }
    }

    private void initializeHand(String myId) {
        if(mHandCards == null) {
            Hand myHand = null;
            for(Hand hand : mTurnData.hands) {
                if(hand.playerId.equals(myId)) {
                    myHand = hand;
                    break;
                }
            }

            if(myHand == null) {
                logErr("ERROR: NO HAND FOUND");
                throw new RuntimeException("ERROR: NO HAND FOUND");
            }

            mHandCards = new ArrayList<>();
            for(Integer cardId : myHand.cards) {
                mHandCards.add(new Card("c" + cardId, cardId));
            }
        }
    }

    private void initializeCandidates() {
        mCandidatesCards = new ArrayList<>();
        for(SelectedCard selectedCard : mTurnData.selectedCards) {
            mHandCards.add(new Card("c" + selectedCard.card, selectedCard.card));
        }
    }

    protected void logMsg(String msg) {
        Log.i(TAG, msg);
    }

    protected void logErr(String error) {
        Log.e(TAG, error);
    }

    protected void toastMsg(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

}
