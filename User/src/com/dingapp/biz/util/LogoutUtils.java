package com.dingapp.biz.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import com.dingapp.biz.AppConstants;
import com.dingapp.biz.hx.HXUtils;
import com.dingapp.core.app.NavigationFragment;
import com.dingapp.core.app.StubActivity;
import com.dingapp.core.db.dao.MemberDao;

public class LogoutUtils {
	public static void logout(Activity activity) {
		HXUtils.loginOut();
		AppConstants.member = null;
		MemberDao memberDao = new MemberDao();
		memberDao.deleteAll();
		// 登出设置为true
		SpUtils.putLogoutFlag(activity, true);
		doInentToEntryFragment(activity);
	}

	private static void doInentToEntryFragment(Activity activity) {
		JSONObject extraObj = new JSONObject();
		try {
			extraObj.put(NavigationFragment.sPageNames,
					"home_page,making_friends,goods_carts,member_center");
			extraObj.put(NavigationFragment.sTabNames, "首页,分类,购物车,我的");
			extraObj.put(NavigationFragment.sNormalIcons,
					"tab1_normal,tab2_normal,tab3_normal,tab4_normal");
			extraObj.put(NavigationFragment.sFocusIcons,
					"tab1_focused,tab2_focused,tab3_focused,tab4_focused");
		} catch (JSONException e) {
			e.printStackTrace();
			activity.finish();
			return;
		}
		activity.startActivity(StubActivity.ctorJmpIntent("navigation",
				extraObj.toString()));
		activity.finish();
	}
}
