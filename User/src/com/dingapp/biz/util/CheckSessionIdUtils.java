package com.dingapp.biz.util;

import android.app.Activity;
import android.content.Intent;

import com.dingapp.core.app.LaunchActivity;
import com.dingapp.core.db.dao.MemberDao;
import com.hyphenate.chat.EMClient;

public class CheckSessionIdUtils {
	public static void ReLogin(Activity activity){
		new MemberDao().deleteAll();
		EMClient.getInstance().logout(true);
		Intent intent = new Intent().setClass(
				activity, LaunchActivity.class);
		activity.startActivity(intent);
	}
}
