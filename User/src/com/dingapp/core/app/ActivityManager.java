package com.dingapp.core.app;

import java.util.Stack;

import android.app.Activity;

public class ActivityManager {
	private Stack<Activity> mActivityStack = new Stack<Activity>();
	
	private ActivityManager() {
	}

	private static class SLocker {
		private static ActivityManager sInstance = new ActivityManager();
	}

	public static ActivityManager getInstance() {
		return SLocker.sInstance;
	}

	public void addActicity(Activity act) {
		mActivityStack.push(act);
	}

	public void removeActivity(Activity act) {
		mActivityStack.remove(act);
	}

	public void killMyProcess() {
		int nCount = mActivityStack.size();
		for (int i = nCount - 1; i >= 0; i--) {
			Activity activity = mActivityStack.get(i);
			activity.finish();
		}

		mActivityStack.clear();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public int getActivityCnt() {
		return mActivityStack.size();
	}

	public Activity getActivityAt(int idx) {
		if (idx < 0 || idx >= mActivityStack.size()) {
			return null;
		}

		return mActivityStack.get(idx);
	}

	public Activity getRootActivity() {
		if (mActivityStack.size() <= 0) {
			return null;
		}

		return mActivityStack.firstElement();
	}
}
