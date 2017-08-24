package com.dingapp.biz.util;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.DisplayMetrics;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;

import com.dingapp.core.app.Application;
import com.dingapp.core.util.AndroidUtil;

public class WebViewUtils {
	// 初始化webview
	@SuppressWarnings("deprecation")
	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	public static void initWebView(WebView wv) {
		wv.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);// 取消滚动条
		WebSettings webSettings = wv.getSettings();
		webSettings.setDefaultTextEncodingName("UTF-8");// 设置默认为utf-8
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		// 关闭缩放
		// Added in API level 3
		webSettings.setBuiltInZoomControls(true);
		// add in api 1
		webSettings.setSupportZoom(false);
		// add in api 11
		if (Build.VERSION.SDK_INT > 11) {
			webSettings.setDisplayZoomControls(false);
		}
		webSettings.setAppCacheEnabled(true); // 默认是关闭的
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		if (AndroidUtil.isNetworkAvailable(Application.getAppContext()))
			webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		else
			webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		webSettings.setDomStorageEnabled(true);
		// 不让保存用户密码，保存的话会明文存放在/data/app/中，root的用户可以很容易拿到用户数据
		webSettings.setSavePassword(false);
		webSettings.setDatabaseEnabled(true);
		// 使webview支持localstorage必须设置此属性
		webSettings.setDatabasePath(Application.getAppContext().getFilesDir()
				+ "/webview_database");
		webSettings.setUseWideViewPort(true);
		// 硬件加速
		webSettings.setRenderPriority(RenderPriority.HIGH);
		webSettings.setLoadWithOverviewMode(true);
		// webSettings.setUserAgentString(Utils.getWebviewUA(webSettings.getUserAgentString()));

		wv.setInitialScale(125);
		DisplayMetrics metrics = new DisplayMetrics();
		// ((StubActivity)context).getWindowManager().getDefaultDisplay()
		// .getMetrics(metrics);
		int mDensity = metrics.densityDpi;
		if (mDensity == 240) {
			webSettings.setDefaultZoom(ZoomDensity.FAR);
		} else if (mDensity == 160) {
			webSettings.setDefaultZoom(ZoomDensity.MEDIUM);
		} else if (mDensity == 120) {
			webSettings.setDefaultZoom(ZoomDensity.CLOSE);
		} else if (mDensity == DisplayMetrics.DENSITY_XHIGH) {
			webSettings.setDefaultZoom(ZoomDensity.FAR);
		} else if (mDensity == DisplayMetrics.DENSITY_TV) {
			webSettings.setDefaultZoom(ZoomDensity.FAR);
		} else {
			webSettings.setDefaultZoom(ZoomDensity.MEDIUM);
		}
	}
}
