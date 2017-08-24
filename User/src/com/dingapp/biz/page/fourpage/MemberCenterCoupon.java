package com.dingapp.biz.page.fourpage;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dingapp.andriod.z20.R;
import com.dingapp.biz.page.fourpage.MemberCenterCouponListFragment.OnRefreshCouponCountListener;
import com.dingapp.core.app.BaseFragment;

public class MemberCenterCoupon extends BaseFragment implements OnClickListener {
	private TextView tv_not_used;
	private TextView tv_used;
	private TextView tv_overdued;
	private ImageView img_back;
	private MemberCenterCouponListFragment currentFragment;
	private int positon;
	private View view_1;
	private View view_2;
	private View view_3;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return View.inflate(getActivity(), R.layout.member_center_coupon, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initListener();
		// 初始化第一次数据
		initTextColor(tv_not_used);
		showLine(0);
		replace(0);
	}

	private void initListener() {
		tv_not_used.setOnClickListener(this);
		tv_used.setOnClickListener(this);
		tv_overdued.setOnClickListener(this);
		img_back.setOnClickListener(this);
	}

	private void initView() {
		tv_not_used = (TextView) getView().findViewById(R.id.tv_not_used);
		tv_used = (TextView) getView().findViewById(R.id.tv_used);
		tv_overdued = (TextView) getView().findViewById(R.id.tv_overdued);
		view_1 = (View) getView().findViewById(R.id.view_1);
		view_2 = (View) getView().findViewById(R.id.view_2);
		view_3 = (View) getView().findViewById(R.id.view_3);
		view_1.setVisibility(View.VISIBLE);
		view_2.setVisibility(View.INVISIBLE);
		view_3.setVisibility(View.INVISIBLE);
		img_back = (ImageView) getView().findViewById(R.id.img_back);
	}

	@Override
	public void onClick(View v) {
		if (v == tv_overdued) {
			if (positon == 1) {
				return;
			}
			positon = 1;
			initTextColor(tv_overdued);
			showLine(1);
			hideCurrentFragment(currentFragment);
			replace(1);
			return;
		} else if (v == tv_not_used) {
			if (positon == 0) {
				return;
			}
			positon = 0;
			initTextColor(tv_not_used);
			showLine(0);
			hideCurrentFragment(currentFragment);
			replace(0);
			return;
		} else if (v == tv_used) {
			if (positon == 2) {
				return;
			}
			positon = 2;
			initTextColor(tv_used);
			showLine(2);
			hideCurrentFragment(currentFragment);
			replace(2);
			return;
		} else if (v == img_back) {
			popBack(null);
			return;
		}
	}

	private void showLine(int i) {
		if (i == 0) {
			view_1.setVisibility(View.VISIBLE);
			view_2.setVisibility(View.INVISIBLE);
			view_3.setVisibility(View.INVISIBLE);
		} else if (i == 1) {
			view_1.setVisibility(View.INVISIBLE);
			view_2.setVisibility(View.VISIBLE);
			view_3.setVisibility(View.INVISIBLE);
		} else if (i == 2) {
			view_1.setVisibility(View.INVISIBLE);
			view_2.setVisibility(View.INVISIBLE);
			view_3.setVisibility(View.VISIBLE);
		}
	}

	private void initTextColor(TextView textview) {
		tv_not_used.setTextColor(0xff808080);
		tv_used.setTextColor(0xff808080);
		tv_overdued.setTextColor(0xff808080);
		textview.setTextColor(0xfff66e00);
	}

	private void hideCurrentFragment(BaseFragment fg) {
		if (fg != null) {
			FragmentManager fManager = getChildFragmentManager();
			FragmentTransaction trans = fManager.beginTransaction();
			trans.hide(fg);
			trans.commit();
		}
	}

	private void replace(int position) {
		if (hashMap.containsKey(position)) {
			FragmentManager fManager = getChildFragmentManager();
			FragmentTransaction trans = fManager.beginTransaction();
			trans.show(hashMap.get(position));
			trans.commit();
		} else {
			FragmentManager fManager = getChildFragmentManager();
			FragmentTransaction trans = fManager.beginTransaction();
			trans.add(R.id.fl_coupon_content, getFragment(position));
			trans.commit();
		}
		currentFragment = hashMap.get(position);
		currentFragment
				.setOnRefreshCouponCountListener(new OnRefreshCouponCountListener() {

					@Override
					public void refresh(int[] counts) {
						initCount(counts);
					}
				});
		int[] couponCount = currentFragment.getCouponCount();
		initCount(couponCount);
	}

	/**
	 * @param couponCount
	 *            初始化 优惠券数量
	 */
	private void initCount(int[] couponCount) {
		if (couponCount != null) {
			tv_not_used.setText("未使用(" + couponCount[0] + ")");
			tv_used.setText("已使用(" + couponCount[1] + ")");
			tv_overdued.setText("已过期(" + couponCount[2] + ")");
		}
	}

	private Map<Integer, MemberCenterCouponListFragment> hashMap = new HashMap<Integer, MemberCenterCouponListFragment>();

	private BaseFragment getFragment(int position) {
		MemberCenterCouponListFragment fg = null;
		switch (position) {
		case 0:
			// 可用优惠券
			fg = new MemberCenterCouponListFragment();
			Bundle bundle = new Bundle();
			bundle.putString("status", "available");
			fg.setArguments(bundle);
			break;
		case 1:
			// 过期优惠券
			fg = new MemberCenterCouponListFragment();
			Bundle bundle2 = new Bundle();
			bundle2.putString("status", "expired");
			fg.setArguments(bundle2);
			break;
		case 2:
			// 已使用优惠券
			fg = new MemberCenterCouponListFragment();
			Bundle bundle3 = new Bundle();
			bundle3.putString("status", "used");
			fg.setArguments(bundle3);
			break;

		}
		hashMap.put(position, fg);
		return fg;
	}
}
