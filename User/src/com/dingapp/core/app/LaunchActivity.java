package com.dingapp.core.app;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.hx.HXContactUtils;
import com.dingapp.biz.hx.HXUtils;
import com.dingapp.biz.page.adapters.MyPageAdapter;
import com.dingapp.biz.util.MD5Util;
import com.dingapp.core.db.dao.MemberDao;
import com.dingapp.core.db.orm.Member;
import com.dingapp.core.util.Constants;

public class LaunchActivity extends Activity {

	public static final String LAUNCHER_IMG = "launchimage";
	private Handler mUIHandler;
	private long mStartTime;
	public static final long TIME_OUT = 2000;
	// 启动页
	private List<View> list = new ArrayList<View>();
	private MyPageAdapter myPageAdapter;
	private ViewPager vp;
	private TextView tv_tiyan;
	private ImageView iv_laungh_bg;
	private Runnable mTimeoutRunnable = new Runnable() {

		@Override
		public void run() {
			jumpIntoApplication();
		}
	};
	private boolean gotoPage;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mStartTime = System.currentTimeMillis();
		queryStubActivity();
		ActivityManager.getInstance().addActicity(this);
		setContentView(R.layout.launch);
		boolean isFirst = getSharedPreferences("splash_user",
				Context.MODE_PRIVATE).getBoolean("splash_user", false);
		HXContactUtils.getInstance(this).requestHxInfos(0);
		loginHX();
		iv_laungh_bg = (ImageView) findViewById(R.id.iv_laungh_bg);
		LayoutParams lp = iv_laungh_bg.getLayoutParams();
		lp.width = Constants.getScreenWidth();
		lp.height = Constants.getScreenHeight();
		iv_laungh_bg.setLayoutParams(lp);
		if (isFirst) {
			HXContactUtils.getInstance(this).requestHxInfos(0);
			loginHX();
			noFirstComein();
		} else {
			firstComein();
		}
	}

	private void loginHX() {
		Member member = new MemberDao().get();
		if (member != null && member.getSessionId() != null
				&& !TextUtils.isEmpty(member.getSessionId())) {
			HXUtils.login(
					"appmember" + AppConstants.member.getMemberId(),
					MD5Util.MD5(AppConstants.member.getMemberId() + "appmember"),
					this);
			HXContactUtils.getInstance(getApplicationContext()).requestHxInfos(
					0);
		}
	}

	private void queryStubActivity() {
		if (getIntent() != null && getIntent().getExtras() != null) {
			boolean containsKey = getIntent().getExtras()
					.containsKey("message");
			if (containsKey) {
				gotoPage = true;
			}
		}
		if (Application.stubActivityList.size() > 0) {
			for (int i = 0; i < Application.stubActivityList.size(); i++) {
				Application.stubActivityList.get(i).finish();
			}
		}
		Application.stubActivityList.clear();
	}

	// 第一次进来的启动页
	@SuppressWarnings("deprecation")
	private void firstComein() {
		tv_tiyan = (TextView) findViewById(R.id.tv_lanch_tiyan);
		tv_tiyan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mUIHandler = new Handler();
				mUIHandler.post(new Runnable() {

					@Override
					public void run() {
						Application.getInstance().postUiInit();
					}
				});
				startTimeOutListener();
			}
		});
		iv_laungh_bg.setVisibility(View.GONE);
		for (int i = 0; i < 3; i++) {
			list.add(getPicsView(i));
		}
		vp = (ViewPager) findViewById(R.id.vp_pic);
		vp.setVisibility(View.VISIBLE);
		myPageAdapter = new MyPageAdapter(list);
		vp.setAdapter(myPageAdapter);
		Editor edit = getSharedPreferences("splash_user", Context.MODE_PRIVATE)
				.edit();
		edit.putBoolean("splash_user", true);
		edit.commit();

		vp.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				if (arg0 == 2) {
					tv_tiyan.setVisibility(View.VISIBLE);
				} else {
					tv_tiyan.setVisibility(View.GONE);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	private View getPicsView(int itemPosition) {
		View viewStep = LayoutInflater.from(this).inflate(R.layout.welcom_view,
				null);
		ImageView iv = (ImageView) viewStep.findViewById(R.id.iv_welcome);
		iv.setVisibility(View.VISIBLE);
		LayoutParams lp = iv.getLayoutParams();
		lp.width = Constants.getScreenWidth();
		lp.height = Constants.getScreenHeight();
		iv.setLayoutParams(lp);
		iv.setScaleType(ScaleType.FIT_XY);
		if (itemPosition == 0) {
			iv.setImageResource(R.drawable.iv_launch_bg1);
		}
		if (itemPosition == 1) {
			iv.setImageResource(R.drawable.iv_launch_bg2);
		}
		if (itemPosition == 2) {
			iv.setImageResource(R.drawable.iv_launch_bg3);
		}
		return viewStep;
	}

	// 非第一次
	private void noFirstComein() {
		mUIHandler = new Handler();
		mUIHandler.post(new Runnable() {

			@Override
			public void run() {
				Application.getInstance().postUiInit();
			}
		});
		startTimeOutListener();
	}

	private void jumpIntoApplication() {
		// TODO: 如果需要根据登录态选择跳转目标，可以在这里添加逻辑。
		// 跳转到首页
		doInentToEntryFragment();
	}

	private void doInentToEntryFragment() {
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
			finish();
			return;
		}
		Intent ctorJmpIntent = StubActivity.ctorJmpIntent("navigation",
				extraObj.toString());
		// 需要直接到微聊页面，加个message字符串作为标识符
		if (gotoPage) {
			ctorJmpIntent.putExtra("message", "message");
		}
		startActivity(ctorJmpIntent);
		finish();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ActivityManager.getInstance().removeActivity(this);
	}

	private void startTimeOutListener() {
		long now = System.currentTimeMillis();
		long collapsed = now - mStartTime;
		if (collapsed >= TIME_OUT) {
			mUIHandler.post(mTimeoutRunnable);
		} else {
			mUIHandler.postDelayed(mTimeoutRunnable, TIME_OUT - collapsed);
		}
	}
	// 第一次进来，请求广告页，

}
