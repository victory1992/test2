package com.dingapp.biz.pay.weixin;

import java.lang.ref.WeakReference;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 接收微信支付结果的receiver;
 * 
 * @author
 * 
 */
public class WxPayReceiver extends BroadcastReceiver {
	private WeakReference<WxCallback> ref;

	public WxPayReceiver(WxCallback act) {
		ref = new WeakReference<WxCallback>(act);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		WxCallback call = ref.get();
		if (call == null) {
			return;
		}
		call.callback();
	}

	public static interface WxCallback {
		public void callback();
	}
}