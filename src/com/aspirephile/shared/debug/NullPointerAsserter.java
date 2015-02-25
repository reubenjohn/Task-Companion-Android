package com.aspirephile.shared.debug;

import android.util.Log;

public class NullPointerAsserter {

	String tag;

	public NullPointerAsserter(String tag) {
		this.tag = tag;
	}

	public NullPointerAsserter(Class<?> cls) {
		this.tag = cls.getCanonicalName();
	}

	public boolean assertPointer(Object... objects) {
		boolean allGood = true;
		try {
			boolean array = objects.length > 1; // Also checks if objects points
												// to null
			for (int i = 0; i < objects.length; i++) {
				try {
					objects[i].getClass();
				} catch (NullPointerException e) {
					allGood = false;
					Log.e(tag, "NullPointerException of object"
							+ (array ? ("[" + i + "]") : ""));
					removeFromStackBottom(e);
					e.printStackTrace();
				}
			}
		} catch (NullPointerException e) {
			allGood = false;
			Log.e(tag, "NullPointerException of object");
			removeFromStackBottom(e);
			e.printStackTrace();
		}

		return allGood;
	}

	public boolean assertPointerQuietly(Object... objects) {
		if (objects != null) {
			for (int i = 0; i < objects.length; i++) {
				if (objects[i] == null) {
					return false;
				}
			}
		} else
			return false;
		return true;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	private void removeFromStackBottom(Exception e) {
		if (e != null) {
			StackTraceElement[] ea = e.getStackTrace();
			if (ea != null) {
				for (int j = 0; j < ea.length - 1; j++) {
					ea[j] = ea[j + 1];
				}
				e.setStackTrace(ea);
			}
		}
	}
}
