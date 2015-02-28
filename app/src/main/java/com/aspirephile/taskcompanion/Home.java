package com.aspirephile.taskcompanion;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.aspirephile.shared.debug.Logger;
import com.aspirephile.shared.debug.NullPointerAsserter;
import com.aspirephile.shared.senses.OnShakeListener;
import com.aspirephile.shared.senses.ShakeSense;
import com.aspirephile.shared.timming.Time;
import com.aspirephile.taskcompanion.preferences.STSP;
import com.aspirephile.taskcompanion.session.SessionComplete;
import com.aspirephile.taskcompanion.session.setup.SessionSetup;
import com.aspirephile.taskcompanion.util.SystemUiHider;
import com.aspirephile.taskcompanion.welcome.Welcome;

public class Home extends ActionBarActivity implements OnClickListener,
        OnCheckedChangeListener, OnShakeListener {
    private Logger l = new Logger(Home.class);
    private NullPointerAsserter asserter = new NullPointerAsserter(l);
    private SystemUiHider mSystemUiHider;

    ToggleButton toggle;
    Button lap;
    View controlsView, contentView;
    TaskCompanion T;
    Handler tHandler = new Handler();
    Boolean isLargeLayoutBoolean = false;
    FrameLayout lapsContainer;
    LapsContainerParams lapsContainerParams;
    TimePickerDialog targetDialog;
    SharedPreferences sessionPrefs;
    ShakeSense lapShakeSense;
    boolean resetScheduledForOnResume;
    FullScreenStatus fullScreenStatus;
    Runnable goFullScreen = new Runnable() {

        @Override
        public void run() {
            toggleFullScreen(true);
            fullScreenStatus = FullScreenStatus.FULLSCREEN;
        }

    };
    ActionMode sessionEditActionMode;
    private ActionMode.Callback sessionEditActionModeCallBack = new ActionMode.Callback() {

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            sessionEditActionMode = null;
            T.setMode(TaskCompanion.Mode.NORMAL);
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_session_edit, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem menu) {
            switch (menu.getItemId()) {
                case R.id.mi_edit_target_time:
                    Time time = new Time(T.timerElements.getTargetTime());
                    targetDialog.updateTime(time.getMinutes(), time.getSeconds());
                    targetDialog.show();
                    break;
            }
            return false;
        }
    };
    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };
    Handler goFullSCreenHandler = new Handler();
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (Preferences.AUTO_HIDE) {
                delayedHide(Preferences.AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home);

        handleWelcomes();
        bridgeXML();
        setListeners();
        initializeFields();

    }

    private void handleWelcomes() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        if (preferences.getBoolean(STSP.keys.firstRun, STSP.defaults.firstRun)) {
            startActivity(new Intent(Home.this, Welcome.class));
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(STSP.keys.firstRun, TaskCompanion.debugMode);
            editor.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        l.d("Home created");
        if (Preferences.AUTO_HIDE)
            delayedHide(Preferences.AUTO_HIDE_DELAY_MILLIS);
    }

    @Override
    protected void onStop() {
        T.onStop();
        lapShakeSense.onStop();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserSettings();
        if (resetScheduledForOnResume) {
            resetSession();
        }
        T.onResume(!resetScheduledForOnResume);
        T.soundManager.initialize(getApplicationContext(), T);
        lapShakeSense.onResume();
        resetScheduledForOnResume = false;

    }

    @Override
    protected void onPause() {
        T.onPause();
        super.onPause();
    }

    public void resetSession() {
        // TODO transition the lapsContainer layout change during reset
        T.resetSession();
        toggle.setChecked(false);
        lapsContainer.setLayoutParams(lapsContainerParams
                .getLayoutParams(false));
    }

    public void confirmReset() {
        final boolean wasRunning = T.isRunning();
        T.stop();
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        builder.setTitle(R.string.session_reset_title)
                .setMessage(R.string.session_reset_message)
                .setIcon(R.drawable.ic_action_replay)
                .setPositiveButton(R.string.reset,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                resetSession();

                            }
                        })
                .setNegativeButton(R.string.session_reset_negative,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                if (wasRunning)
                                    T.start();
                            }
                        })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface arg0) {
                        if (wasRunning)
                            T.start();
                    }
                }).show();
        // TODO disable the positive button for 2 seconds
        // TODO add warning sound in case it is pressed in the pocket
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    protected void bridgeXML() {
        controlsView = findViewById(R.id.fullscreen_content_controls);
        contentView = findViewById(R.id.fullscreen_content);
        toggle = (ToggleButton) findViewById(R.id.b_toggle);
        lap = (Button) findViewById(R.id.b_lap);
        lapsContainer = (FrameLayout) findViewById(R.id.home_laps_container);
    }

    protected void setListeners() {
        toggle.setOnCheckedChangeListener(this);
        lap.setOnClickListener(this);
        findViewById(R.id.b_toggle).setOnTouchListener(mDelayHideTouchListener);
        contentView.setOnClickListener(this);
    }

    protected void initializeFields() {
        setupSystemUIHider();

        sessionPrefs = getSharedPreferences(TaskCompanion.files.sessionInfo, Context.MODE_PRIVATE);
        TaskCompanion.defaults.loadFromResources(getResources());
        T = new TaskCompanion(tHandler, getSupportFragmentManager(),
                getSharedPreferences("session", Context.MODE_PRIVATE));
        T.setStatusLogging(true);
        isLargeLayoutBoolean = getResources().getBoolean(R.bool.large_layout);

        lapsContainerParams = new LapsContainerParams();
        lapsContainer.setLayoutParams(lapsContainerParams
                .getLayoutParams(!T.lapsF.hasNoLaps()));

        Time currentTarget = new Time(TaskCompanion.defaults.lapDuration);
        targetDialog = new TimePickerDialog(Home.this, new OnTimeSetListener() {
            short callCount = 0;

            @Override
            public void onTimeSet(TimePicker view, int minute, int second) {
                if (callCount % 2 == 1) {
                    T.setTargetTime(Time.getTimeInMilliseconds(0, 0, minute,
                            second, 0));
                }
                callCount++;
            }
        }, currentTarget.getMinutes(), currentTarget.getSeconds(), true);
        targetDialog.setTitle(R.string.target_dialog_title);
        targetDialog.setMessage(getResources().getString(
                R.string.target_dialog_summary));

        fullScreenStatus = FullScreenStatus.NOT_FULLSCREEN;

        lapShakeSense = new ShakeSense();
        lapShakeSense
                .initialize((SensorManager) getSystemService(Context.SENSOR_SERVICE));
        lapShakeSense.setOnShakeListener(this);
    }

    protected void setupSystemUIHider() {

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView,
                Preferences.HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Preferences.HIDE_BUTONS) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                                if (mControlsHeight == 0) {
                                    mControlsHeight = controlsView.getHeight();
                                }
                                if (mShortAnimTime == 0) {
                                    mShortAnimTime = getResources()
                                            .getInteger(
                                                    android.R.integer.config_shortAnimTime);
                                }
                                controlsView
                                        .animate()
                                        .translationY(
                                                visible ? 0 : mControlsHeight)
                                        .setDuration(mShortAnimTime);
                            } else {
                                controlsView
                                        .setVisibility(visible ? View.VISIBLE
                                                : View.GONE);
                            }

                            if (visible && Preferences.AUTO_HIDE) {
                                delayedHide(Preferences.AUTO_HIDE_DELAY_MILLIS);
                            }
                        }

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_lap:
                lap();
                break;
            case R.id.fullscreen_content:
                mSystemUiHider.show();
                break;

        }
    }

    private void lap() {
        // TODO transition the lapsCountainer layout change during lap
        lapsContainer
                .setLayoutParams(lapsContainerParams.getLayoutParams(true));
        if (T.lap())
            startActivityForResult(
                    new Intent(Home.this, SessionComplete.class),
                    codes.completeSession);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean newState) {
        if (newState)
            T.start();
        else
            T.stop();
        softToggleFullScreen(newState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.mi_preferences:
                i = new Intent("com.aspirephile.taskcompanion.PREFERENCES");
                startActivityForResult(i, 0);
                break;
            case R.id.mi_new_session:
                startNewSession();
                break;
            case R.id.mi_reset:
                confirmReset();
                break;
            case R.id.mi_edit_session:
                if (sessionEditActionMode == null)
                    sessionEditActionMode = Home.this
                            .startSupportActionMode(sessionEditActionModeCallBack);
                T.setMode(TaskCompanion.Mode.SESSION_EDIT);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        l.i(
                "Result received with requestResult:" + requestCode
                        + " resultCode:" + resultCode + " and data->"
                        + Boolean.toString(asserter.assertPointerQuietly(data)));
        if (resultCode == RESULT_OK && asserter.assertPointer(data)) {
            switch (requestCode) {
                case codes.createSession:
                    Bundle sessionInfo = data.getExtras();
                    if (asserter.assertPointer(sessionInfo)) {
                        T.createNewSessionFromBundle(sessionInfo);
                    } else l.e("Could not create session! No extras found!");
                    break;
                case codes.completeSession:
                    switch (data.getIntExtra(
                            TaskCompanion.keys.extras.session_complete_proceedings,
                            R.id.b_repeat_session)) {
                        case R.id.b_repeat_session:
                            resetScheduledForOnResume = true;
                            break;
                        case R.id.b_new_session:
                            startNewSession();
                    }

            }
        } else
            l.e("Home Activity result received result code: " + resultCode + " and intent data: " + data);
    }

    public void startNewSession() {
        if (T.isRunning()) {
            toggle.setChecked(false);
            l.d("Toggle button set to false");
            T.stop();
        }
        l.d("launching CREATE_SESSION");
        Intent i = new Intent(Home.this, SessionSetup.class);
        startActivityForResult(i, codes.createSession);
        // showSessionDialog(isLargeLayoutBoolean);
    }

    /*
     * private void showSessionDialog(boolean windowed) { AlertDialog.Builder
     * builder = new AlertDialog.Builder(Home.this); Time currentTarget = new
     * Time(T.timerElements.getTargetTime()); final TimePickerDialog timePicker
     * = new TimePickerDialog(Home.this, new OnTimeSetListener() { short
     * callCount = 0;
     *
     * @Override public void onTimeSet(TimePicker view, int minute, int second)
     * { if (callCount % 2 == 1) { T.setTargetTime(Time.getTimeInMilliseconds(0,
     * 0, minute, second, 0)); } callCount++; } }, currentTarget.getMinutes(),
     * currentTarget.getSeconds(), true);
     * builder.setTitle(R.string.title_session_setup)
     * .setPositiveButton(R.string.session_edit_positive, new
     * DialogInterface.OnClickListener() {
     *
     * @Override public void onClick(DialogInterface dialog, int which) {
     *
     * } }) .setNegativeButton(R.string.session_edit_negative, null)
     * .setItems(R.array.session_setup_elements, new
     * DialogInterface.OnClickListener() {
     *
     * @Override public void onClick(DialogInterface dialog, int which) { switch
     * (which) { case 0: l.d("Set target time dialog");
     * timePicker.show(); break; case 1:
     *
     * break; case 2: break; } } }).show();
     *
     * }
     */
    private void toggleFullScreen(boolean requestFullScreen) {
        if (requestFullScreen) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    private void softToggleFullScreen(boolean requestFullScreen) {
        switch (fullScreenStatus) {
            case FULLSCREEN:
                toggleFullScreen(false);
                fullScreenStatus = FullScreenStatus.NOT_FULLSCREEN;
                break;
            case PENDING_FULLSCREEN:
                goFullSCreenHandler.removeCallbacks(goFullScreen);
                fullScreenStatus = FullScreenStatus.NOT_FULLSCREEN;
                break;
            case NOT_FULLSCREEN:
                if (requestFullScreen) {
                    goFullSCreenHandler.postDelayed(goFullScreen,
                            Preferences.AUTO_HIDE_DELAY_MILLIS);
                    fullScreenStatus = FullScreenStatus.PENDING_FULLSCREEN;
                }
                break;
        }
    }

    @Override
    public void onShaken() {
        lap();
    }

    public void loadUserSettings() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        Bundle settings = new Bundle();

        settings.putBoolean(TaskCompanion.keys.settings.sounds.lap_progress,
                preferences.getBoolean(
                        TaskCompanion.keys.settings.sounds.lap_progress,
                        TaskCompanion.defaults.sounds.lapProgress));
    }

    public enum FullScreenStatus {
        FULLSCREEN, PENDING_FULLSCREEN, NOT_FULLSCREEN
    }

    private static class Preferences {

        public static final int AUTO_HIDE_DELAY_MILLIS = 3000;
        /**
         * The flags to pass to {@link SystemUiHider#getInstance}.
         */
        public static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
        public static boolean AUTO_HIDE = false;
        public static boolean HIDE_BUTONS = false;
    }

    private static class codes {
        public static final int createSession = 1241;
        public static final int completeSession = 9862;
    }

    private class LapsContainerParams {
        LapsLayout lapsLayout;
        boolean cached_isLandscape;

        public LapsContainerParams() {
            lapsLayout = new LapsLayout();
        }

        public LayoutParams getLayoutParams(boolean hasLaps) {
            l.d("sending lapsLayoutParames with landscape: "
                    + lapsContainerParams.cached_isLandscape + " and hasLaps: "
                    + hasLaps);
            if (hasLaps) {
                return lapsLayout.HasLapsLayout;
            } else
                return lapsLayout.NoLapsLayout;
        }

        private class LapsLayout {
            public LayoutParams HasLapsLayout, NoLapsLayout;

            public LapsLayout() {
                cached_isLandscape = Home.this.getResources().getBoolean(
                        R.bool.landscape);
                // TODO find a better way to determine screen orientation
                if (cached_isLandscape) {
                    HasLapsLayout = new LayoutParams(0,
                            LayoutParams.WRAP_CONTENT, 9.5f);
                    NoLapsLayout = new LayoutParams(LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT);
                } else {
                    HasLapsLayout = new LayoutParams(LayoutParams.MATCH_PARENT,
                            0, 12);
                    HasLapsLayout.leftMargin = 16;
                    HasLapsLayout.rightMargin = 16;

                    NoLapsLayout = new LayoutParams(LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT);
                    NoLapsLayout.leftMargin = 16;
                    NoLapsLayout.rightMargin = 16;
                }
            }
        }

    }


}
