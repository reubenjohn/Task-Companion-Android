package com.aspirephile.studytimer.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.aspirephile.shared.debug.Logger;
import com.aspirephile.shared.ui.FullScreenManager;
import com.aspirephile.studytimer.R;
import com.aspirephile.studytimer.StudyTimer;

public class Splash extends ActionBarActivity {
    Logger l = new Logger(Splash.class);
    FullScreenManager fullScreenManager;
    private SplashFragment splashF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initializeFields();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.splash_container, splashF).commit();
        }
    }

    private void initializeFields() {
        splashF = new SplashFragment();
        fullScreenManager = new FullScreenManager(Splash.this);
        fullScreenManager.allowContentBehindStatusBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        l.d("Activity Result for: " + requestCode
                + " received with result code: " + resultCode + "(" + data
                + ")");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case StudyTimer.codes.request.welcome:
                    boolean firstRunSuccessResult = data.getExtras().getBoolean(
                            StudyTimer.keys.extras.firstRunCompleted);
                    l.d("First run result received with success: "
                            + firstRunSuccessResult);
                    splashF.sendFirstRunCompletionResult(firstRunSuccessResult);
                    break;

                default:
                    l.e("Unknown request code received: " + requestCode);
                    break;
            }
        } else {
            l.e("Bad result code received");
            switch (requestCode) {
                case StudyTimer.codes.request.welcome:
                    l.d("Welcome activity result received with bad result code, thus finishing Splash activity");
                    finish();
                    break;

                default:
                    l.e("Unknown request code received: " + requestCode);
                    break;
            }

        }
    }
}
