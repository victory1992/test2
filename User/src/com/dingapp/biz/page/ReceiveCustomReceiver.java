package com.dingapp.biz.page;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class ReceiveCustomReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(action=="update"){
			update(intent);
		}
	}
	public abstract void update(Intent intent);

}
