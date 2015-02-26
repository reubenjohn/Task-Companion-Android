package com.aspirephile.studytimer.welcome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.aspirephile.shared.debug.Logger;
import com.aspirephile.studytimer.R;
import com.aspirephile.studytimer.StudyTimer;

public class Welcome extends FragmentActivity {
    Logger l = new Logger(Welcome.class);

    WelcomeFragment welcomeF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        bridgeXML();
        initiateFeilds();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fl_welcome, welcomeF).commit();
        }
    }

    private void bridgeXML() {

    }

    private void initiateFeilds() {
        welcomeF = new WelcomeFragment();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        l.d("WelcomeFragment received Activity Result for request code: "
                + requestCode + " with result code: " + resultCode + "(" + data
                + ")");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case StudyTimer.codes.request.sessionSetup:
                    boolean firstRunSuccessResult = data.getExtras().getBoolean(
                            StudyTimer.keys.extras.firstRunCompleted);
                    l.d("First run result received with success: "
                            + firstRunSuccessResult);

                    setResult(Activity.RESULT_OK, data);
                    finish();
                    break;

                default:
                    l.e("Unknown request code received: " + requestCode);
                    break;
            }
        } else {
            l.e("Bad result code received");
            switch (requestCode) {
                case StudyTimer.codes.request.sessionSetup:
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
