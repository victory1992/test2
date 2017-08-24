package com.dingapp.biz.util;

import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dingapp.biz.pay.LogUtils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * 工具类
 * 
 * @author
 * 
 */
public class Utils {

	static final String LOG_TAG = "PullToRefresh";
	static TelephonyManager mTelephonyManager;

	/**
	 * 是否联网
	 * 
	 * @param context
	 * @return true if connect is available
	 */
	public static boolean isNetworkAvailable(final Context context) {
		final ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	/**
	 * 获取应用版本号
	 * 
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			int versionCode = info.versionCode;
			return versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Integer.MAX_VALUE;
	}

	public static void warnDeprecation(String depreacted, String replacement) {
		Log.w(LOG_TAG, "You're using the deprecated " + depreacted
				+ " attr, please switch over to " + replacement);
	}

	/**
	 * 判断是不是手机号
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNum(String mobiles) {
		if (mobiles == null || mobiles.equals("")) {
			return false;
		}
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9])|(14[5,7]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		System.out.println(m.matches() + "---");
		return m.matches();

	}

	/**
	 * 确认字符串是否为email格式
	 * 
	 * @param strEmail
	 * @return
	 */
	public static boolean isEmail(String strEmail) {
		String strPattern = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strEmail);
		return m.matches();
	}

	/**
	 * 判断是否有网络连接
	 * 
	 * @param context
	 * @return
	 */
	public boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (mConnectivityManager == null) {
				return false;
			}
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 隐藏键盘
	 * 
	 * @param c
	 * @param et
	 */
	public static void closeInputMethod(Context c, View view) {
		InputMethodManager imm = (InputMethodManager) c
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();
		if (isOpen) {
			imm.hideSoftInputFromWindow(view.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 判断sim卡状态
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isSimExist(Context context) {
		mTelephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		boolean isExist = false;
		int simState = mTelephonyManager.getSimState();
		switch (simState) {
		case TelephonyManager.SIM_STATE_ABSENT:
			// mString = "无卡";
			Toast.makeText(context, "当前无可用sim卡，请插入sim卡", Toast.LENGTH_SHORT)
					.show();
			break;
		case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
			// mString = "需要NetworkPIN解锁";
			Toast.makeText(context, "需要NetworkPIN解锁", Toast.LENGTH_SHORT)
					.show();
			break;
		case TelephonyManager.SIM_STATE_PIN_REQUIRED:
			// mString = "需要PIN解锁";
			Toast.makeText(context, "需要PIN解锁", Toast.LENGTH_SHORT).show();
			break;
		case TelephonyManager.SIM_STATE_PUK_REQUIRED:
			// mString = "需要PUN解锁";
			Toast.makeText(context, "需要PUN解锁", Toast.LENGTH_SHORT).show();
			break;
		case TelephonyManager.SIM_STATE_READY:
			// mString = "良好";
			isExist = true;
			break;
		case TelephonyManager.SIM_STATE_UNKNOWN:
			// mString = "未知状态";
			Toast.makeText(context, "未知状态", Toast.LENGTH_SHORT).show();
			break;
		}
		return isExist;
	}

	/**
	 * 判断密码是否符合规则
	 * 
	 * @param psd
	 * @return
	 */
	public static boolean isPsdRight(String psd) {
		String pat = "[0-9a-zA-Z]{6,16}";
		Pattern pattern = Pattern.compile(pat);
		Matcher matcher = pattern.matcher(psd);
		return matcher.matches();
	}

	/**
	 * 昵称是否符合规则
	 * 
	 * @param psd
	 * @return
	 */
	public static boolean isNicknameRight(String nickname) {
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(nickname);
		return matcher.find();
	}

	/**
	 * dp转px
	 * 
	 * @param context
	 * @param dp
	 * @return
	 */
	public static int dp2px(Context context, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}

	private static long time = 0;

	/**
	 * 是否可以点击
	 * 
	 * @return
	 */
	public static boolean isClickable() {
		long currTime = System.currentTimeMillis();
		LogUtils.d("pb", "currTime : " + currTime);
		LogUtils.d("pb", "time : " + time);
		if (currTime - time < 500) {
			time = currTime;
			return false;
		}
		time = currTime;
		return true;
	}

	/**
	 * @return 保留两位 小数
	 */
	public static String keepDouble2(double value){
		DecimalFormat df = new DecimalFormat("######0.00");
		return df.format(value);
	}
	public static boolean isBackground(Context context) {
	    ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
	    for (RunningAppProcessInfo appProcess : appProcesses) {
	         if (appProcess.processName.equals(context.getPackageName())) {
	                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
	                          Log.i("后台", appProcess.processName);
	                          return true;
	                }else{
	                          Log.i("前台", appProcess.processName);
	                          return false;
	                }
	           }
	    }
	    return false;
	}
}
