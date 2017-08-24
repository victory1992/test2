package com.dingapp.core.app;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.dingapp.andriod.z20.R;
import com.dingapp.commonui.widget.CustomFragmentTabHost;
import com.dingapp.commonui.widget.CustomFragmentTabHost.IFragmentFactory;
import com.dingapp.core.util.BroadCastUtil;
import com.dingapp.core.util.Constants;
import com.dingapp.core.util.UIUtil;

public class NavigationFragment extends BaseFragment {
	// 必须传递一个页面列表，以逗号分隔。
	public static String sPageNames = "pages";
	// 必须传递一个栏目名称列表，以逗号分隔。
	public static String sTabNames = "tabNames";
	// 传递一个栏目正常图标列表，以逗号分隔。可以为空。
	public static String sNormalIcons = "normalIcons";
	// 传递一个栏目焦点图标列表，以逗号分隔。可以为空。
	public static String sFocusIcons = "focusIcons";
	// 默认显示的页面，可以不传
	public static String sCurrentPage = "currIdx";

	private CustomFragmentTabHost tabhost;
	private LayoutInflater mInflater;
	private String[] mArrPages;
	private String[] mArrTabNames;
	private String[] mFocusedIcons;
	private String[] mNormalIcons;
	private BroadcastReceiver mPushMsgReceiver;
	private LinearLayout ll_container;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.navigation, null);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(StubActivity.SAVE_TAG, true);
		outState.putInt("columnCount", mArrPages.length);
		for (int i = 0; i < mArrPages.length; i++) {
			outState.putString("page_" + i, mArrPages[i]);
			outState.putString("tab_" + i, mArrTabNames[i]);
			outState.putString("normal_" + i, mNormalIcons[i]);
			outState.putString("focused_" + i, mFocusedIcons[i]);
		}
		outState.putInt(sCurrentPage, tabhost.getCurrentTab());
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initArguments(savedInstanceState);
		mInflater = LayoutInflater.from(getActivity());
		tabhost = (CustomFragmentTabHost) getView().findViewById(R.id.tabhost);
		tabhost.setFragmentActivity(getActivity());
		tabhost.setFragmentFactory(new IFragmentFactory() {

			@Override
			public BaseFragment createFragment(String tabTag, Bundle bundle) {
				return doCreateFragmentByTabTag(tabTag, bundle);
			}
		});
		tabhost.setup();
		// 保证动画流畅
		getView().postDelayed(new Runnable() {
			@Override
			public void run() {
				delayInitUi();
			}
		}, Constants.sAnimDuration);
	}

	private void delayInitUi() {
		if (!UIUtil.isActValid(getActivity())) {
			return;
		}
		for (int i = 0; i < mArrPages.length; i++) {
			addTab(i);
		}
		if (getArguments().containsKey(sCurrentPage)) {
			final int idx = getArguments().getInt(sCurrentPage);
			// 延迟跳转页面，保证动画完整。
			getView().postDelayed(new Runnable() {

				@Override
				public void run() {
					if (!UIUtil.isActValid(getActivity())) {
						return;
					}
					tabhost.setCurrentTab(idx);
				}
			}, Constants.sAnimDuration);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		IntentFilter broadCastIntent = new IntentFilter(BroadCastUtil.PushMsg);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				mPushMsgReceiver, broadCastIntent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mPushMsgReceiver != null) {
			LocalBroadcastManager.getInstance(getActivity())
					.unregisterReceiver(mPushMsgReceiver);
		}
	}

	private void addTab(int idx) {
		TabSpec spec = tabhost.newTabSpec("tabSpec" + idx);
		spec.setIndicator(createView(idx));// icon
		Bundle bundle = new Bundle();
		bundle.putString("title", mArrTabNames[idx]);
		bundle.putInt("pos", idx);
		tabhost.addTab(spec, bundle);
	}

	private View createView(int idx) {
		View tabView = mInflater.inflate(R.layout.tab_item_view, null);
		ImageView imageView = (ImageView) tabView
				.findViewById(R.id.tabImageView);
		String normalDrawableUrl = mNormalIcons[idx];
		String focusedDrawableUrl = mFocusedIcons[idx];
		Drawable normalDr = UIUtil.ctorDrawable(getResources(),
				normalDrawableUrl);
		Drawable focusedDr = UIUtil.ctorDrawable(getResources(),
				focusedDrawableUrl);
		StateListDrawable drawable = new StateListDrawable();
		drawable.addState(new int[] { android.R.attr.state_selected },
				focusedDr);
		drawable.addState(new int[] {}, normalDr);
		imageView.setImageDrawable(drawable);
		
		ll_container = (LinearLayout)tabView.findViewById(R.id.ll_container);
//		StateListDrawable lldrawable = new StateListDrawable();
//		Drawable ll_selected = null;
////		Drawable ll_normal = getResources().getDrawable(R.drawable.shape_tab_normal);
//		if(idx == 0){
//			ll_selected = getResources().getDrawable(R.drawable.tab_0_selected);
//		}else if(idx ==1){
//			ll_selected = getResources().getDrawable(R.drawable.tab_2_selected);
//		}else if(idx == 2){
//			ll_selected = getResources().getDrawable(R.drawable.tab_2_selected);
//		}else if(idx == 3){
//			ll_selected = getResources().getDrawable(R.drawable.tab_3_selected);
//		}
//		lldrawable.addState(new int[]{android.R.attr.state_selected},ll_selected);
//		ll_container.setBackground(lldrawable);
		TextView textView = (TextView) tabView.findViewById(R.id.tabTextView);
		textView.setText(mArrTabNames[idx]);
		return tabView;
	}

	private void initArguments(Bundle savedInstanceState) {
		if (savedInstanceState == null
				|| !savedInstanceState.containsKey(StubActivity.SAVE_TAG)) {
			Bundle args = getArguments();
			String pages = args.getString(sPageNames);
			String tabNames = args.getString(sTabNames);
			String focusedIcons = args.getString(sFocusIcons);
			String normalIcons = args.getString(sNormalIcons);
			mArrPages = pages.split(",");
			mArrTabNames = tabNames.split(",");
			mFocusedIcons = focusedIcons.split(",");
			mNormalIcons = normalIcons.split(",");
		} else {
			int len = savedInstanceState.getInt("columnCount");
			mArrPages = new String[len];
			mArrTabNames = new String[len];
			mFocusedIcons = new String[len];
			mNormalIcons = new String[len];
			for (int i = 0; i < len; i++) {
				mArrPages[i] = savedInstanceState.getString("page_" + i);
				mArrTabNames[i] = savedInstanceState.getString("tab_" + i);
				mNormalIcons[i] = savedInstanceState.getString("normal_" + i);
				mFocusedIcons[i] = savedInstanceState.getString("focused_" + i);
			}
		}
	}

	private BaseFragment doCreateFragmentByTabTag(String tabTag, Bundle mArgs) {
		BaseFragment frg = null;
		int idx = -1;
		try {
			idx = Integer.parseInt(tabTag.substring(7));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		}
		if (idx < 0 || idx >= mArrPages.length) {
			return null;
		}
		frg = PageProtocolManager.translatePageNameToFrg(mArrPages[idx],"");
		return frg;
	}

	@Override
	protected void onDataReset(Bundle bundle) {
		super.onDataReset(bundle);
		if (bundle.containsKey(sCurrentPage)) {
			final int idx = bundle.getInt(sCurrentPage);
			if (idx != tabhost.getCurrentTab()) {
				// 延迟跳转页面，保证动画完整。
				getView().postDelayed(new Runnable() {

					@Override
					public void run() {
						if (!UIUtil.isActValid(getActivity())) {
							return;
						}
						tabhost.setCurrentTab(idx);
					}
				}, Constants.sAnimDuration);
			}
		}
		BaseFragment frg = tabhost.getCurrentFragment();
		if (frg == null) {
			return;
		}
		frg.onDataReset(bundle);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
		}
		BaseFragment frg = tabhost.getCurrentFragment();
		if (frg != null) {
			frg.onHiddenChanged(hidden);
		}
	}

	// 这个是识别页面名称的重要标签，必须有而且不能被混淆。
	public String getPagename() {
		return "navigation";
	}
}
