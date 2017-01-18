package com.isi.dixit.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.example.games.basegameutils.BaseGameUtils;
import com.isi.dixit.R;


public abstract class UtilityActivity extends FragmentActivity{
    protected final String TAG = getClass().getSimpleName();
    protected AlertDialog mAlertDialog;

    protected void hideStatusBar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    protected void addFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
    }

    protected void switchToFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    protected void toastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void dialogErr(String title, String content) {
        BaseGameUtils.makeSimpleDialog(this, title, content);
    }

    protected void logMsg(String msg) {
        Log.i(TAG, msg);
    }

    protected void logErr(String error) {
        Log.e(TAG, error);
    }

    protected void showSpinner() {
        findViewById(R.id.progressLayout).setVisibility(View.VISIBLE);
    }

    protected void dismissSpinner() {
        findViewById(R.id.progressLayout).setVisibility(View.GONE);
    }

    protected void showWarning(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title).setMessage(message);
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close current activity
                    }
                });

        mAlertDialog = alertDialogBuilder.create();
        mAlertDialog.show();
    }
}
