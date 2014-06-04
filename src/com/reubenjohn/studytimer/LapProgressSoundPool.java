package com.reubenjohn.studytimer;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class LapProgressSoundPool {

	private Context context;
	Beep beep;
	CutOff cutOff;
	WakeLock wakeLock;

	static class sounds {
		private static int beep = 0, cutOff = 0;
	}

	private SoundPool soundPool;

	public LapProgressSoundPool(Context context, WakeLock wakeLock) {
		this.context = context;
		initializeAudio();
		beep = new Beep();
		cutOff = new CutOff();
		this.wakeLock = wakeLock;
	}

	protected void initializeAudio() {
		soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		sounds.beep = soundPool.load(context, R.raw.beep, 1);
		sounds.cutOff = soundPool.load(context, R.raw.detonator_interface, 1);
	}

	public class Beep implements Runnable {
		@Override
		public void run() {
			Log.d("StudyTimer", "Playing beep");
			soundPool.play(sounds.beep, 1, 1, 5, 0, 1);
		}

	}

	public class CutOff implements Runnable {
		@Override
		public void run() {
			Log.d("StudyTimer", "Playing cut off");
			soundPool.play(sounds.cutOff, 1, 1, 5, 0, 1);
			if (wakeLock.isHeld()) {
				wakeLock.release();
				Log.d("StudyTimer", "WakeLock released");
			}
		}

	}
}
