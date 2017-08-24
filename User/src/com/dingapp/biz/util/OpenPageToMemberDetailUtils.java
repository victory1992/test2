package com.dingapp.biz.util;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.dingapp.core.app.BaseFragment;

public class OpenPageToMemberDetailUtils {
	public static void openPage(Fragment frg, String member_id, boolean isNewAct) {
		Bundle bundle = new Bundle();
		bundle.putString("member_id", member_id);
		((BaseFragment) frg).openPage("member_detail", bundle, isNewAct);
	}
}
