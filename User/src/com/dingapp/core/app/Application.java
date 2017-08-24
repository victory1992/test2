package com.dingapp.core.app;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.hx.HXContactUtils;
import com.dingapp.biz.net.SingleRequestQueue;
import com.dingapp.core.db.dao.MemberDao;
import com.dingapp.core.db.orm.Member;
import com.dingapp.core.init.ImageLoaderLauncher;
import com.dingapp.core.launcher.LauncherAgent;
import com.dingapp.core.util.LoggerUtil;
import com.dingapp.imageloader.core.DisplayImageOptions;
import com.dingapp.imageloader.core.ImageLoader;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.model.EaseNotifier.EaseNotificationInfoProvider;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.util.EasyUtils;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

public class Application extends android.app.Application implements
		UncaughtExceptionHandler {

	private static Application sInstance;
	private UncaughtExceptionHandler mDefaultExceptionHandler;
	private DisplayImageOptions mDefaultImgDisplayOption;
	private Boolean mInitialized = Boolean.valueOf(false);
	public static List<StubActivity> stubActivityList = new ArrayList<StubActivity>();
	private static EMMessageListener emListener;

	public void onCreate() {
		super.onCreate();

		// 多进程，不共享Application
		if (sInstance == null) {
			sInstance = this;
		}

		String processName = getCurProcessName();
		String pkgName = getPackageName();
		LoggerUtil.d("init-job", "processName:" + processName + " - pkgName:"
				+ pkgName);
		if (!TextUtils.equals(pkgName, processName)) {
			return;
		}
		initApplication();
		registerActivityLife();
	}

	public static EMMessageListener getEMMessageListener() {
		return emListener;
	}

	private void initHX() {
		EaseUI.getInstance().init(getApplicationContext(), null);
		emListener = new EMMessageListener() {

			@Override
			public void onMessageReceived(List<EMMessage> arg0) {
				if (!EasyUtils.isAppRunningForeground(getApplicationContext())) {

				} else {
					Log.d("activity status", "foregound");
				}
				checkMsg(arg0);
			}

			@Override
			public void onMessageReadAckReceived(List<EMMessage> arg0) {

			}

			@Override
			public void onMessageDeliveryAckReceived(List<EMMessage> arg0) {

			}

			@Override
			public void onMessageChanged(EMMessage arg0, Object arg1) {

			}

			@Override
			public void onCmdMessageReceived(List<EMMessage> arg0) {

			}
		};
		EMClient.getInstance().chatManager().addMessageListener(emListener);
		EaseUI.getInstance()
				.getNotifier()
				.setNotificationInfoProvider(
						new EaseNotificationInfoProvider() {

							@Override
							public String getTitle(EMMessage message) {
								return null;
							}

							@Override
							public int getSmallIcon(EMMessage message) {
								// TODO Auto-generated method stub
								return 0;
							}

							@Override
							public Intent getLaunchIntent(EMMessage message) {
								Intent intent = new Intent().setClass(
										getAppContext(), LaunchActivity.class);
								intent.putExtra("message", "message");
								return intent;
							}

							@Override
							public String getLatestText(EMMessage message,
									int fromUsersNum, int messageNum) {
								// TODO Auto-generated method stub
								return null;
							}

							@Override
							public String getDisplayedText(EMMessage message) {
								// TODO Auto-generated method stub
								return null;
							}
						});
	}

	// umeng 推送
	private void initUPush() {
		PushAgent.getInstance(sInstance).setNotificaitonOnForeground(true);
		PushAgent.getInstance(sInstance).register(new IUmengRegisterCallback() {

			@Override
			public void onSuccess(String arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onFailure(String arg0, String arg1) {
				// TODO Auto-generated method stub
			}
		});
		// 打包时
		PushAgent.getInstance(sInstance).setDebugMode(false);

	}

	protected void checkMsg(List<EMMessage> arg0) {
		for (int i = 0; i < arg0.size(); i++) {
			EMMessage emMessage = arg0.get(i);
			String content = emMessage.getBody().toString();
			String from = emMessage.getFrom();
			if (from != null && EaseUserUtils.helpMap.containsKey(from)
					&& content.contains("通过了你的好友申请")) {
				HXContactUtils.getInstance(getAppContext()).requestHxInfos(0);
			}
			if (from != null && !EaseUserUtils.contactMap.containsKey(from)) {
				HXContactUtils.getInstance(getAppContext()).requestHxInfos(0);
			}
		}
		EaseUI.getInstance().getNotifier().onNewMesg(arg0);
	}

	private void registerActivityLife() {
		this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

			@Override
			public void onActivityStopped(Activity arg0) {
				Log.d("onActivityStopped", arg0.getClass().getSimpleName());
			}

			@Override
			public void onActivityStarted(Activity arg0) {
				Log.d("onActivityStarted", arg0.getClass().getSimpleName());
			}

			@Override
			public void onActivitySaveInstanceState(Activity arg0, Bundle arg1) {
				Log.d("onActivitySaveInstanceState", arg0.getClass()
						.getSimpleName());
			}

			@Override
			public void onActivityResumed(Activity arg0) {
				Log.d("onActivityResumed", arg0.getClass().getSimpleName());
			}

			@Override
			public void onActivityPaused(Activity arg0) {
				Log.d("onActivityPaused", arg0.getClass().getSimpleName());
			}

			@Override
			public void onActivityDestroyed(Activity arg0) {
				// TODO Auto-generated method stub
				Log.d("onActivityDestroyed", arg0.getClass().getSimpleName());
				if (arg0 instanceof StubActivity) {
					stubActivityList.remove((StubActivity) arg0);
				}
			}

			@Override
			public void onActivityCreated(Activity arg0, Bundle arg1) {
				// TODO Auto-generated method stub
				Log.d("onActivityCreated", arg0.getClass().getSimpleName());
				if (arg0 instanceof StubActivity) {
					stubActivityList.add((StubActivity) arg0);
				}
			}
		});
	}

	public void initApplication() {
		// 防止重复初始化
		synchronized (mInitialized) {
			if (mInitialized.booleanValue()) {
				return;
			}
			mInitialized = Boolean.valueOf(true);
		}

		UncaughtExceptionHandler handler = Thread
				.getDefaultUncaughtExceptionHandler();
		// 多进程，不共享Application
		if (!(handler instanceof Application)) {
			mDefaultExceptionHandler = handler;
			Thread.setDefaultUncaughtExceptionHandler(this);
		}

		preUiInit();
	}

	private void initImageLoader() {
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		builder.showImageOnLoading(R.drawable.default_list_fail);
		builder.showImageOnFail(R.drawable.default_list_fail);
		builder.showImageForEmptyUri(R.drawable.default_list_fail);
		builder.cacheOnDisk(true);
		builder.cacheInMemory(true);
		setDefaultImgDisplayOption(builder.build());
		LauncherAgent.getInstance().register(
				new ImageLoaderLauncher(this, getDefaultImgDisplayOption()),
				null);
	}

	private void registerLauncherTask() {
		initImageLoader();
		AppConstants.urlList = new ArrayList<String>();
		Member member = new MemberDao().get();
		if (member == null) {
			AppConstants.member = new Member();
			AppConstants.member.setSessionId("");
		} else {
			AppConstants.member = member;
		}
	}

	void preUiInit() {
		// 初始化百度地图
		SDKInitializer.initialize(getApplicationContext());
		registerLauncherTask();
		LauncherAgent.getInstance().preInit();
		PageProtocolManager.initConfigureFile();
		SingleRequestQueue.getreRequestQueue(getInstance()
				.getApplicationContext());
		initHX();
		initUPush();
	}

	public void postUiInit() {
		LauncherAgent.getInstance().postInit();
	}

	public static Context getAppContext() {
		return sInstance.getApplicationContext();
	}

	public static Application getInstance() {
		return sInstance;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (mDefaultExceptionHandler != null) {
			Thread.setDefaultUncaughtExceptionHandler(mDefaultExceptionHandler);
		}

		ex.printStackTrace();
		ImageLoader.getInstance().stop();
		try {
			com.dingapp.core.app.ActivityManager.getInstance().killMyProcess();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DisplayImageOptions getDefaultImgDisplayOption() {
		return mDefaultImgDisplayOption;
	}

	public void setDefaultImgDisplayOption(
			DisplayImageOptions defaultImgDisplayOption) {
		this.mDefaultImgDisplayOption = defaultImgDisplayOption;
	}

	private String getCurProcessName() {
		int pid = android.os.Process.myPid();
		ActivityManager actMgr = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : actMgr
				.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}

	@Override
	public void onLowMemory() {
		// TODO 清空一些不使用的强引用对象
		super.onLowMemory();
	}
}