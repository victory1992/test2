package com.dingapp.biz.page.customview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
//主要是处理在图片放大缩小时报的异常
public class MyViewPager extends ViewPager {
	public MyViewPager(Context context) {
		super(context);
	}

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		try {
			return super.onTouchEvent(arg0);
		} catch (IllegalArgumentException e) {
			
		}
		return false;
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		try {
			return super.onInterceptTouchEvent(arg0);
		} catch (IllegalArgumentException e) {
			
		}
		return false;
		
	}
}
