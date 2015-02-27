package com.aspirephile.taskcompanion.data;

import android.os.Bundle;

import com.aspirephile.taskcompanion.StudyTimer;
import com.aspirephile.taskcompanion.preferences.STSP;

public class SessionParams {
	int totalLaps;
	long lapDuration;

	public SessionParams() {
		totalLaps = StudyTimer.defaults.totalLaps;
		lapDuration = StudyTimer.defaults.lapDuration;
	}

	public void setTotalLaps(int totalLaps) {
		if (totalLaps <= StudyTimer.prefs.minLaps)
			this.totalLaps = StudyTimer.prefs.minLaps;
		else if (totalLaps >= StudyTimer.prefs.maxLaps)
			this.totalLaps = StudyTimer.prefs.maxLaps;
	}

	public void setLapDuration(long lapDuration) {
		if (lapDuration < StudyTimer.prefs.minLapDuration)
			this.lapDuration = StudyTimer.prefs.minLapDuration;
		else
			this.lapDuration = lapDuration;
	}

	public Bundle getBundledSessionParams() {
		Bundle sessionInfo = new Bundle();
		sessionInfo.putInt(STSP.keys.totalLaps, totalLaps);
		sessionInfo.putLong(STSP.keys.targetTime, lapDuration);
		return sessionInfo;
	}

	public int getTotalLaps() {
		return totalLaps;
	}

	public long getLapDuration() {
		return lapDuration;
	}
}
