package com.aspirephile.studytimer;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.aspirephile.studytimer.R;
import com.aspirephile.studytimer.preferences.STSP;
import com.aspirephile.studytimer.sound.SoundManager;
import com.aspirephile.studytimer.timming.Time;
import com.aspirephile.studytimer.timming.Timer;
import com.aspirephile.studytimer.timming.frametimer.FrameIntervalListener;
import com.aspirephile.studytimer.timming.frametimer.FrameTimer;
import com.aspirephile.studytimer.timming.frametimer.FrameTimerListener;

public class StudyTimer implements FrameTimerListener, OnClickListener {

	// TODO consider using enum instead
	public static final class MODES {

		public static final int NORMAL = 0;
		public static final int SESSION_EDIT = 1;

	}

	public static final boolean debugMode = false;
	public FrameTimer framer;
	Timer runtime;
	public TimerElementsFragment timerElements;
	LapsFragment lapsF;
	EmptyLapFragment emptyLap;
	FillerFragment filler;
	SharedPreferences.OnSharedPreferenceChangeListener prefsListener;
	FragmentManager fragM;
	private SharedPreferences sessionPrefs;
	SoundManager soundManager;

	public static class defaults {
		public static long lapDuration = 5000;
		public static int totalLaps = 20;
		public static long elapse = 0;
		public static long totalElapse = 0;

		public static class Speech {

			public static final int LapIncludedSpeech = 30000;
		}

		public static class sounds {
			public static boolean lapProgress = true;
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
			Log.d("StudyTimer", "Loaded defaults from rescources->"
					+ getConcatenatedDefaults());
		}
	}

	public static class prefs {
		public static final long minLapDuration = 1000;
		public static int minLaps = 5;
		public static int maxLaps = 1000;
	}

	public static class keys {
		public static class settings {
			public static class sounds {
				public static final String lap_progress = "key_sounds_lap_progress_switch";
			}

			public static final String shake_to_lap = "key_shake_to_lap";
		}

		public static class extras {
			public static final String session_complete_proceedings = "session_complete_proceedings";
			public static final String first_run = "first_run";
		}
	}

	public static class codes {
		public static class request {
			public static int welcome = 2045;
		}

	}

	private static class logging {
		static boolean status = false;
		static int loggingInterval = 300;
		static FrameIntervalListener listener = null;
	}

	public class SessionInfo {
		public long totalElapse;
		public long currentElapse;
		public int laps;
	}

	StudyTimer(Handler thisHandler, FragmentManager fragM,
			SharedPreferences sessionPrefs) {
		framer = new FrameTimer(thisHandler);
		bridgeFragments(fragM);
		this.sessionPrefs = sessionPrefs;
		initializeFeilds();
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
		assert timerElements != null;
		assert lapsF != null;
		assert emptyLap != null;
		assert filler != null;
	}

	protected void initializeFeilds() {
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
		Log.d("StudyTimer", "StudyTimer lap called");
		assert lapsF != null;
		if (lapsF != null) {
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
		Log.d("StudyTimer", "reset() called");
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
		Log.d("StudyTimer", "onPause() called");
		framer.stop();
		runtime.stop();
		saveSessionToPrefs();
	}

	public void onResume(boolean resumeFromPreviousState) {
		Log.d("StudyTimer", "onResume() with resumeFromPreviousState "
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
		Log.i("StudyTimer", getStatus());
	}

	public String getStatus() {
		return "Status: Runtime[" + runtime.getFormattedTime() + "] Frame["
				+ framer.getFrame() + "] " + getTimerElementsStatus() + " "
				+ getLapsFStatus();
	}

	public String getTimerElementsStatus() {
		if (timerElements != null)
			return "Basic timer elements[OK]";
		else
			return "Basic timer elements[BAD]";
	}

	public String getDBFStatus() {
		if (lapsF != null)
			return "Lap elements[OK]: Laps[" + lapsF.getLapCount() + "]";
		else
			return "Lap elements[BAD]";
	}

	public String getLapsFStatus() {
		if (lapsF != null)
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
				logging.listener = new FrameIntervalListener() {
					@Override
					public void OnFrameReached() {
						logStatus();
					}
				};
				framer.addFrameReachListener(logging.listener,
						logging.loggingInterval);
			}
		} else {
			if (logging.status) {
				if (logging.listener != null)
					logging.status = false;
				framer.removeFrameIntervalListenerContainer(logging.listener);
			}
		}
	}

	public void setTargetTime(long timeInMilliseconds) {
		Log.d("StudyTimer", "Target time set: " + timeInMilliseconds);
		timerElements.setTargetTime(timeInMilliseconds);
		if (isRunning()) {
			SoundManager.removeAllLapProgressSounds();
			SoundManager.postAllLapProgressSounds(timerElements.getElapse(),
					timerElements.getTargetTime());
		}
	}

	public float getLapProgress() {
		assert timerElements != null;
		if (timerElements != null)
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

	public void setNoLapMode(boolean noLaps) {
		FragmentTransaction transaction = fragM.beginTransaction();
		if (noLaps) {
			transaction.hide(lapsF);
			transaction.show(emptyLap);
			emptyLap.getView().invalidate();
		} else {
			transaction.hide(emptyLap);
			transaction.show(lapsF);
			lapsF.getView().invalidate();
		}
		transaction.commit();
	}

	public void setMode(int MODE) {
		switch (MODE) {
		case MODES.NORMAL:
		case MODES.SESSION_EDIT:
			Log.d("StudyTimer", "Setting mode to: " + MODE);
			timerElements.setMode(MODE);
			break;
		default:
			Log.d("StudyTimer", "Unknown TimerElement mode request received: "
					+ MODE);
		}
	}

	public void createNewSessionFromBundle(Bundle sessionInfo) {
		saveSessionToPrefsFromBundle(sessionInfo);
		Log.i("Sessions", "Creating new session:");
		resetSession();
		lapsF.createNewSession(sessionInfo);
		timerElements.createNewSession(sessionInfo);
		Log.i("Sessions", "Session created");
	}

	public void putSessionInfoToBundle(Bundle sessionInfo) {
		timerElements.putSessionInfo(sessionInfo);
		lapsF.putSessionInfo(sessionInfo);
		Log.i("Sessions", "Session info put into bundle");
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
		Log.i("Sessions", "Session saved to shared session preferences from "
				+ sessionInfo.toString());
	}

	public void saveSessionToPrefs() {
		Bundle sessionInfo = new Bundle();
		timerElements.putSessionInfo(sessionInfo);
		lapsF.putSessionInfo(sessionInfo);
		Log.i("Sessions",
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
		Log.i("Sessions", "Session loaded from shared session preferences to "
				+ sessionInfo.toString());
		return sessionInfo;
	}

	public void loadSessionFromBundle(Bundle sessionInfo) {
		timerElements.loadSessionFromBundle(sessionInfo);
		lapsF.loadSessionFromBundle(sessionInfo);
		Log.i("Sessions", "Session loaded from bundle");
	}
}
