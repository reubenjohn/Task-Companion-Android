package com.aspirephile.shared.senses;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class ShakeSense {

	private SensorManager mSensorManager;
	private OnShakeListener listener = new OnShakeListener() {

		@Override
		public void onShaken() {
			Log.d("StudyTimer", "Device shaken");

		}
	};

	private float mAccel;
	private float mAccelCurrent;
	private float mAccelLast;
	long startOfJerk = 0;
	long startOfShakes = 0;
	int shakes = 0;

	private static class preferences {
		protected static short shakesRequired = 4;
		protected static float accelerationRequired = 5;
		protected static int shakeTimeout = 2000;
		protected static int jerkTimeout = 100;
	}

	private final SensorEventListener mSensorListener = new SensorEventListener() {

		public void onSensorChanged(SensorEvent se) {
			float x = se.values[0];
			float y = se.values[1];
			float z = se.values[2];
			mAccelLast = mAccelCurrent;
			mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
			float delta = mAccelCurrent - mAccelLast;
			mAccel = mAccel * 0.9f + delta; // perform low-cut filter
			long cachedSystemTime = System.currentTimeMillis();
			if (mAccel > preferences.accelerationRequired) {
				if (startOfJerk == 0) {
					startOfJerk = cachedSystemTime;
				}
				if (startOfShakes == 0)
					startOfShakes = cachedSystemTime;
				else if ((cachedSystemTime - startOfShakes) > preferences.shakeTimeout) {
					Log.d("ShakeSense", "Shake timeout ("
							+ (cachedSystemTime - startOfShakes) + ")");
					shakes = 0;
					startOfShakes = cachedSystemTime;
				}
			} else if (startOfJerk != 0) {
				if ((cachedSystemTime - startOfJerk) > preferences.jerkTimeout) {
					startOfJerk = 0;
					shakes++;
					Log.d("ShakeSense", "Phone jerk " + shakes
							+ " (acceleration>"
							+ preferences.accelerationRequired + ")");
					if (shakes >= preferences.shakesRequired) {
						startOfJerk = shakes = 0;
						listener.onShaken();
					}
				}
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	public void onResume() {
		mSensorManager.registerListener(mSensorListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);

	}

	public void onStop() {
		mSensorManager.unregisterListener(mSensorListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
	}

	public void initialize(SensorManager sensorManager) {
		assert sensorManager != null;
		if (sensorManager != null) {
			mSensorManager = sensorManager;
			mSensorManager.registerListener(mSensorListener,
					mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_NORMAL);
			mAccel = 0.00f;
			mAccelCurrent = SensorManager.GRAVITY_EARTH;
			mAccelLast = SensorManager.GRAVITY_EARTH;
		}
	}

	public void setOnShakeListener(OnShakeListener listener) {
		if (listener != null)
			this.listener = listener;
	}

}