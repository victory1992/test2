package com.dingapp.core.app;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.core.db.dao.MemberDao;
import com.dingapp.core.db.orm.Member;
import com.dingapp.core.util.ButtonIsFastClickUtils;
import com.dingapp.core.util.LoggerUtil;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.util.EasyUtils;
import com.umeng.message.PushAgent;

public class StubActivity extends FragmentActivity implements IPageSwitcher {
	public static final String sArgsPageName = "page_name";
	public static final String sArgsSlideMenu = "has_slide_menu";
	// 传递给目标fragment的参数，为json字符串
	public static final String sArgsCustom = "custom_args";
	public static final String sArgsIsPush = "is_push";
	public static final String sArgsStartup = "start_up";

	private int[] mFirstFragmentAnimation;
	private boolean isExit;

	public static final String SAVE_TAG = "restore";
	private RelativeLayout status_bar;
	// 跳转url传递的参数
	private String jsonParams = "";

	@TargetApi(19)
	@Override
	protected void onCreate(Bundle saveInstanceStatue) {
		super.onCreate(saveInstanceStatue);
		//
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		// getWindow().setFlags(
		// WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
		// WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// }
		// 推送启动
		PushAgent.getInstance(this).onAppStart();
		ActivityManager.getInstance().addActicity(this);
		setContentView(R.layout.stub_layout);
		// // 状态栏
		// status_bar = (RelativeLayout) findViewById(R.id.status_bar);
		// setHeight(getStatusBarHeight());
		if (saveInstanceStatue != null
				&& saveInstanceStatue.containsKey(SAVE_TAG)
				&& saveInstanceStatue.getBoolean(SAVE_TAG)) {
			restore(saveInstanceStatue);
		} else {
			Member member = new MemberDao().get();
			if (member == null) {
				AppConstants.member = new Member();
				AppConstants.member.setSessionId("");
			} else {
				AppConstants.member = member;
			}
			jumpToTargetFrg();
		}
		// PushAgent.getInstance(this).onAppStart();
	}

	public void setHeight(int height) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			return;
		}
		if (height != 0) {
			height = getStatusBarHeight();
		}
		LinearLayout.LayoutParams layoutParams = (android.widget.LinearLayout.LayoutParams) status_bar
				.getLayoutParams();
		layoutParams.height = height;
		layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
		status_bar.setLayoutParams(layoutParams);

	}

	// 获取状态栏高度
	public int getStatusBarHeight() {
		// TODO Auto-generated method stub
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			return 0;
		}
		try {
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			Field field = clazz.getField("status_bar_height");
			int x = Integer.parseInt(field.get(clazz).toString());
			return getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	// 避免双次点击，定制模块不能拦截
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			String frgTag = getTopBaseFragment().getFragmentTag();
			if (frgTag != null && !TextUtils.isEmpty(frgTag)
					&& frgTag.contains("BeginDesignFragment")) {
				return super.dispatchTouchEvent(ev);
			}
			if (ButtonIsFastClickUtils.isFastDoubleClick()) {
				return true;
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("restore", true);
		outState.putIntArray("firstFragmentAnimation", mFirstFragmentAnimation);
		FragmentManager fm = getSupportFragmentManager();
		int count = fm.getBackStackEntryCount();
		outState.putInt("fragmentCount", count);
		Bundle fragmentsBundle = new Bundle();
		for (int i = 0; i < count; i++) {
			BackStackEntry bse = fm.getBackStackEntryAt(i);
			BaseFragment bFrg = (BaseFragment) fm.findFragmentByTag(bse
					.getName());
			Bundle frgBundle = new Bundle();
			fragmentsBundle.putString("1_pageName", bFrg.getPagename());
			bFrg.onSaveInstanceState(frgBundle);
			fragmentsBundle.putBundle("1_bundle", frgBundle);
		}
		outState.putBundle("fragments", fragmentsBundle);
	}

	private void innerOpenPage(String pageName, Bundle args) {
		PageJumper jumper = new PageJumper();
		Integer enterIn = R.anim.page_in;
		Integer enterOut = R.anim.page_out;
		Integer popIn = R.anim.pop_in;
		Integer popOut = R.anim.pop_out;
		jumper.mAnimation = new int[] { enterIn.intValue(),
				enterOut.intValue(), popIn.intValue(), popOut.intValue() };
		jumper.mContainerId = R.id.fragment_container;
		jumper.mNewActivity = false;
		jumper.mArgs = args;
		openPage(pageName, jumper);
	}

	@Override
	public void openPage(String pageName, PageJumper jmp) {
		if (jmp.mNewActivity) {
			Intent mainIntent = new Intent();
			mainIntent
					.setClass(Application.getAppContext(), StubActivity.class);
			mainIntent.putExtra(sArgsPageName, pageName);
			mainIntent.putExtra(sArgsSlideMenu, false); // TODO : fragment
														// 带侧边栏怎么办
			mainIntent.putExtra(sArgsCustom, jmp.mArgs);
			mainIntent.putExtra(sArgsIsPush, false);
			mainIntent.putExtra(sArgsStartup, false);
			startActivity(mainIntent);
		} else {
			BaseFragment frg = PageProtocolManager.translatePageNameToFrg(
					pageName, jsonParams);
			if (frg == null) {
				LoggerUtil.e(pageName, "can't get frg!");
				return;
			}
			// 会报空指针错误
			if (frg.getArguments() != null) {
				for (String key : frg.getArguments().keySet()) {
					jmp.mArgs.putString(key,
							(String) frg.getArguments().get(key));
				}
				frg.setArguments(null);
			}
			PageManager.getInstance().openPageWithFragmentActivity(this, frg,
					jmp);
		}
	}

	@Override
	public void gotoPage(String pageName, PageJumper jmp) {
		BaseFragment frg = PageProtocolManager.translatePageNameToFrg(pageName,
				jsonParams);
		if (frg == null) {
			LoggerUtil.e(pageName, "can't get frg!");
			return;
		}

		PageManager.getInstance().gotoPageWithFragmentActivity(this, frg, jmp);
	}

	private void restore(Bundle saveInstanceStatue) {
		mFirstFragmentAnimation = saveInstanceStatue
				.getIntArray("firstFragmentAnimation");
		int fragmentCount = saveInstanceStatue.getInt("fragmentCount");
		Bundle fragmentsBundle = saveInstanceStatue.getBundle("fragments");
		for (int i = 0; i < fragmentCount; i++) {
			String pageName = fragmentsBundle.getString(i + "_pageName");
			Bundle args = fragmentsBundle.getBundle(i + "_bundle");
			// innerOpenPage(pageName, args);
		}
	}

	private void jumpToTargetFrg() {
		Intent intent = getIntent();
		if (intent.getBooleanExtra(sArgsSlideMenu, false)) {
			initLeftMenu();
		}
		boolean containsKey = intent.getExtras().containsKey("message");
		String pageName = intent.getStringExtra(sArgsPageName);
		boolean isFromPush = intent.getBooleanExtra(sArgsIsPush, false);
		boolean startup = intent.getBooleanExtra(sArgsStartup, false);
		Bundle toFrgBundleArgs = intent.getBundleExtra(sArgsCustom);
		if (intent.hasExtra("jsonParams")) {
			jsonParams = intent.getStringExtra("jsonParams");
		}
		if (isFromPush) {
			if (startup) {
				// 如果点击push消息需要跳转到指定得业务页面，那么在业务页面直接点击back键会直接推出程序。
				// 为了避免这种情况，需要提前将首页加载。
				innerOpenPage("navigation", toFrgBundleArgs);
			}
			// toFrgBundleArgs 有冗余属性，可以不使用冗余属性。
			innerOpenPage(pageName, toFrgBundleArgs);
			return;
		} else if (containsKey) {
			toFrgBundleArgs.putInt("currIdx", 0);
			innerOpenPage("navigation", toFrgBundleArgs);
			innerOpenPage("wechat_home", toFrgBundleArgs);
			return;
		} else {
			innerOpenPage(pageName, toFrgBundleArgs);
			return;
		}
	}

	private static Bundle ctorToFrgBundle(String toFrgJsonStringArgs) {
		Bundle bundle = new Bundle();
		if (TextUtils.isEmpty(toFrgJsonStringArgs)) {
			return bundle;
		}
		try {
			JSONObject toFrgJsonArgs = new JSONObject(toFrgJsonStringArgs);
			Iterator<?> iter = toFrgJsonArgs.keys();
			while (iter.hasNext()) {
				String iterArgName = (String) iter.next();
				String iterValue = toFrgJsonArgs.getString(iterArgName);
				bundle.putString(iterArgName, iterValue);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return bundle;
	}

	private void initLeftMenu() {
		// TODO LeftMenu必须独占一个activity
	}

	private boolean couldPopBack() {
		FragmentManager actMgr = getSupportFragmentManager();
		int entryCnt = actMgr.getBackStackEntryCount();
		return entryCnt > 1;
	}

	private boolean couldPopStack() {
		int actCnt = ActivityManager.getInstance().getActivityCnt();
		return actCnt > 1;
	}

	@Override
	public void popBack(Bundle bundle) {
		if (couldPopBack()) {
			PageManager.getInstance().popBackWithFragmentActivity(this, bundle);
		} else if (couldPopStack()) {
			PageManager.getInstance()
					.popStackWithFragmentActivity(this, bundle);
		} else {
			exitBy2Click();
		}
	}

	@Override
	public void popStack(Bundle bundle) {
		if (couldPopStack()) {
			PageManager.getInstance()
					.popStackWithFragmentActivity(this, bundle);
		} else {
			exitBy2Click();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		BaseFragment topFrg = getTopBaseFragment();
		if (topFrg != null) {
			topFrg.onHiddenChanged(true);
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		// 应用未退出，需要重新配置一些文件的时候-----请求后台是否需要更新，如果需要更新，update
		Log.d("stubActivityList", Application.stubActivityList.size() + "");
		if (Application.stubActivityList.size() == 1) {
			// Intent intent = new Intent(this, AdvanceActivity.class);
			// startActivity(intent );
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		BaseFragment topFrg = getTopBaseFragment();
		if (topFrg != null) {
			topFrg.onHiddenChanged(false);
			if (!EasyUtils.isAppRunningForeground(getApplicationContext())) {
				String topName = topFrg.getClass().getSimpleName();
				if (topName != null
						&& topName.equals(NavigationFragment.class
								.getSimpleName())) {
					Log.d("top", topFrg.getClass().getSimpleName());
				}
			}
		}
		EaseUI.getInstance().getNotifier().reset();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode != KeyEvent.KEYCODE_BACK) {
			return false;
		}

		BaseFragment topFrg = getTopBaseFragment();
		if (topFrg != null && topFrg.onKeyDown(keyCode, event)) {
			return true;
		}
		if (couldPopBack()) {
			PageManager.getInstance().popBackWithFragmentActivity(this, null);
			return true;
		}

		if (couldPopStack()) {
			PageManager.getInstance().popStackWithFragmentActivity(this, null);
			return true;
		}

		exitBy2Click();
		return true;
	}

	private void exitBy2Click() {
		// moveTaskToBack(false);
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			// Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
			this.finish();
		} else {
			ActivityManager.getInstance().killMyProcess();
		}
	}

	@Override
	public BaseFragment getTopBaseFragment() {
		return PageManager.getInstance().getTopBaseFragment(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityManager.getInstance().removeActivity(this);
		if (ActivityManager.getInstance().getActivityCnt() == 0) {
			PageManager.getInstance().destroy();
		}
	}

	@Override
	public int[] getFirstFragmentAnimation() {
		return mFirstFragmentAnimation;
	}

	public static Intent ctorJmpIntent(String pageName) {
		return ctorJmpIntent(pageName, false, null);
	}

	public static Intent ctorJmpIntent(String pageName, boolean hasLeftSlideMenu) {
		return ctorJmpIntent(pageName, hasLeftSlideMenu, null, false, false);
	}

	public static Intent ctorJmpIntent(String pageName, String extraParameters) {
		return ctorJmpIntent(pageName, false, extraParameters, false, false);
	}

	public static Intent ctorJmpIntent(String pageName,
			boolean hasLeftSlideMenu, String extraParameters) {
		return ctorJmpIntent(pageName, hasLeftSlideMenu, extraParameters,
				false, false);
	}

	public static Intent ctorJmpIntent(String pageName,
			boolean hasLeftSlideMenu, String extraParameters, boolean fromPush,
			boolean startup) {
		Intent mainIntent = new Intent();
		mainIntent.setClass(Application.getAppContext(), StubActivity.class);
		mainIntent.putExtra(sArgsPageName, pageName);
		mainIntent.putExtra(sArgsSlideMenu, hasLeftSlideMenu);
		if (!TextUtils.isEmpty(extraParameters)) {
			Bundle toFrgBundleArgs = ctorToFrgBundle(extraParameters);
			mainIntent.putExtra(sArgsCustom, toFrgBundleArgs);
		}
		mainIntent.putExtra(sArgsIsPush, fromPush);
		mainIntent.putExtra(sArgsStartup, startup);
		if (!TextUtils.isEmpty(extraParameters)
				&& !extraParameters.contains("focusIcons")
				&& !extraParameters.contains("tabNames")) {
			mainIntent.putExtra("jsonParams", extraParameters);
		}
		return mainIntent;
	}

	// public static void openPage(String url) {
	// BaseFragment frg = PageProtocolManager.translatePageNameToFrg(url);
	// if (frg == null) {
	// LoggerUtil.e(url, "can't get frg!");
	// return ;
	// }
	// PageJumper jmp = new PageJumper();
	// jmp.mArgs = new Bundle();
	// jmp.mArgs.putString("url_param", PageProtocolManager.calcParam(url));
	// PageManager.getInstance().openPageWithFragmentActivity(this, frg, jmp);
	// }
}
