package com.dingapp.biz.hx;

import android.app.Activity;
import android.util.Log;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

public class HXUtils {
	private static String userName;
	private static String pwd;
	private static Activity mActivity;

	public static void register() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					EMClient.getInstance().createAccount(userName, pwd);
					Log.d("huanxin", "register sucess");
					mActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							EMClient.getInstance()
									.login(userName, pwd, calBack);
						}
					});
				} catch (HyphenateException e) {
					e.printStackTrace();
					Log.d("huanxin", e.getErrorCode() + "");
					Log.d("huanxin", "register fail");
				}
			}
		}).start();

	}

	private static EMCallBack calBack = new EMCallBack() {

		@Override
		public void onSuccess() {
			EMClient.getInstance().groupManager().loadAllGroups();
			EMClient.getInstance().chatManager().loadAllConversations();
			Log.d("huanxin", "login success");
		}

		@Override
		public void onProgress(int arg0, String arg1) {

		}

		@Override
		public void onError(int code, String arg1) {
			if (code == 204) {
				register();
				return;
			}
			Log.d("huanxin", "login fail");
		}
	};

	public static void login(String username, String pwd, Activity activty) {
		HXUtils.userName = username;
		if (pwd != null) {
			HXUtils.pwd = pwd.toUpperCase();
		}
		mActivity = activty;
		EMClient.getInstance().login(username, HXUtils.pwd, calBack);
	}

	public static void loginOut() {
		EMClient.getInstance().logout(true);
	}
}
