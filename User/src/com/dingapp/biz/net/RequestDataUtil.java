package com.dingapp.biz.net;

import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.orm.ImageBean;
import com.dingapp.core.util.AndroidUtil;
import com.dingapp.core.util.UIUtil;

public class RequestDataUtil {
	private static RequestDataUtil sInstance;

	private RequestDataUtil() {

	}

	public static RequestDataUtil getRequestInstance() {
		if (sInstance == null) {
			sInstance = new RequestDataUtil();
		}
		return sInstance;
	}

	public boolean requestData(Listener<String> listener,
			HashMap<String, String> postMap, String url, Context context,
			List<ImageBean> imageList, String isNeedLogin) {
		if(context == null){
			return false;
		}
		RequestQueue mQueue = SingleRequestQueue.getreRequestQueue(context
				.getApplicationContext());
		// 判断是否需要登录、
		if (postMap != null) {
			postMap.put("platform", AppConstants.PLATFORM);
			if (isNeedLogin.equals("true")) {
				if (AppConstants.member.getSessionId() != null
						&& !AppConstants.member.getSessionId().equals("")) {
					postMap.put("session_id",
							AppConstants.member.getSessionId());
				} else {
					return true;
				}
			} else if (isNeedLogin.equals("normal")) {
				// 可传sessionId也可不传。
				if (AppConstants.member.getSessionId() != null
						&& !AppConstants.member.getSessionId().equals("")) {
					postMap.put("session_id",
							AppConstants.member.getSessionId());
				}
			}
		}
		if (AndroidUtil.isNetworkAvailable(context)) {
			StringPostRequest request = null;
			// 有图片的网络请求
			if (imageList != null) {
				request = new StringPostRequest(postMap, AppConstants.BaseUrl
						+ url, listener, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

					}
				}, imageList);
			} else {
				request = new StringPostRequest(postMap, AppConstants.BaseUrl
						+ url, listener, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
					}
				});
			}
			mQueue.add(request);
		} else {
			UIUtil.showToast(context, AppConstants.NetNotifice);
		}
		return false;
	}
}
