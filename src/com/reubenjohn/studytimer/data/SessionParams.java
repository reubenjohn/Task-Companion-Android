package com.reubenjohn.studytimer.data;

import android.os.Bundle;

import com.reubenjohn.studytimer.StudyTimer;
import com.reubenjohn.studytimer.preferences.STSP;

public class SessionParams {
	int totalLaps;
	long lapDuration;

	public SessionParams() {
		totalLaps = StudyTimer.defaults.totalLaps;
		lapDuration = StudyTimer.defaults.lapDuration;
	}

	public void setTotalLaps(int totalLaps) {
		this.totalLaps = totalLaps;
	}

	public void setLapDuration(long lapDuration) {
		this.lapDuration = lapDuration;
	}

	public Bundle getBundledSessionParams() {
		Bundle sessionInfo = new Bundle();
		sessionInfo.putInt(STSP.keys.totalLaps, totalLaps);
		sessionInfo.putLong(STSP.keys.targetTime, lapDuration);
		return sessionInfo;
	}
}
