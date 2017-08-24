package com.dingapp.core.util;

import android.annotation.SuppressLint;

public class PrefUtil {

	@SuppressLint("CommitPrefEdits")
	private PrefUtil() {
		// mStorageImpl = PreferenceManager
		// .getDefaultSharedPreferences(Application.getAppContext());
	}

	private static class SLocker {
		private static PrefUtil sPref = new PrefUtil();
	}

	public static PrefUtil getInstance() {
		return SLocker.sPref;
	}
}
