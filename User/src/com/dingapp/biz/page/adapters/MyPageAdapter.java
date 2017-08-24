package com.dingapp.biz.page.adapters;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class MyPageAdapter extends PagerAdapter{
	private List<View>views;
	public MyPageAdapter(List<View>views){
		this.views = views;
	}
	@Override
	public int getCount() {
		return views==null?0:views.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(views.get(position));
		return views.get(position);
	}
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(views.get(position));
	}

}
