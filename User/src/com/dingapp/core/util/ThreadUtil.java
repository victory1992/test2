package com.dingapp.core.util;

import android.os.Looper;

public class ThreadUtil {
	public static boolean isMainThread() {
		Looper looper = Looper.myLooper();
		return (looper != null) && (looper == Looper.getMainLooper());
	}
}
