package com.isi.dixit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.isi.dixit.R;
import com.isi.dixit.fragments.GameplayFragment;
import com.isi.dixit.fragments.MainFragment;

public class MainActivity extends UtilityActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        MainFragment.Listener {
    //Result codes
    private static final int RC_RESOLVE = 5000;
    private static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;

    //Fragmetns
    MainFragment mMainFragment;
    GameplayFragment mGameplayFragment;

    //API components
    private GoogleApiClient mGoogleApiClient;

    //Flags
    private boolean mResolvingConnectionFailure = false;
    private boolean mSignInClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideStatusBar();

        mMainFragment = MainFragment.getInstance(this);
        mGameplayFragment = GameplayFragment.getInstance();

        addFragment(mMainFragment);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .build();
    }

    @Override
    protected void onStop() {
        super.onStop();
        logMsg("onStop");
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        logMsg("onActivityResult");

        if(requestCode == RC_SIGN_IN) {
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
    public void onConnected(@Nullable Bundle bundle) {
        logMsg("onConnected");
        mMainFragment.showSignInButton(false);

        Player p = Games.Players.getCurrentPlayer(mGoogleApiClient);
        String displayName;
        if (p == null) {
            logErr("Player name is null!");
            displayName = "???";
        } else {
            displayName = p.getDisplayName();
        }
        ((TextView)findViewById(R.id.tv_greeting)).setText(
                String.format(
                        getString(R.string.msg_greeting),
                        displayName
                )
        );
    }

    @Override
    public void onConnectionSuspended(int i) {
        logMsg("onConnectionSuspended");
        mGoogleApiClient.connect();
    }

    @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        logErr("onConnectionFailed: " + connectionResult.getErrorMessage());

        if(mSignInClicked) {
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            if (!BaseGameUtils.resolveConnectionFailure(
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
        switchToFragment(new GameplayFragment());
    }
}