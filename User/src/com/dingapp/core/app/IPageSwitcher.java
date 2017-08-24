package com.dingapp.core.app;

import android.os.Bundle;

/**
 * <pre>
 * page 协议的架构是：
 * 
 * BaseFragment
 *      ||
 *      \/
 * IPageSwitcher  <===  provided by StubActivity
 *      ||
 *      \/
 * PageManager
 * </pre>
 * 
 * @author panda
 */
public interface IPageSwitcher {
	/** 打开一个新的fragment */
	public void openPage(String pageName, PageJumper jmp);

	/** 根据URL跳转页面 */
	// public void openPage(String url);
	/**
	 * 如果栈中存在fragment，那么将fragment之上的所有fragment都弹出。如果栈中不存在fragment，
	 * 那么向栈中压入一个新的fragment。
	 */
	public void gotoPage(String pageName, PageJumper jmp);

	/** 把栈顶的fragment弹出去 */
	public void popBack(Bundle bundle);

	/** 获得栈顶的fragment */
	public BaseFragment getTopBaseFragment();

	/** 弹出子栈 */
	public void popStack(Bundle bundle);

	/** 获取第一个fragment的进入动画 */
	int[] getFirstFragmentAnimation();
}
