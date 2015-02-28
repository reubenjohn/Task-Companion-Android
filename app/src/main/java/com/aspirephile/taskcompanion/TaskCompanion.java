package com.aspirephile.taskcompanion;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.aspirephile.shared.debug.Logger;
import com.aspirephile.shared.debug.NullPointerAsserter;
import com.aspirephile.shared.timming.Time;
import com.aspirephile.shared.timming.Timer;
import com.aspirephile.shared.timming.frametimer.FrameIntervalListener;
import com.aspirephile.shared.timming.frametimer.FrameIntervalListenerContainer;
import com.aspirephile.shared.timming.frametimer.FrameTimer;
import com.aspirephile.shared.timming.frametimer.FrameTimerListener;
import com.aspirephile.taskcompanion.preferences.STSP;
import com.aspirephile.taskcompanion.sound.SoundManager;

@SuppressWarnings("UnusedDeclaration")
public class TaskCompanion implements FrameTimerListener, OnClickListener {
    private static Logger l = new Logger(TaskCompanion.class);
    private NullPointerAsserter asserter = new NullPointerAsserter(l);

    public static final boolean debugMode = false;
    public FrameTimer framer;
    public TimerElementsFragment timerElements;
    Timer runtime;
    LapsFragment lapsF;
    EmptyLapFragment emptyLap;
    FillerFragment filler;
    FragmentManager fragM;
    SoundManager soundManager;
    private SharedPreferences sessionPrefs;
    private Mode mode;

    TaskCompanion(Handler thisHandler, FragmentManager fragM,
                  SharedPreferences sessionPrefs) {
        framer = new FrameTimer(thisHandler);
        bridgeFragments(fragM);
        this.sessionPrefs = sessionPrefs;
        initializeFields();
        setListeners(framer);
        runtime.start();
    }

    protected void bridgeFragments(FragmentManager fragM) {
        this.fragM = fragM;
        timerElements = (TimerElementsFragment) fragM
                .findFragmentById(R.id.home_timer_elements);
        lapsF = (LapsFragment) fragM.findFragmentById(R.id.home_laps);
        emptyLap = (EmptyLapFragment) fragM
                .findFragmentById(R.id.home_empty_laps);
        filler = (FillerFragment) fragM
                .findFragmentById(R.id.filler_background);
        l.bridgeXML(asserter.assertPointer(timerElements, lapsF, emptyLap, filler));
    }

    protected void initializeFields() {
        timerElements.initializeLapCount(lapsF.getLapCount());
        timerElements.setAverage(lapsF.getAverage());
        Time.setDefaultFormat("%MM:%SS.%s");
        framer.setInterval(100);
        runtime = new Timer();
        runtime.setFormat("%MM:%SS.%sss");
        setNoLapMode(lapsF.hasNoLaps());
        soundManager = new SoundManager();

    }

    public void startNewSession(SessionInfo sessionInfo) {
        if (sessionInfo == null) {
            resetSession();
        }
    }

    public void start() {
        timerElements.start();
        if (SoundManager.beepManager.isBeepsEnabled()) {
            SoundManager.postAllLapProgressSounds(timerElements.getElapse(),
                    timerElements.getTargetTime());
        }
    }

    public void stop() {
        SoundManager.removeAllLapProgressSounds();
        timerElements.stop();
    }

    public void toggle() {
        if (isRunning()) {
            stop();
        } else {
            start();
        }
    }

    public boolean lap() {
        l.d("TaskCompanion lap called");
        if (asserter.assertPointer(lapsF)) {
            lapsF.addLap(timerElements.getElapse());
            setNoLapMode(lapsF.hasNoLaps());
            if (lapsF.getLapCount() == lapsF.getTotalLapCount()) {
                return true;
            }
        }
        timerElements.setAverage(lapsF.getAverage());
        timerElements.lap(lapsF.getLapCount());
        if (isRunning()) {
            if (SoundManager.beepManager.isBeepsEnabled()) {
                SoundManager.removeAllLapProgressSounds();
                SoundManager.postAllLapProgressSounds(
                        timerElements.getElapse(),
                        timerElements.getTargetTime());
            }
        }
        return false;
    }

    public void resetSession() {
        l.d("reset() called");
        stop();
        timerElements.reset();
        lapsF.resetSession();
        setNoLapMode(true);
    }

    public boolean isRunning() {
        return timerElements.isRunning();
    }

    protected void resetRuntime() {
        resetSession();
        runtime.reset();
    }

    public void onPause() {
        l.d("onPause() called");
        framer.stop();
        runtime.stop();
        saveSessionToPrefs();
    }

    public void onResume(boolean resumeFromPreviousState) {
        l.d("onResume() with resumeFromPreviousState "
                + resumeFromPreviousState);
        if (resumeFromPreviousState)
            loadSessionFromBundle(getSessionBundleFromPrefs());
        framer.start();
        runtime.start();
        soundManager.onResume();
    }

    public void onStop() {
        if (SoundManager.beepManager.isBeepsEnabled())
            SoundManager.removeAllLapProgressSounds();
    }

    public void logStatus() {
        l.i(getStatus());
    }

    public String getStatus() {
        return "Status: Runtime[" + runtime.getFormattedTime() + "] Frame["
                + framer.getFrame() + "] " + getTimerElementsStatus() + " "
                + getLapsFStatus();
    }

    public String getTimerElementsStatus() {
        if (asserter.assertPointerQuietly(timerElements))
            return "Basic timer elements[OK]";
        else
            return "Basic timer elements[BAD]";
    }

    public String getDBFStatus() {
        if (asserter.assertPointerQuietly(lapsF))
            return "Lap elements[OK]: Laps[" + lapsF.getLapCount() + "]";
        else
            return "Lap elements[BAD]";
    }

    public String getLapsFStatus() {
        if (asserter.assertPointerQuietly(lapsF))
            return "Lap elements[OK]: Laps[" + lapsF.getLapCount() + "]";
        else
            return "Lap elements[BAD]";
    }

    public void putSessionInfo(Bundle sessionInfo) {
        timerElements.putSessionInfo(sessionInfo);
        lapsF.putSessionInfo(sessionInfo);
    }

    protected void setListeners(FrameTimer framer) {
        timerElements.addFrameTimerListenersTo(framer);
        framer.addFrameTimerListener(this);
        timerElements.setTimerElementsListener(lapsF);
    }

    public void setStatusLogging(boolean status) {
        if (status) {
            if (!logging.status) {
                logging.status = true;
                if (asserter.assertPointer(logging.listenerContainer)) {
                    logging.listenerContainer = new FrameIntervalListenerContainer(new FrameIntervalListener() {
                        @Override
                        public void OnFrameReached() {
                            logStatus();
                        }
                    }, logging.loggingInterval);
                }
                framer.addFrameIntervalListenerContainer(logging.listenerContainer);
            }
        } else {
            if (logging.status) {
                if (asserter.assertPointer(logging.listenerContainer))
                    logging.status = false;
                framer.removeFrameIntervalListenerContainer(logging.listenerContainer);
            }
        }
    }

    public void setTargetTime(long timeInMilliseconds) {
        l.d("Target time set: " + timeInMilliseconds);
        timerElements.setTargetTime(timeInMilliseconds);
        if (isRunning()) {
            SoundManager.removeAllLapProgressSounds();
            SoundManager.postAllLapProgressSounds(timerElements.getElapse(),
                    timerElements.getTargetTime());
        }
    }

    public float getLapProgress() {
        if (asserter.assertPointer(timerElements))
            return timerElements.getLapProgress();
        else
            return -1;
    }

    @Override
    public void onNewFrame() {
        filler.updateFillerProgress(timerElements.getLapProgress());
        /*
         * float lapProgress=timerElements.getLapProgress(); if(lapProgress!=-1)
		 * lapsF.updateCurrentLapProgress(lapProgress);
		 */
    }

    @Override
    public void onEndFrame() {

    }

    @Override
    public void onReset() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_toggle:
                toggle();
                break;
            case R.id.b_lap:
                lap();
                break;

        }
    }

    public void setNoLapMode(boolean noLaps) /**/{
        FragmentTransaction transaction = fragM.beginTransaction();
        if (noLaps) {
            transaction.hide(lapsF);
            transaction.show(emptyLap);
            View v = emptyLap.getView();
            assert v != null;
            v.invalidate();
        } else {
            transaction.hide(emptyLap);
            transaction.show(lapsF);
            View v = lapsF.getView();
            assert v != null;
            v.invalidate();
        }
        transaction.commit();
    }

    public void setMode(Mode mode) {
        switch (mode) {
            case NORMAL:
            case SESSION_EDIT:
                l.d("Setting mode to: " + mode);
                timerElements.setMode(mode);
                break;
            default:
                l.d("Unknown TimerElement mode request received: "
                        + mode);
        }
    }

    public void createNewSessionFromBundle(Bundle sessionInfo) {
        saveSessionToPrefsFromBundle(sessionInfo);
        l.i("Creating new session:");
        resetSession();
        lapsF.createNewSession(sessionInfo);
        timerElements.createNewSession(sessionInfo);
        l.i("Session created");
    }

    public void putSessionInfoToBundle(Bundle sessionInfo) {
        timerElements.putSessionInfo(sessionInfo);
        lapsF.putSessionInfo(sessionInfo);
        l.i("Session info put into bundle");
    }

    public void saveSessionToPrefsFromBundle(Bundle sessionInfo) {
        SharedPreferences.Editor editor = sessionPrefs.edit();
        editor.putLong(STSP.keys.targetTime,
                sessionInfo.getLong(STSP.keys.targetTime, defaults.lapDuration));
        editor.putInt(STSP.keys.totalLaps,
                sessionInfo.getInt(STSP.keys.totalLaps, defaults.totalLaps));
        editor.putLong(STSP.keys.elapse,
                sessionInfo.getLong(STSP.keys.elapse, defaults.elapse));
        editor.putLong(STSP.keys.totalElapse, sessionInfo.getLong(
                STSP.keys.totalElapse, defaults.totalElapse));
        editor.putLong(
                STSP.keys.stopTime,
                sessionInfo.getLong(STSP.keys.stopTime,
                        System.currentTimeMillis()));
        editor.putBoolean(STSP.keys.lapTimeUp,
                sessionInfo.getBoolean(STSP.keys.lapTimeUp, false));
        editor.commit();
        l.i("Session saved to shared session preferences from "
                + sessionInfo.toString());
    }

    public void saveSessionToPrefs() {
        Bundle sessionInfo = new Bundle();
        timerElements.putSessionInfo(sessionInfo);
        lapsF.putSessionInfo(sessionInfo);
        l.i(
                "Session bundled and about to be parceled to save session to preferences from bundle");
        saveSessionToPrefsFromBundle(sessionInfo);
    }

    public Bundle getSessionBundleFromPrefs() {
        Bundle sessionInfo = new Bundle();
        sessionInfo.putLong(STSP.keys.targetTime, sessionPrefs.getLong(
                STSP.keys.targetTime, defaults.lapDuration));
        sessionInfo.putInt(STSP.keys.totalLaps,
                sessionPrefs.getInt(STSP.keys.totalLaps, defaults.totalLaps));
        sessionInfo.putLong(STSP.keys.elapse,
                sessionPrefs.getLong(STSP.keys.elapse, defaults.elapse));
        sessionInfo.putLong(STSP.keys.totalElapse, sessionPrefs.getLong(
                STSP.keys.totalElapse, defaults.totalElapse));
        sessionInfo.putLong(
                STSP.keys.stopTime,
                sessionPrefs.getLong(STSP.keys.stopTime,
                        System.currentTimeMillis()));
        sessionInfo.putBoolean(STSP.keys.lapTimeUp,
                sessionPrefs.getBoolean(STSP.keys.lapTimeUp, false));
        l.i("Session loaded from shared session preferences to "
                + sessionInfo.toString());
        return sessionInfo;
    }

    public void loadSessionFromBundle(Bundle sessionInfo) {
        timerElements.loadSessionFromBundle(sessionInfo);
        lapsF.loadSessionFromBundle(sessionInfo);
        l.i("Session loaded from bundle");
    }

    public enum Mode {
        NORMAL, SESSION_EDIT
    }

    public static class defaults {
        public static long lapDuration = 5000;
        public static int totalLaps = 20;
        public static long elapse = 0;
        public static long totalElapse = 0;

        public static long getSessionDuration() {
            return lapDuration * totalLaps;
        }

        public static String getConcatenatedDefaults() {
            return "{ " + "lapDuration:" + lapDuration + "," + " totalLaps"
                    + ":" + totalLaps + "," + " sounds.lapProgress" + ":"
                    + sounds.lapProgress + " }";
        }

        public static void loadFromResources(Resources resources) {
            lapDuration = resources.getInteger(R.integer.target_time);
            totalLaps = resources.getInteger(R.integer.total_laps);
            sounds.lapProgress = resources
                    .getBoolean(R.bool.lap_progress_sounds);
            l.d("Loaded defaults from rescources->"
                    + getConcatenatedDefaults());
        }

        public static class Speech {

            public static final int LapIncludedSpeech = 30000;
        }

        public static class sounds {
            public static boolean lapProgress = true;
        }
    }

    public static class prefs {
        public static final long minLapDuration = 1000;
        public static final int totalLapsLongPressIterationDelay = 100;
        public static int minLaps = 5;
        public static int maxLaps = 1000;
    }

    public static class props {

        public static final int splashScreenDuration = 500;
    }

    public static class files {

        public static final String appPrefs = "appPrefs";

        public static String sessionInfo = "sessionInfo";
    }

    public static class keys {
        public static class prefs {

            public static final String firstRun = "firstRun";

        }

        public static class settings {
            public static final String shake_to_lap = "key_shake_to_lap";

            public static class sounds {
                public static final String lap_progress = "key_sounds_lap_progress_switch";
            }
        }

        public static class extras {
            public static final String session_complete_proceedings = "session_complete_proceedings";
            public static final String first_run = "first_run";
            public static final String firstRunCompleted = null;
        }
    }

    public static class codes {
        public static class request {
            public static final int welcome = 2045;
            public static final int home = 4153;
            public static final int sessionSetup = 5764;
        }

    }

    private static class logging {
        static boolean status = false;
        static int loggingInterval = 300;
        static FrameIntervalListenerContainer listenerContainer = null;
    }

    public class SessionInfo {
        public long totalElapse;
        public long currentElapse;
        public int laps;
    }
}
