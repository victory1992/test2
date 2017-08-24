package com.dingapp.biz.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.dingapp.biz.AppConstants;
import com.dingapp.core.app.NavigationFragment;
import com.dingapp.core.app.StubActivity;
import com.dingapp.core.db.dao.MemberDao;
import com.dingapp.core.db.orm.Member;

public class MemberUtil {
	public static boolean hasLogin() {
		return new MemberDao().get() != null;
	}

	public static void reLogin(Context context) {
		MemberDao memberDao = new MemberDao();
		memberDao.deleteAll();
		AppConstants.member = new Member();
		Toast.makeText(context, "账号异常,请重新登录...", Toast.LENGTH_LONG).show();
		doInentToEntryFragment(context);
	}

	private static void doInentToEntryFragment(Context context) {
		JSONObject extraObj = new JSONObject();
		try {
			extraObj.put(
					NavigationFragment.sPageNames,
					"qichengyi://home_page?params={},qichengyi://making_friends?params={},qichengyi://personal_letter?params={},qichengyi://member_center?params={}");
			extraObj.put(NavigationFragment.sTabNames, "资讯,供求,帮帮,我的");
			extraObj.put(NavigationFragment.sNormalIcons,
					"tab1_normal,tab2_normal,tab3_normal,tab4_normal");
			extraObj.put(NavigationFragment.sFocusIcons,
					"tab1_focused,tab2_focused,tab3_focused,tab4_focused");
		} catch (JSONException e) {
			e.printStackTrace();
			((Activity) context).finish();
			return;
		}
		context.startActivity(StubActivity.ctorJmpIntent(
				"qichengyi://navigation?params={}", extraObj.toString()));
		((Activity) context).finish();
	}
}
