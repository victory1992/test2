package com.dingapp.core.app;

import java.net.URI;
import java.net.URLDecoder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TabHost.OnTabChangeListener;

import com.dingapp.andriod.z20.R;
import com.dingapp.commonui.widget.CustomFragmentTabHost;

public class BaseFragment extends Fragment implements ITabHostFragment {
	private int mColumnId;
	IPageSwitcher mPageSwitcher;
	private String mFragmentTag;
	private CustomFragmentTabHost mTabHost;

	protected boolean isActivityFinished() {
		return getActivity() == null || getActivity().isFinishing();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			popBack(null);
			return true;
		}

		return false;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mPageSwitcher = (IPageSwitcher) getActivity();
	}

	/**
	 * 直接将pageName对应的Fragment压入栈中。
	 * 
	 * @param pageName
	 */
	public void openPage(String pageName, Bundle bundle, boolean newActivity) {
		addDefaultParameters(bundle);
		PageJumper jmp = constructPageJumper(bundle, newActivity);
		mPageSwitcher.openPage(pageName, jmp);
	}

	private PageJumper constructPageJumper(Bundle bundle, boolean newActivity) {
		PageJumper jmp = new PageJumper();
		jmp.mNewActivity = newActivity;
		jmp.mArgs = bundle;
		if (bundle != null && bundle.containsKey("containerId")) {
			jmp.mContainerId = bundle.getInt("containerId");
		} else {
			jmp.mContainerId = R.id.fragment_container;
		}
		jmp.mAnimation = getPageAnimation();
		return jmp;
	}

	protected int[] getPageAnimation() {
		Integer enterIn = R.anim.page_in;
		Integer enterOut = R.anim.page_out;
		Integer popIn = R.anim.pop_in;
		Integer popOut = R.anim.pop_out;

		return new int[] { enterIn.intValue(), enterOut.intValue(),
				popIn.intValue(), popOut.intValue() };
	}

	private void addDefaultParameters(Bundle bundle) {
		if (bundle != null && !bundle.containsKey("columnId")) {
			bundle.putInt("columnId", getColumnId());
		}
	}

	/**
	 * 如果pageName已经在栈中了，那么将pageName上面的fragment全部弹出销毁。
	 * 如果pageName没有在栈中，那么将pageName对应的fragment压入栈中。
	 * 
	 * @param pageName
	 */
	protected void gotoPage(String pageName, Bundle bundle, boolean newActivity) {
		addDefaultParameters(bundle);
		PageJumper jmp = constructPageJumper(bundle, newActivity);
		// pageName ==> fragment instance
		// bundle ==> fragment args
		// layerId ==> android 特有的
		mPageSwitcher.gotoPage(pageName, jmp);
	}

	protected void openPage(String destUrl, boolean newActivity) {
		PageJumper jmp = calcJmper(destUrl, newActivity);
		String pageName = calcPageName(destUrl);
		mPageSwitcher.openPage(pageName, jmp);
	}

	private PageJumper calcJmper(String destUrl, boolean newActivity) {
		URI uri = URI.create(destUrl);
		String query = uri.getQuery();
		if (query == null || TextUtils.isEmpty(query)) {
			query = "%7B%22%22%7D";
		}
		String params = URLDecoder.decode(query.substring("params=".length()));
		Bundle bundle = new Bundle();
		bundle.putString("jsonParams", params);
		addDefaultParameters(bundle);
		return constructPageJumper(bundle, newActivity);
	}

	private String calcPageName(String destUrl) {
		URI uri = URI.create(destUrl);
		return uri.getAuthority();
	}

	/**
	 * 将栈顶的Fragment弹出。如果弹出栈顶的fragment后，栈空了，那么退出客户端。
	 */
	protected void popBack(Bundle bundle) {
		mPageSwitcher.popBack(bundle);
	}

	protected void popStack(Bundle bundle) {
		mPageSwitcher.popStack(bundle);
	}

	/**
	 * 当fragment从非栈顶变为栈顶时，收到了新参数，那么调用本接口设置参数。
	 * 
	 * @param bundle
	 */
	protected void onDataReset(Bundle bundle) {

	}

	protected int getColumnId() {
		return mColumnId;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (getArguments() != null && getArguments().containsKey("columnId")) {// for
																				// entry
																				// fragment
																				// &
																				// setting
																				// fragment...
			mColumnId = getArguments().getInt("columnId");
		}
	}

	protected BaseFragment getTopBaseFragment() {
		return mPageSwitcher.getTopBaseFragment();
	}

	public void setFragmentTag(String tag) {
		mFragmentTag = tag;
	}

	public String getFragmentTag() {
		return mFragmentTag;
	}

	@Override
	public void setTabHost(CustomFragmentTabHost tabHost) {
		mTabHost = tabHost;
	}

	@Override
	public void switchTabColumTo(int idx, Bundle bundle) {
		if (mTabHost == null) {
			return;
		}
		final OnTabChangeListener previousListener = mTabHost
				.getOnTabChangedListener();
		final Bundle dataResetBundle = bundle;
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				mTabHost.getCurrentFragment().onDataReset(dataResetBundle);
				if (previousListener != null) {
					previousListener.onTabChanged(tabId);
					mTabHost.setOnTabChangedListener(previousListener);
				} else {
					mTabHost.setOnTabChangedListener(null);
				}
			}
		});
		mTabHost.setCurrentTab(idx);
	}

	protected void refreashCurrentTab(Bundle bundle) {
		if (mTabHost == null) {
			return;
		}
		mTabHost.refreashCurrentTab(bundle);
	}

	// 这个是识别页面名称的重要标签，必须有而且不能被混淆。
	public String getPagename() {
		return "base";
	}
}