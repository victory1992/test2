package com.dingapp.core.util;

import android.util.Log;

public class LoggerUtil {
	public static final boolean S_DEBUG = true;

	public static void v(String tag, String msg) {
		if (S_DEBUG) {
			Log.v(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (S_DEBUG) {
			Log.d(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (S_DEBUG) {
			Log.e(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (S_DEBUG) {
			Log.i(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (S_DEBUG) {
			Log.w(tag, msg);
		}
	}
}
