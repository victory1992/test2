package com.dingapp.biz.pay;

import android.util.Log;

/**
 * 日志工具类
 * 
 * @author
 * 
 */
public class LogUtils {
	private static boolean DEBUG = true;

	public static void d(String tag, String msg) {
		if (DEBUG) {
			Log.d(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (DEBUG) {
			Log.e(tag, msg);
		}
	}

	public static void v(String tag, String msg) {
		if (DEBUG) {
			Log.v(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (DEBUG) {
			Log.i(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (DEBUG) {
			Log.w(tag, msg);
		}
	}
}
