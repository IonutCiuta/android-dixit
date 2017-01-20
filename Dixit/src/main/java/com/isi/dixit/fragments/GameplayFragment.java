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
import android.widget.TextView;

import com.isi.dixit.R;
import com.isi.dixit.adapters.RvCardAdapter;
import com.isi.dixit.adapters.RvScoreAdapter;
import com.isi.dixit.decorators.CardSeparator;
import com.isi.dixit.game.CardVote;
import com.isi.dixit.game.DixitTurn;
import com.isi.dixit.game.Hand;
import com.isi.dixit.game.Score;
import com.isi.dixit.game.SelectedCard;
import com.isi.dixit.game.Card;
import com.isi.dixit.utilities.CardProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameplayFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();

    public interface Listener {
        void onSubmitClicked();
        void onStartVoteClicked();
        void onPlayCardClicked();
        void onVoteCardClicked();
        void onGameOver();
    }

    public interface CardSelector {
        void onCardSelected(Card card);
    }

    private Listener mListener;
    private DixitTurn mTurnData;
    private List<Card> mHandCards;
    private List<Card> mCandidatesCards;

    private TextView mDescriptionTv;
    private FrameLayout mSelectedLayout;
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
        mSelectedLayout =  (FrameLayout) rootView.findViewById(R.id.selectedLl);
        final ImageView mZoomCardIv = (ImageView) rootView.findViewById(R.id.zoomIv);
        final ImageView mSelectedCardIv = (ImageView) rootView.findViewById(R.id.selectedIv);
        mDescriptionTv = (TextView) rootView.findViewById(R.id.tvDescription);

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

    public void updateTurn(DixitTurn mTurnData) {
        this.mTurnData = mTurnData;
        updateUI();
    }

    private void updateUI() {
        final String myId = mTurnData.currentPlayer;
        initializeHand(myId);
        initializeCandidates();
        initializeLeaderboard();
        mSelectedCard = null;
        mSelectedLayout.setVisibility(View.GONE);
        showDescription(false);

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
                logMsg("EVERYBODY HAS VOTED. I MUST UPDATE SCORE AND SELECT ANOTHER CARD.");
                //init hand
                //repurpose button
                //score
                //refreshGameData
                calculateScore();
                checkIfGameOver();


                mSelectedCard = null;
                mCardDescription = "Dragons";
                mTurnData.votes = new ArrayList<>();
                mTurnData.selectedCards = new ArrayList<>();
                mTurnData.describedCard = 0;
                mTurnData.cardDescription = mCardDescription;
                mTurnData.votingState = false;
                mTurnData.selectionState = true;

                mCardAdapter.setCards(mHandCards);
                mSubmitBtn.setText("Play card");
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
        } else {
            logMsg("I'M JUST SOME PLAYER");
            if (mTurnData.selectionState) {
                logMsg("I MUST SELECT CARD");
                showDescription(true);
                mCardAdapter.setCards(mHandCards);

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
                showDescription(true);
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

    private void initializeLeaderboard() {
        mScoreAdapter.setScores(mTurnData.leaderboard);
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
            mCandidatesCards.add(new Card("c" + selectedCard.card, selectedCard.card));
        }
        mCandidatesCards.add(new Card("c" + mTurnData.describedCard, mTurnData.describedCard));
    }

    private void showDescription(boolean yes) {
        if(yes) {
            mDescriptionTv.setVisibility(View.VISIBLE);
            mDescriptionTv.setText("\"" + mTurnData.cardDescription + "\"");
        } else {
            mDescriptionTv.setVisibility(View.GONE);
        }
    }

    private void calculateScore() {
        int votesForCorrectCard = 0;
        HashMap<Integer, Integer> otherCardVotes = new HashMap<>();

        for(CardVote vote : mTurnData.votes) {
            if(vote.card.equals(mTurnData.describedCard)) {
                votesForCorrectCard++;
            } else {
                if(otherCardVotes.containsKey(vote.card)) {
                    otherCardVotes.put(vote.card, otherCardVotes.get(vote.card) + 1);
                } else {
                    otherCardVotes.put(vote.card, 1);
                }
            }
        }

        if(votesForCorrectCard == 0 || votesForCorrectCard == mTurnData.votes.size() - 1) {
            for(Score score : mTurnData.leaderboard) {
                if(!score.player.equals(mTurnData.leadingPlayerId)) {
                    score.points += 2;
                }
            }
            return;
        }

        mScoreAdapter.setScores(mTurnData.leaderboard);
    }

    private void checkIfGameOver() {
        logMsg("CHECK IF GAME OVER");
        for(Score score : mTurnData.leaderboard) {
            if(score.points == 2) {
                logMsg("WINNER FOUND");
                mTurnData.winnerId = score.player;
                mTurnData.winnerName = score.name;
                mListener.onGameOver();
                return;
            }
        }
        logMsg("NO WINNER FOUND");
    }

    protected void logMsg(String msg) {
        Log.i(TAG, msg);
    }

    protected void logErr(String error) {
        Log.e(TAG, error);
    }
}
