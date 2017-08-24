package com.dingapp.core.app;

import android.os.Bundle;

import com.dingapp.commonui.widget.CustomFragmentTabHost;

/**
 * 如果Fragment作为TabHost的子Fragment，那么必须实现ITabHostFragment。
 * 
 * @author panda
 * 
 */
public interface ITabHostFragment {
	public void setTabHost(CustomFragmentTabHost tabHost);

	public void switchTabColumTo(int idx, final Bundle bundle);
}
