package com.reubenjohn.android.debug;

import android.util.Log;

public class NullPointerAsserter {

	String tag;

	public NullPointerAsserter(String tag) {
		this.tag = tag;
	}

	public void assertPointer(Object... objects) {
		for (Object o : objects) {
			if (o == null)
				Log.e(tag, "XML bridge failure");
		}
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
}
