package com.isi.dixit.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.games.Player;
import com.isi.dixit.R;


public class MainFragment extends Fragment implements View.OnClickListener {

    public interface Listener {
        //redirect UI input from fragment elements to the Activity
        void onSignInClicked();
        void onSignOutClicked();
        void onQuickGameClicked();
    }

    private Listener mListener;
    private boolean mShowSignIn;
    private TextView mGreeting;

    public static MainFragment getInstance(Listener listener) {
        MainFragment instance = new MainFragment();
        instance.mListener = listener;
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        int[] CLICKABLES = {R.id.btn_sign_in, R.id.btn_sign_out, R.id.btn_quick_game};

        for(int id : CLICKABLES)
            rootView.findViewById(id).setOnClickListener(this);

        mGreeting = (TextView) rootView.findViewById(R.id.tv_greeting);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUi();
    }

    @Override
    public void onClick(View view) {
        //intercept UI input and call corresponding listener method to trigger an action in activity
        switch (view.getId()) {
            case R.id.btn_sign_in:
                mListener.onSignInClicked();
                break;

            case R.id.btn_sign_out:
                mListener.onSignOutClicked();
                break;

            case R.id.btn_quick_game:
                mListener.onQuickGameClicked();
                break;
        }
    }

    private void updateUi() {
        if(getActivity() == null) return;
        getActivity()
                .findViewById(R.id.fl_sign_in)
                .setVisibility(mShowSignIn ? View.VISIBLE : View.GONE);
        getActivity()
                .findViewById(R.id.fl_quick_game)
                .setVisibility(mShowSignIn ? View.GONE : View.VISIBLE);
    }

    public void showSignInButton(boolean show) {
        mShowSignIn = show;
        updateUi();
    }

    public void showGreeting(Player player) {
        mGreeting.setText(player == null ?
                "???" : String.format(getString(R.string.msg_greeting), player.getDisplayName()));
    }
}
