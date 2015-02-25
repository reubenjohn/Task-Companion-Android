package com.aspirephile.studytimer.preferences;

public class STSP {

	public static class fileNames {
		public static final String currentSession = "CURRENT_SESSION";
	}

	public static class keys {
		public static final String elapse = "ELAPSE";
		public static final String lapTimeUp = "LAP_TIMEUP";
		public static final String totalElapse = "TOTAL_ELAPSE";
		public static final String running = "RUNNING";
		public static final String stopTime = "STOP_TIME_TIME";
		public static final String targetTime = "TARGET_TIME";
		public static final String totalLaps = "TOTAL_LAPS";
		public static final String firstRun = "FIRST_RUN";
	}

	public static class defaults {

		public static final boolean firstRun = true;
	}

	public static class Preferences {
		public static boolean playTTS;
	}
}
