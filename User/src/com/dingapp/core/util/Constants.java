package com.dingapp.core.util;

import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.dingapp.andriod.z20.R;
import com.dingapp.core.app.Application;

public class Constants {

	public final static String BROADCAST_CLEAR = "dingapp-clear";

	public final static String BROADCAST_MEMBER = "dingapp-broadcast-member";

	public final static String SHARED_TO_OTHER_APP = "dingapp-shared";

	public final static String BROADCAST_CATEGORY_CHANGED = "dingapp-category-changed";

	public final static String BROADCAST_PUSH_MESSAGE = "dingapp-push-message";

	public static final int sAnimDuration = 300;

	private static DisplayMetrics sMetrics;

	public synchronized static DisplayMetrics getDisplayMetrics() {
		if (sMetrics == null) {
			sMetrics = calcDisplayMetrics();
		}
		return sMetrics;
	}

	private static DisplayMetrics calcDisplayMetrics() {
		WindowManager wm = (WindowManager) Application.getAppContext()
				.getSystemService(Context.WINDOW_SERVICE);
		Display dis = wm.getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		dis.getMetrics(outMetrics);
		return outMetrics;
	}

	public synchronized static int getScreenWidth() {
		if (sMetrics == null) {
			sMetrics = calcDisplayMetrics();
		}
		return sMetrics.widthPixels;
	}

	public synchronized static int getScreenHeight() {
		if (sMetrics == null) {
			sMetrics = calcDisplayMetrics();
		}
		return sMetrics.heightPixels;
	}

	private static String sWHPServer;

	public static String getServer() {
		if (TextUtils.isEmpty(sWHPServer)) {
			sWHPServer = Application.getAppContext().getString(
					R.string.WHP_Server);
		}
		return sWHPServer;
	}

	private static String sWHPPort;

	public static String getPort() {
		if (TextUtils.isEmpty(sWHPPort)) {
			sWHPPort = Application.getAppContext().getString(R.string.WHP_Port);
		}
		return sWHPPort;
	}

	private static String sWHPTimeout;

	public static String getTimeout() {
		if (TextUtils.isEmpty(sWHPTimeout)) {
			sWHPTimeout = Application.getAppContext().getString(
					R.string.WHP_Timeout);
		}
		return sWHPTimeout;
	}

	public static final String IMG_DEFAULT_LIST_FAIL = "default_list_fail";
	public static final String IMG_DEFAULT_LIST_LOADING = "default_list_loading";
	public static final String IMG_DEFAULT_LIST = "default_list";
	// TODO : BIZ package 必须实现这三个接口且不能混淆这三个接口
	public static final String BIZ_UPGRADE_API_IMPL = "com.dingapp.biz.api.AppRegisterImpl";
	public static final String BIZ_DOWNLOADER_API_IMPL = "com.dingapp.biz.api.ResourceDownloadImpl";

	private static Point sInforFocusPicWH = null;

	public static Point getFocusPictureWH() {
		if (sInforFocusPicWH == null) {
			/* 资讯类焦点图的宽高比是2:1 */
			int width = getScreenWidth();
			int height = width / 2;
			sInforFocusPicWH = new Point(width, height);
		}

		return sInforFocusPicWH;
	}
}
