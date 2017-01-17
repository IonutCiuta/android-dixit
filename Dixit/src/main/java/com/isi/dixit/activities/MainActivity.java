package com.isi.dixit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.isi.dixit.R;
import com.isi.dixit.fragments.GameplayFragment;
import com.isi.dixit.fragments.MainFragment;
import com.isi.dixit.game.DixitTurn;

import java.util.ArrayList;

public class MainActivity extends UtilityActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        MainFragment.Listener, GameplayFragment.Listener {
    //Result codes
    private static final int RC_RESOLVE = 5000;
    private static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;

    //Fragmetns
    MainFragment mMainFragment;
    GameplayFragment mGameplayFragment;

    //API components
    private GoogleApiClient mGoogleApiClient;
    private TurnBasedMatch mTurnBasedMatch;

    //Game data
    private DixitTurn mDixitTurn;

    //Flags
    private boolean mResolvingConnectionFailure = false;
    private boolean mSignInClicked = false;
    private boolean isDoingTurn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideStatusBar();

        mMainFragment = MainFragment.getInstance(this);
        mGameplayFragment = GameplayFragment.getInstance(this);

        addFragment(mMainFragment);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        logMsg("onStart");

        //Attempt to reconnect if
        if(mSignInClicked && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        logMsg("onStop");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle connectionDetails) {
        logMsg("onConnected: connection successful!");

        //Update UI to hide SignIn button and show QuickGame and SignOut and greeting
        if(mMainFragment != null) {
            mMainFragment.showSignInButton(false);
            mMainFragment.showGreeting(Games.Players.getCurrentPlayer(mGoogleApiClient));
        }

        //Get turnBasedMatch
        if(connectionDetails != null) {
            mTurnBasedMatch = connectionDetails.getParcelable(Multiplayer.EXTRA_TURN_BASED_MATCH);
            if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
                logErr("onConnected: accessing TurnBasedMatch while not connected");
            }

            updateMatch(mTurnBasedMatch);
        } else {
            logErr("onConnected: no bundle to setup turns");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        logMsg("onConnectionSuspended");
        dialogErr("Warning", "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        logErr("onConnectionFailed: " + connectionResult.getErrorMessage());

        if(mSignInClicked) {
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            if(!BaseGameUtils.resolveConnectionFailure(
                    this,
                    mGoogleApiClient,
                    connectionResult,
                    RC_SIGN_IN,
                    getString(R.string.error_authentication_content))) {
                mResolvingConnectionFailure = false;
            }
        }

        mMainFragment.showSignInButton(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        logMsg("onActivityResult");

        if(requestCode == RC_SIGN_IN) {
            logMsg("onActivityResult: RC_SIGN_IN");
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if(resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                BaseGameUtils.showActivityResultError(
                        this,
                        requestCode,
                        resultCode,
                        R.string.error_authentication_content
                );
            }
        }
    }

    @Override
    public void onSignInClicked() {
        logMsg("onSignInClicked");
        toastMsg("Sign In");
        mSignInClicked = true;
        mGoogleApiClient.connect();
    }

    @Override
    public void onSignOutClicked() {
        logMsg("onSignOutClicked");
        toastMsg("Sign Out");
        if(mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        mMainFragment.showSignInButton(true);
    }

    @Override
    public void onQuickGameClicked() {
        logMsg("onSignInClicked");
        toastMsg("QuickGame");
        quickMatch();
        //switchToFragment(new GameplayFragment());
    }

    @Override
    public void onSubmitClicked() {
        logMsg("onSubmitClicked");
        //mDixitTurn.updateTurnCounter();

        Games.TurnBasedMultiplayer.takeTurn(mGoogleApiClient, mTurnBasedMatch.getMatchId(),
                mDixitTurn.persist(), getNextPlayerId()).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                        processResultOfUpdate(result);
                    }
                });

        mDixitTurn = null;
    }

    //Used as action for onQuickMatch click event
    private void quickMatch() {
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(2, 2, 0);
        TurnBasedMatchConfig config = TurnBasedMatchConfig
                .builder()
                .setAutoMatchCriteria(autoMatchCriteria)
                .build();

        //// TODO: 1/16/2017 spinner maybe

        ResultCallback<TurnBasedMultiplayer.InitiateMatchResult> callback = new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
            @Override
            public void onResult(@NonNull TurnBasedMultiplayer.InitiateMatchResult initiateMatchResult) {
                logMsg("quickMatch: setup callback");
                processResultForInitiationRequest(initiateMatchResult);
            }
        };

        Games.TurnBasedMultiplayer.createMatch(mGoogleApiClient, config).setResultCallback(callback);
    }

    private void processResultForInitiationRequest(TurnBasedMultiplayer.InitiateMatchResult result) {
        TurnBasedMatch match = result.getMatch();
        if (!checkStatusCode(result.getStatus().getStatusCode())) {
            return;
        }

        //check if match already started
        if (match.getData() != null) {
            // This is a game that has already started, so I'll just start
            updateMatch(match);
        } else {
            startMatch(match);
        }
    }

    //setup game state here
    private void startMatch(TurnBasedMatch match) {
        switchToFragment(mGameplayFragment);
        mMainFragment = null;

        mDixitTurn = new DixitTurn();
        mDixitTurn.setMessage("First turn");

        mTurnBasedMatch = match;
        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String participantId = mTurnBasedMatch.getParticipantId(playerId);

        Games.TurnBasedMultiplayer.takeTurn(
                mGoogleApiClient,
                match.getMatchId(),
                mDixitTurn.persist(),
                participantId
        ).setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
            @Override
            public void onResult(@NonNull TurnBasedMultiplayer.UpdateMatchResult result) {
                processResultOfUpdate(result);
            }
        });
    }

    public void processResultOfUpdate(TurnBasedMultiplayer.UpdateMatchResult result) {
        TurnBasedMatch match = result.getMatch();

        if (!checkStatusCode(result.getStatus().getStatusCode())) {
            return;
        }

        // TODO: 1/16/2017 maybe check this
        /*if (match.canRematch()) {
            askForRematch();
        }*/

        isDoingTurn = (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN);

        if (isDoingTurn) {
            updateMatch(match);
            return;
        }

        updateTurnCounter();
    }

    private void updateMatch(TurnBasedMatch match) {
        mTurnBasedMatch = match;
        int status = match.getStatus();
        int turnStatus = match.getTurnStatus();

        switch (status) {
            case TurnBasedMatch.MATCH_STATUS_CANCELED:
                dialogErr("Canceled!", "This game was canceled!");
                return;

            case TurnBasedMatch.MATCH_STATUS_EXPIRED:
                dialogErr("Expired!", "This game is expired. So sad!");
                return;

            case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
                dialogErr(
                        "Waiting for auto-match...",
                        "We're still waiting for an automatch partner."
                );
                return;

            case TurnBasedMatch.MATCH_STATUS_COMPLETE: {
                if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE) {
                    dialogErr(
                            "Complete!",
                            "This game is over; someone finished it, and so did you!"
                    );
                    break;
                }

                dialogErr(
                        "Complete!",
                        "This game is over; someone finished it! You can only finish it now."
                );
            }
        }

        switch (turnStatus) {
            case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:
                mDixitTurn = DixitTurn.unpersist(mTurnBasedMatch.getData());
                mDixitTurn.updateTurnCounter();
                logMsg("MY TURN!");
                toastMsg("MY TURN!");
                return;

            case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
                logMsg("NOT MY TURN!");
                toastMsg("NOT MY TURN");
                break;

            case TurnBasedMatch.MATCH_TURN_STATUS_INVITED:
                logMsg("STILL WAITING");
                toastMsg("STILL WAITING");
                break;
        }

        mDixitTurn = null;
    }

    private String getNextPlayerId() {
        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = mTurnBasedMatch.getParticipantId(playerId);
        ArrayList<String> participantIds = mTurnBasedMatch.getParticipantIds();

        int desiredIndex = -1;

        for (int i = 0; i < participantIds.size(); i++) {
            if (participantIds.get(i).equals(myParticipantId)) {
                desiredIndex = i + 1;
            }
        }

        if(desiredIndex < participantIds.size()) {
            return participantIds.get(desiredIndex);
        }

        if (mTurnBasedMatch.getAvailableAutoMatchSlots() <= 0) {
            // You've run out of automatch slots, so we start over.
            return participantIds.get(0);
        } else {
            // You have not yet fully automatched, so null will find a new
            // person to play against.
            return null;
        }
    }

    private boolean checkStatusCode(int statusCode) {
        switch (statusCode) {
            case GamesStatusCodes.STATUS_OK:
                return true;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_DEFERRED:
                toastMsg("Stored action for later.  (Please remove this toast before release.");
                return true;
            case GamesStatusCodes.STATUS_MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
                showErrorMessage(R.string.status_multiplayer_error_not_trusted_tester);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_ALREADY_REMATCHED:
                showErrorMessage(R.string.match_error_already_rematched);
                break;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_FAILED:
                showErrorMessage(R.string.network_error_operation_failed);
                break;
            case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
                showErrorMessage(R.string.client_reconnect_required);
                break;
            case GamesStatusCodes.STATUS_INTERNAL_ERROR:
                showErrorMessage(R.string.internal_error);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_INACTIVE_MATCH:
                showErrorMessage(R.string.match_error_inactive_match);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_LOCALLY_MODIFIED:
                showErrorMessage(R.string.match_error_locally_modified);
                break;
            default:
                showErrorMessage(R.string.unexpected_status);
                logErr("Did not have warning or string to deal with: " + statusCode);
        }

        return false;
    }

    private void showErrorMessage(int msgResId) {
        BaseGameUtils.makeSimpleDialog(this, getString(msgResId));
    }

    private void setUI() {
        boolean isSignedIn = (mGoogleApiClient != null) && (mGoogleApiClient.isConnected());
        
        if(isSignedIn) {
            // TODO: 1/17/2017 setup this 
            return;
        }
        
        if(isDoingTurn) {
            // TODO: 1/17/2017 it's my turn
            toastMsg("It's your turn");
        } else {
            // TODO: 1/17/2017 it's not my turn
            toastMsg("It's not your turn");
        }
    }

    private void updateTurnCounter() {
        ((TextView)findViewById(R.id.tvTurnCounter)).setText(mDixitTurn != null ? mDixitTurn.getTurnCounter() + "" : "0");
    }
}