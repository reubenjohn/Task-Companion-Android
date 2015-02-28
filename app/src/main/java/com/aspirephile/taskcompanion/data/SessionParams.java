package com.aspirephile.taskcompanion.data;

import android.os.Bundle;

import com.aspirephile.taskcompanion.TaskCompanion;
import com.aspirephile.taskcompanion.preferences.STSP;

public class SessionParams {
	int totalLaps;
	long lapDuration;

	public SessionParams() {
		totalLaps = TaskCompanion.defaults.totalLaps;
		lapDuration = TaskCompanion.defaults.lapDuration;
	}

	public void setTotalLaps(int totalLaps) {
		if (totalLaps <= TaskCompanion.prefs.minLaps)
			this.totalLaps = TaskCompanion.prefs.minLaps;
		else if (totalLaps >= TaskCompanion.prefs.maxLaps)
			this.totalLaps = TaskCompanion.prefs.maxLaps;
	}

	public void setLapDuration(long lapDuration) {
		if (lapDuration < TaskCompanion.prefs.minLapDuration)
			this.lapDuration = TaskCompanion.prefs.minLapDuration;
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
