package com.dingapp.biz.net;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import android.content.Context;

public class SingleRequestQueue {
	private static RequestQueue mQueue;

	private SingleRequestQueue(Context context) {
		mQueue = Volley.newRequestQueue(context);
	}

	public static synchronized RequestQueue getreRequestQueue(Context context) {
		if (mQueue == null) {
			new SingleRequestQueue(context.getApplicationContext());
		}
		return mQueue;
	}
}
