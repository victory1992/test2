package com.dingapp.biz.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.view.View;
import android.widget.TextView;

public class ChechWifyState extends BroadcastReceiver {
	private TextView tv_nowify;

	public ChechWifyState(TextView tv_nowify) {
		super();
		this.tv_nowify = tv_nowify;
	}

	public ChechWifyState() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		State wifiState = null;
		State mobileState = null;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED == mobileState) {
			if (tv_nowify != null) {
				tv_nowify.setVisibility(View.GONE);
			}

			// 手机网络连接成功
		} else if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED != mobileState) {
			// 手机没有任何的网络
			if (tv_nowify != null) {
				tv_nowify.setVisibility(View.VISIBLE);
			}

		} else if (wifiState != null && State.CONNECTED == wifiState) {
			// 无线网络连接成功
			if (tv_nowify != null) {
				tv_nowify.setVisibility(View.GONE);
			}

		}

	}
}
