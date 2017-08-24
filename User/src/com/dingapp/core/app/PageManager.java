package com.dingapp.core.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.dingapp.core.util.LoggerUtil;
import com.dingapp.core.util.UIUtil;

/**
 * 这个PageManager的作用是将StubActivity和NavigationActivity的公共操作提取出来，防止重复编码。
 */
class PageManager {
	private int mSed;
	private Handler mSalfHandler;

	private PageManager() {
		mSalfHandler = new Handler(Looper.getMainLooper());
		mSed = 0;
	}

	private static class SLocker {
		private static PageManager sPM = new PageManager();
	}

	void destroy() {
		PageProtocolManager.spageRouter.clear();
		mSed = 0;
	}

	synchronized static PageManager getInstance() {
		return SLocker.sPM;
	}

	synchronized String createUniqueTagForFragment(BaseFragment frg) {
		String tag = frg.getClass().getName() + "-" + mSed;
		mSed++;
		return tag;
	}

	void openPageWithFragmentActivity(FragmentActivity act, BaseFragment frg,
			PageJumper jmp) {
		UIUtil.hideKeyboard(act);

		if (jmp.mNewActivity) {
			LoggerUtil.e(frg.getPagename(),
					"pageManager can't open this fragment in another activity");
			return;
		} else {
			/** 只有真正入栈的时候才会分配tag */
			String tag = createUniqueTagForFragment(frg);
			frg.setFragmentTag(tag);
			doPushFragment(act, frg, jmp);
		}
	}

	private void doPushFragment(FragmentActivity act, BaseFragment frg,
			PageJumper jmp) {
		int layoutId = jmp.mContainerId;
		if (jmp.mArgs != null) {
			frg.setArguments(jmp.mArgs);
		}
		FragmentManager mgr = act.getSupportFragmentManager();
		BaseFragment previousTopFrg = getTopBaseFragment(mgr);
		FragmentTransaction transaction = mgr.beginTransaction();
		if (jmp.mAnimation != null) {
			if (jmp.mAnimation.length == 2) {
				transaction.setCustomAnimations(jmp.mAnimation[0],
						jmp.mAnimation[1]);
			} else if (jmp.mAnimation.length == 4) {
				transaction
						.setCustomAnimations(jmp.mAnimation[0],
								jmp.mAnimation[1], jmp.mAnimation[2],
								jmp.mAnimation[3]);
			}
		}

		if (previousTopFrg != null) {
			transaction.hide(previousTopFrg);
		}
		transaction.add(layoutId, frg, frg.getFragmentTag());
		transaction.addToBackStack(frg.getFragmentTag());
		transaction.commitAllowingStateLoss();
	}

	BaseFragment getTopBaseFragment(FragmentActivity act) {
		return getTopBaseFragment(act.getSupportFragmentManager());
	}

	private BaseFragment getTopBaseFragment(FragmentManager mgr) {
		BaseFragment frg = null;
		String tag = null;
		int cnt = mgr.getBackStackEntryCount();
		if (cnt > 0) {
			BackStackEntry entry = mgr.getBackStackEntryAt(cnt - 1);
			tag = entry.getName();
			frg = (BaseFragment) mgr.findFragmentByTag(tag);
		}
		return frg;
	}

	/**
	 * 如果pageName已经在栈中了，那么将pageName上面的fragment全部弹出销毁。
	 * 如果pageName没有在栈中，那么将pageName对应的fragment压入栈中。
	 * 
	 * TODO: 伪goto语法，不允许有同类的fragment在栈里。
	 * 
	 * @param pageName
	 */
	void gotoPageWithFragmentActivity(FragmentActivity act, BaseFragment frg,
			PageJumper jmp) {
		UIUtil.hideKeyboard(act);
		BaseFragment targetFrg = findTargetFrgOnStack(frg.getClass().getName());
		if (targetFrg != null) {
			removeUntilTo(targetFrg);
			if (jmp.mArgs != null) {
				targetFrg.onDataReset(jmp.mArgs);
			}
		} else {
			openPageWithFragmentActivity(act, frg, jmp);
		}
	}

	private void removeUntilTo(BaseFragment targetFrg) {
		int actCnt = ActivityManager.getInstance().getActivityCnt();
		for (int i = actCnt - 1; i >= 0; i--) {
			FragmentActivity iterAct = (FragmentActivity) ActivityManager
					.getInstance().getActivityAt(i);
			if (isFragmentInActivity(targetFrg, iterAct)) {
				final FragmentActivity runnableAct = iterAct;
				final String tag = targetFrg.getFragmentTag();
				mSalfHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						runnableAct.getSupportFragmentManager().popBackStack(
								tag, 0);
					}
				}, 100);

				break;
			} else {
				iterAct.finish();
			}
		}
	}

	private boolean isFragmentInActivity(BaseFragment frg, FragmentActivity act) {
		FragmentManager mgr = act.getSupportFragmentManager();
		return null != mgr.findFragmentByTag(frg.getFragmentTag());
	}

	BaseFragment findTargetFrgOnStack(FragmentActivity act, BaseFragment frg) {
		return findTargetFrgOnStack(frg.getClass().getName());
	}

	private BaseFragment findTargetFrgOnStack(String className) {
		BackStackEntry entry = null;
		BaseFragment targetFrg = null;
		int actCnt = ActivityManager.getInstance().getActivityCnt();
		for (int actIter = 0; actIter < actCnt; actIter++) {
			FragmentActivity act = (FragmentActivity) ActivityManager
					.getInstance().getActivityAt(actIter);
			FragmentManager iterMgr = act.getSupportFragmentManager();
			int cnt = iterMgr.getBackStackEntryCount();
			for (int i = cnt - 1; i >= 0; i--) {
				entry = iterMgr.getBackStackEntryAt(i);
				targetFrg = (BaseFragment) iterMgr.findFragmentByTag(entry
						.getName());
				if (TextUtils.equals(targetFrg.getClass().getName(), className)) {
					return targetFrg;
				}
			}
		}

		return null;
	}

	/**
	 * 将栈顶的Fragment弹出。如果弹出栈顶的fragment后，栈空了，那么结束activity。
	 * 
	 * @param baseFragment
	 */
	void popBackWithFragmentActivity(FragmentActivity act, Bundle bundle) {
		UIUtil.hideKeyboard(act);
		FragmentManager actMgr = act.getSupportFragmentManager();
		int entryCnt = actMgr.getBackStackEntryCount();
		if (entryCnt > 1) {
			actMgr.popBackStack();
			if (bundle != null) {
				BackStackEntry entry = actMgr.getBackStackEntryAt(entryCnt - 2);
				BaseFragment previousFrg = (BaseFragment) actMgr
						.findFragmentByTag(entry.getName());
				previousFrg.onDataReset(bundle);
			}
		} else {
			LoggerUtil.e("PageManager", "popback only one fragment");
		}
	}

	// BaseFragment ctorFragmentBaseIntent(Intent intent) {
	// String className = intent.getStringExtra("fragment_class");
	//
	// Class<?> clz = null;
	// try {
	// clz = Class.forName(className);
	// } catch (ClassNotFoundException e) {
	// e.printStackTrace();
	// return null;
	// }
	//
	// Object obj = null;
	// try {
	// obj = clz.newInstance();
	// } catch (Exception e) {
	// e.printStackTrace();
	// return null;
	// }
	//
	// if (!(obj instanceof BaseFragment)) {
	// return null;
	// }
	//
	// if (intent.hasExtra("fragment_tag")) {
	// ((BaseFragment)obj).setFragmentTag(intent.getStringExtra("fragment_tag"));
	// }
	//
	// return (BaseFragment)obj;
	// }

	// PageJumper ctorPageJumperBaseIntent(Intent intent) {
	// if (intent.hasExtra("page_jumper")) {
	// return intent.getParcelableExtra("page_jumper");
	// }
	//
	// return null;
	// }

	void popStackWithFragmentActivity(FragmentActivity act, Bundle bundle) {
		int actCnt = ActivityManager.getInstance().getActivityCnt();
		if (actCnt <= 1) {
			return;
		}
		if (bundle != null) {
			FragmentActivity previousAct = (FragmentActivity) ActivityManager
					.getInstance().getActivityAt(actCnt - 2);
			if ((previousAct != null) && (previousAct instanceof StubActivity)) {
				BaseFragment frg = ((StubActivity) previousAct)
						.getTopBaseFragment();
				if (frg != null) {
					frg.onDataReset(bundle);
				}
			}
		}
		act.finish();
		int[] animations = ((IPageSwitcher) act).getFirstFragmentAnimation();
		if (animations != null && animations.length == 4) {
			act.overridePendingTransition(animations[2], animations[3]);
		}
	}
}
