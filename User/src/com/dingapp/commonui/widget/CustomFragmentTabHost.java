package com.dingapp.commonui.widget;

import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.dingapp.andriod.z20.R;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.LoggerUtil;

/**
 * <pre>
 * 在系统控件基础上增加fragment支持。系统控件通过设置TabContentFactory来添加内容视图。
 * 本类有两个实现思路：
 * 第一个扩展TabContentFactory来添加视图。这种方案不能采用。
 * ?原因在于无法显示隐藏Fragment的动画。
 * 第二个是监听onTabChanged方法，在onTabChanged方法中创建fragment。
 * 
 * 第二种方案的技术关键点：
 * 1. 需要自定义ContentFactory，此Factory生产的View是类似于空操作的意思。纯属为了符合TabHost框架。
 * 2. 实现OnTabChangeListener，通过监听OnTabChanged方法来切换fragment。
 * 3. 重载setOnTabChangedListener方法，防止外界改写了OnTabChangeListener。
 * 4. 在WindowAttach状态发生变化时正确处理fragment。
 * 5. 处理好fragment的added、show、hidden、attach、detach状态。
 * 6. Tab信息有两个，第一个是TabHost自己定义的iconSpec，另一个是本类引入的frgmentSpec。处理好这两个spec的赢谁关系。
 * </pre>
 * 
 * @author panda
 */
public class CustomFragmentTabHost extends TabHost {
	/**
	 * 用于切换fragment，在切换tab时，这个值记录当前show &&
	 * attach的fragment。与TabHost维护的currentIdx没有对应关系。
	 */
	private String mCurrentFrgTag = null;
	/** 用于计算动画类型。当切换fragment时，如果旧焦点fragment位置在新焦点fragment的左边，应该做左滑动画。反之，做右滑动画。 */
	private int mCurrentFrgIndex = -1;
	private IFragmentFactory mFragmentFactory;
	private HashMap<String, FragmentInfo> mFrgMap = new HashMap<String, FragmentInfo>();
	private FragmentActivity mFragmentActivity;
	private boolean mAttachedToWindow = false;

	private OnTabChangeListener mOnTabChangedListener;

	public CustomFragmentTabHost(Context context) {
		super(context);
	}

	public CustomFragmentTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 本方法会使用frgAct来获得FragmentManager，管理Fragment。
	 * 
	 * @param frgAct
	 */
	public void setFragmentActivity(FragmentActivity frgAct) {
		mFragmentActivity = frgAct;
		super.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				doOnTabChanged(tabId);
			}
		});
	}

	/**
	 * tab与fragment的映射关系完全外部使用者控制。 当tabhost需要新建fragment时，会使用factory来创建Fragment。
	 * 
	 * @param factory
	 */
	public void setFragmentFactory(IFragmentFactory factory) {
		mFragmentFactory = factory;
	}

	/**
	 * 父类TabHost完成的工作有： 1. 将前一个假content view 关闭 2. 切换tab icon的selected status 3.
	 * 将后一个假content view 显示出来（如果需要，会将content view添加到视觉树中）。
	 * 
	 * 剩余的工作有： 1. 将前一个fragment hide 2. 将后一个fragment add ｜ show 3.
	 * 通知onTabChangedListener
	 */
	private void doOnTabChanged(String tabId) {
		// 有效性验证
		if (TextUtils.isEmpty(tabId) || !mFrgMap.containsKey(tabId)) {
			return;
		}

		if (mAttachedToWindow) {
			doChangeFragment(tabId);
		}

		// 通知TabChanged
		if (mOnTabChangedListener != null) {
			mOnTabChangedListener.onTabChanged(tabId);
		}
	}

	private void doChangeFragment(String tabId) {
		FragmentManager fm = mFragmentActivity.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		int slide_left_in = R.anim.alpha_in;
		int slide_left_out = R.anim.alpha_out;
		int slide_right_in = R.anim.alpha_in;
		int slide_right_out = R.anim.alpha_out;

		// 将前一个fragment hide
		if (!TextUtils.isEmpty(mCurrentFrgTag)
				&& mFrgMap.containsKey(mCurrentFrgTag)) {
			// 如果当前切换操作不是初始化操作，需要添加动画。换句话说，初始化操作不需要动画，切换操作需要动画。
			int newIdx = getCurrentTab();
			LoggerUtil.d("tab host", "oldIdx : \"" + mCurrentFrgIndex
					+ "\", newIdx : \"" + newIdx + "\"");
			if (mCurrentFrgIndex < newIdx) {
				ft.setCustomAnimations(slide_left_in, slide_left_out);
			} else {
				ft.setCustomAnimations(slide_right_in, slide_right_out);
			}

			FragmentInfo previousFrgInfo = mFrgMap.get(mCurrentFrgTag);
			BaseFragment frg = previousFrgInfo.mFrg;
			if (frg.isAdded()) {
				if (!frg.isHidden()) {
					ft.hide(frg);
				}
			}
		}

		// 将后一个fragment attach && show
		FragmentInfo nextFrgInfo = mFrgMap.get(tabId);
		BaseFragment nextFrg = nextFrgInfo.mFrg;
		if (nextFrg == null) {
			nextFrg = mFragmentFactory.createFragment(tabId, nextFrgInfo.mArgs);
			nextFrgInfo.mFrg = nextFrg;
			if (nextFrg != null) {
				nextFrg.setTabHost(this);
			}
		}
		if (!nextFrg.isAdded()) {
			if (nextFrgInfo.mArgs != null) {
				nextFrg.setArguments(nextFrgInfo.mArgs);
			}
			ft.add(android.R.id.tabcontent, nextFrg);
		} else {
			if (nextFrg.isHidden()) {
				ft.show(nextFrg);
			} else {
				LoggerUtil.e("tab host", "\"next Tab idx: \"" + getCurrentTab()
						+ "\" has added and shown!!!" + " className is \""
						+ nextFrg.getClass().getName() + "\"");
			}
		}
		mCurrentFrgIndex = getCurrentTab();
		mCurrentFrgTag = tabId;
		//允许状态丢失，负责会报java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
		try {
			ft.commit();
		} catch (Exception e) {
			ft.commitAllowingStateLoss();
		}
//		ft.commit();
	}

	@Override
	public void setOnTabChangedListener(OnTabChangeListener l) {
		mOnTabChangedListener = l;
	}

	public OnTabChangeListener getOnTabChangedListener() {
		return mOnTabChangedListener;
	}

	public void addTab(TabSpec tabSpec, Bundle bundle) {
		FragmentInfo frgInfo = new FragmentInfo();
		frgInfo.mArgs = bundle;
		mFrgMap.put(tabSpec.getTag(), frgInfo);

		FragmentContentFactory factory = new FragmentContentFactory();
		tabSpec.setContent(factory);
		addTab(tabSpec);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		mAttachedToWindow = true;

		if (mCurrentFrgIndex == getCurrentTab()) {
			return;
		}

		String targetTag = getCurrentTabTag();
		doChangeFragment(targetTag);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mAttachedToWindow = false;
	}

	/**
	 * <pre>
	 * tabhost可以被fragment使用。简称使用tabhost的fragment为TabHostFragment。
	 * 如果其他fragment需要直接跳转到TabHostFragment指定的tab，那么需要使用这个方法。
	 * 实际场景应该类似于：
	 * 1. 获得其他fragment传递的模板tabid
	 * 2. getFragmentByTabId(tabid) 获得tabhost对应的fragment。
	 * 3. fragment.onDataReset(新参数)
	 * </pre>
	 * 
	 * @param id
	 * @return
	 */
	public BaseFragment getFragmentByTabId(String id) {
		if (mFrgMap.containsKey(id)) {
			return mFrgMap.get(id).mFrg;
		}

		return null;
	}

	/**
	 * 这个方法用于框架。 使用场景是： 1. TabHostFragment从隐藏状态变为显示状态，或者从显示状态变为隐藏状态。 2.
	 * 框架调用TabHostFragment.onHiddenChanged 3. TabHostFragment获得当前tab 4.
	 * 当前tab中的fragment执行onHiddenChanged
	 * 
	 * @return
	 */
	public BaseFragment getCurrentFragment() {
		if (TextUtils.isEmpty(mCurrentFrgTag)) {
			return null;
		}
		FragmentInfo info = mFrgMap.get(mCurrentFrgTag);
		if (info == null) {
			return null;
		}
		return mFrgMap.get(mCurrentFrgTag).mFrg;
	}

	/**
	 * 为了实现小红点逻辑。 当某个tab需要处理新消息时，tab按钮上需要显示一种特殊的标记，如小红点。
	 * TabHostFragment可以使用这个接口获得任何一个tab按钮。 然后可以更改标记的可见状态。
	 * 
	 * @param idx
	 * @return
	 */
	public View getTabView(int idx) {
		TabWidget tw = getTabWidget();
		int cnt = tw.getChildCount();
		if (idx >= 0 && idx < cnt) {
			return tw.getChildAt(idx);
		}
		return null;
	}

	public class FragmentContentFactory implements TabContentFactory {
		@Override
		public View createTabContent(String tag) {
			// 下面的view是没有任何作用的，tab host 的content位置显示的内容由fragment确定
			View v = new View(getContext());
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}
	}

	public static interface IFragmentFactory {
		public BaseFragment createFragment(String tabTag, Bundle bundle);
	}

	private class FragmentInfo {
		BaseFragment mFrg;
		Bundle mArgs;
	}

	public void refreashCurrentTab(Bundle bundle) {
		FragmentManager fm = mFragmentActivity.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		FragmentInfo previousFrgInfo = mFrgMap.get(mCurrentFrgTag);
		BaseFragment frg = previousFrgInfo.mFrg;
		ft.hide(frg);
		ft.remove(frg);

		BaseFragment nextFrg = mFragmentFactory.createFragment(mCurrentFrgTag,
				bundle);
		nextFrg.setArguments(bundle);
		previousFrgInfo.mFrg = nextFrg;
		nextFrg.setTabHost(this);
		nextFrg.setArguments(previousFrgInfo.mArgs);
		ft.add(android.R.id.tabcontent, nextFrg);
		ft.commit();
	}
}
