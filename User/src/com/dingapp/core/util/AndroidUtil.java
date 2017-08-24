package com.dingapp.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.dingapp.andriod.z20.R;
import com.dingapp.core.app.Application;

public class AndroidUtil {
	// 是否允许MAC使用MAC地址
	private static boolean ALLOW_MAC = true;
	// 唯一标识的长度
	private static int IDENTIFY_LEN = 14;
	// MAC地址的标识
	private static String KEY_MAC = "android_mac";
	// 初始化IMEI
	private static boolean mIntiIMEI = false;
	// 初始化MAC
	private static boolean mInitMAC = false;
	// IMEI缓存
	private static String mIMEI;
	// MAC地址缓存
	private static String mMAC;
	// 是否读取缓存
	private static boolean mReaderCache = false;

	// 是否读取的MAC地址
	public static boolean isReaderMac() {
		// 没有完成
		if (!isInitCompeleted()) {
			return false;
		}
		// 不允许MAC地址
		if (!ALLOW_MAC) {
			return false;
		}
		// 取IMEI地址
		if (!TextUtils.isEmpty(getIMEI())) {
			return false;
		}
		// 取MAC地址
		return true;
	}

	// 判断是否读取缓存
	public static boolean isReaderMACCache() {
		return mReaderCache;
	}

	// 获取IMEI地址
	private static String getIMEI() {
		if (mIntiIMEI) {
			return mIMEI;
		}
		TelephonyManager telephonyManager = (TelephonyManager) Application
				.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
		try {
			mIMEI = telephonyManager.getDeviceId();
		} catch (Exception e) {
		}
		mIntiIMEI = true;
		return mIMEI;
	}

	// 是否初始化完成
	public static boolean isInitCompeleted() {
		if (!TextUtils.isEmpty(getIMEI()) || mInitMAC) {
			return true;
		}
		return false;
	}

	/**
	 * 获取设备唯一标识，IMEI 或者 MAC
	 * 
	 * @return
	 */
	public static String getIdentifyId() {
		// 没有完成
		if (!isInitCompeleted()) {
			return null;
		}
		// 不允许MAC地址
		if (!ALLOW_MAC) {
			return getIMEI();
		}
		// 取IMEI地址
		if (!TextUtils.isEmpty(getIMEI())) {
			return getIMEI();
		}
		// 取MAC地址
		return getMAC();
	}

	// 获取MAC地址
	public static String getMAC() {
		if (mInitMAC) {
			return mMAC;
		}
		return null;
	}

	// 获取真实MAC地址
	public static String getMACSource() {
		if (mInitMAC && !TextUtils.isEmpty(mMAC)) {
			if (mMAC.length() > 12) {
				return mMAC.substring(0, 12);
			}
			return mMAC;
		}
		return "";
	}

	public static boolean initMACInMainThread() {
		return initMAC(1, true);
	}

	public static boolean initMACdoInBackground() {
		// 尝试100次
		return initMAC(100, false);
	}

	// 后台获取MAC地址
	public static String getMacFromDeviceInBackground() {
		return getMacFromDevice(100);
	}

	private static boolean initMAC(int internal, boolean readerCache) {
		String mac = null;
		// 是否读取缓存
		if (readerCache) {
			// 通过缓存获取
			mac = readSharedData(Application.getAppContext(), KEY_MAC);
			if (!TextUtils.isEmpty(mac)) {
				mInitMAC = true;
				mMAC = mac;
				mReaderCache = true;
				return true;
			}
		}

		mac = getMacFromDevice(internal);
		if (!TextUtils.isEmpty(mac)) {
			saveMacInfo(mac);
			mReaderCache = false;
			return true;
		}
		return false;
	}

	// 保存Mac地址
	public static void saveMacInfo(String mac) {
		mInitMAC = true;
		mMAC = mac;
		saveSharedData(Application.getAppContext(), KEY_MAC, mMAC);
	}

	// 尝试读取MAC地址
	private static String getMacFromDevice(int internal) {
		String mac = null;

		WifiManager wifiManager = (WifiManager) Application.getAppContext()
				.getSystemService(Context.WIFI_SERVICE);
		// 尝试获取mac
		mac = tryGetMAC(wifiManager);
		if (!TextUtils.isEmpty(mac)) {
			return mac;
		}

		// 获取失败，尝试打开wifi获取
		boolean isOkWifi = tryOpenMAC(wifiManager);
		for (int index = 0; index < internal; index++) {
			// 如果第一次没有成功，第二次做100毫秒的延迟。
			if (index != 0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			mac = tryGetMAC(wifiManager);
			if (!TextUtils.isEmpty(mac)) {
				break;
			}
		}

		// 尝试关闭wifi
		if (isOkWifi) {
			tryCloseMAC(wifiManager);
		}
		return mac;
	}

	// 尝试打开MAC
	private static boolean tryOpenMAC(WifiManager manager) {
		boolean softOpenWifi = false;
		int state = manager.getWifiState();
		if (state != WifiManager.WIFI_STATE_ENABLED
				&& state != WifiManager.WIFI_STATE_ENABLING) {
			manager.setWifiEnabled(true);
			softOpenWifi = true;
		}
		return softOpenWifi;
	}

	// 尝试关闭MAC
	private static void tryCloseMAC(WifiManager manager) {
		manager.setWifiEnabled(false);
	}

	// 尝试获取MAC地址
	private static String tryGetMAC(WifiManager manager) {
		WifiInfo wifiInfo = manager.getConnectionInfo();
		if (wifiInfo == null || TextUtils.isEmpty(wifiInfo.getMacAddress())) {
			return null;
		}
		String mac = wifiInfo.getMacAddress().replaceAll(":", "").trim()
				.toUpperCase();
		mac = formatIdentify(mac);
		return mac;
	}

	// 格式化唯一标识
	private static String formatIdentify(String identify) {
		// 判空
		if (TextUtils.isEmpty(identify)) {
			return identify;
		}
		// 去除空格
		identify = identify.trim();
		// 求长度
		int len = identify.length();
		// 正好
		if (len == IDENTIFY_LEN) {
			return identify;
		}
		// 过长，截取
		if (len > IDENTIFY_LEN) {
			return identify.substring(0, IDENTIFY_LEN);
		}
		// 过短，补0
		for (; len < IDENTIFY_LEN; len++) {
			identify += "0";
		}
		// 大于默认
		return identify;
	}

	private final static Map<String, String> map = new ConcurrentHashMap<String, String>();

	private static String readSharedData(Context context, String key) {
		return map.get(key);
	}

	private static void saveSharedData(Context content, String key, String mac) {
		map.put(key, mac);
	}

	public static int getVerCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			LoggerUtil.e("androidutil", e.getMessage());
		}
		return verCode;
	}

	public static String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			LoggerUtil.e("androidutil", e.getMessage());
		}
		return verName;
	}

	public static boolean isNetworkAvailable(Context context) {
		if (context == null) {
			return false;
		}
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static void doIntentToCallTelephone(Context context, String phoneNum) {
		Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
				+ phoneNum));
		context.startActivity(intent);
	}

	private static HashMap<String, String> mAssetCache = new HashMap<String, String>();

	public static String getAssetsContents(String name) {
		if (mAssetCache.containsKey(name)) {
			return mAssetCache.get(name);
		}

		StringBuilder sb = new StringBuilder();
		AssetManager asm = Application.getAppContext().getAssets();
		InputStream is = null;
		try {
			is = asm.open(name);
			byte[] buffer = new byte[1024];
			int cnt = -1;
			while ((cnt = is.read(buffer)) > 0) {
				sb.append(new String(buffer, 0, cnt));
			}
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		String resource = sb.toString();
		mAssetCache.put(name, resource);
		return resource;
	}

	public static boolean isRunningForeground(Context ctx) {
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		String currentPackageName = cn.getPackageName();
		if (TextUtils.equals(currentPackageName, ctx.getPackageName())) {
			return true;
		}

		return false;
	}

	private static Notification getNotification(int iconResId, String title) {
		Notification notify = new Notification(iconResId, title,
				System.currentTimeMillis());
		notify.flags = Notification.FLAG_AUTO_CANCEL;
		notify.defaults = Notification.DEFAULT_LIGHTS;
		notify.ledARGB = 0xff00ff00;
		notify.ledOnMS = 3000; // 亮的时间
		notify.flags |= Notification.FLAG_SHOW_LIGHTS;
		notify.sound = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		notify.defaults |= Notification.DEFAULT_VIBRATE;
		notify.vibrate = new long[] { 0, 100, 200, 300 };
		return notify;
	}

	public static void sendNotification(Context ctx, Intent notifyIntent,
			String tipMsg) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager notifyMgr = (NotificationManager) ctx
				.getSystemService(ns);
		// 定义通知栏展现的内容信息
		int icon = R.drawable.icon;
		Notification notification = getNotification(icon, tipMsg);
		// 定义下拉通知栏时要展现的内容信息
		CharSequence contentTitle = ctx.getString(R.string.app_name);
		CharSequence contentText = tipMsg;
		try {
			PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
					notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			notification.setLatestEventInfo(ctx, contentTitle, contentText,
					contentIntent);
			// 用mNotificationManager的notify方法通知用户生成标题栏消息通知
			notifyMgr.notify(1, notification);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}