package com.dingapp.biz.util;

import android.app.Activity;
import android.content.Context;

public class SpUtils {
	public static final String DINGAPP = "dingapp";

	/**
	 * @param activity  将登出标志位置为flase
	 * @param flag
	 */
	public static void putLogoutFlag(Activity activity, boolean flag) {
		if(activity == null){
			return;
		}
		activity.getSharedPreferences(DINGAPP, Context.MODE_PRIVATE).edit()
				.putBoolean("isLogout", flag).commit();
	}

	/**
	 * @param activity   	获取登出标志位，默认给true
	 * @param defaultFlag
	 * @return
	 */
	public static boolean getLogoutFlag(Activity activity, boolean defaultFlag) {
		if(activity == null){
			return true;
		}
		return activity.getSharedPreferences(DINGAPP, Context.MODE_PRIVATE)
				.getBoolean("isLogout", defaultFlag);
	}
}
