package com.dingapp.commonui.widget;

import android.content.Context;
import android.support.v4.view.PagerTabStrip;
import android.util.AttributeSet;

public class DAEPagerTabStrip extends PagerTabStrip {

	public DAEPagerTabStrip(Context context) {
		super(context);
		setDrawFullUnderline(false);
	}

	public DAEPagerTabStrip(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		setDrawFullUnderline(false);
	}

}
