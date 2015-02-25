package com.aspirephile.shared.debug;

import android.util.Log;

public class Logger {

	String tag;

	public Logger(String tag) {
		setTag(tag);
	}

	public Logger(Class<?> cls) {
		setTag(cls);
	}

	public void setTag(String tag) throws NullPointerException {
		if (tag != null) {
			this.tag = tag;
		} else
			throw new NullPointerException();
	}

	public void setTag(Class<?> cls) throws NullPointerException {
		if (cls != null) {
			this.tag = cls.getName();
		} else
			throw new NullPointerException();
	}

	public void d(String msg) {
		Log.d(tag, msg);
	}

	public void e(String msg) {
		Log.e(tag, msg);
	}

	public void i(String msg) {
		Log.i(tag, msg);
	}

	public void w(String msg) {
		Log.w(tag, msg);
	}
}
