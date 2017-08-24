package com.dingapp.biz.util;

import view.refresh.PullToRefreshBase;
import view.refresh.PullToRefreshBase.Mode;

public class StopRefresh {
	public static void stopRefreash(final PullToRefreshBase<?> refreshView) {
		refreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				refreshView.onRefreshComplete();
			}
		}, 2000);
	}

	public static void initRefreshView(PullToRefreshBase<?> refreshView,
			Mode mode) {
		refreshView.setMode(mode);
		refreshView.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载...");
		refreshView.getLoadingLayoutProxy(false, true).setRefreshingLabel(
				"正在加载...");
		refreshView.getLoadingLayoutProxy(false, true).setReleaseLabel(
				"松开加载更多...");
		// 设置PullRefreshListView下拉加载时的加载提示
		refreshView.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新...");
		refreshView.getLoadingLayoutProxy(true, false).setRefreshingLabel(
				"正在加载...");
		refreshView.getLoadingLayoutProxy(true, false).setReleaseLabel(
				"松开刷新...");
//		Drawable drawable = Application.getInstance().getResources()
//				.getDrawable(R.drawable.a);
//		
//		refreshView.getLoadingLayoutProxy(true, false).setLoadingDrawable(
//				drawable);
//		refreshView.getLoadingLayoutProxy(false, true).setLoadingDrawable(
//				drawable);
	}

}
