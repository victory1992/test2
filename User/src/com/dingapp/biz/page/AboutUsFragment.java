package com.dingapp.biz.page;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.net.RequestDataUtil;
import com.dingapp.biz.util.WebViewUtils;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.AndroidUtil;
import com.dingapp.core.util.LoggerUtil;
import com.dingapp.core.util.UIUtil;

public class AboutUsFragment  extends BaseFragment{
	private ImageView iv_back;
	private TextView tv_aboutus_version;
	private WebView wv;
	private Listener<String> listener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parseAddressData(response);
		}
	};
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.about_us, null);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		iv_back = (ImageView) getView().findViewById(R.id.iv_aboutus_back);
		tv_aboutus_version = (TextView) getView().findViewById(R.id.tv_aboutus_version);
		wv = (WebView) getView().findViewById(R.id.webview);
		WebViewUtils.initWebView(wv);
		String  versionName = getAppVersionName(getActivity());
		if(versionName!=null&&!TextUtils.isEmpty(versionName)){
			tv_aboutus_version.setText("版本号: "+versionName);
		}
		iv_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popBack(null);
			}
		});
		requestAddressData();
	}
	/** 
	 * 返回当前程序版本名 
	 */  
	public String getAppVersionName(Context context) {  
	    String versionName = "";  
	    try {  
	        // ---get the package info---  
	        PackageManager pm = context.getPackageManager();  
	        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);  
	        versionName = pi.versionName;  
	        if (versionName == null || versionName.length() <= 0) {  
	            return "";  
	        }  
	    } catch (Exception e) {  
	    }  
	    return versionName;  
	} 
	private void requestAddressData() {
		String url = "/api/v1/member/about_us";
		HashMap<String, String> postMap = new HashMap<String, String>();
		RequestDataUtil.getRequestInstance().requestData(listener, postMap,
				url, getActivity(), null, "false");
	}

	protected void parseAddressData(String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			String statusCode = jsonObject.getString("statusCode");
			String statusMsg = jsonObject.getString("statusMsg");
			if (statusCode.equals("200")) {
				if (jsonObject.has("data")) {
					JSONObject dataJson = jsonObject.getJSONObject("data");
					if (dataJson.has("about_info")) {
						String detial = dataJson.getString("about_info");
						String template = AndroidUtil
								.getAssetsContents("help_info.html");
						LoggerUtil.d("webview", template);
						String detail = template.replaceAll("%s", detial);
						LoggerUtil.d("webview", detail);
						wv.loadDataWithBaseURL("about:blank", detail,
								"text/html", "UTF-8", null);
					}
				}
			} else {
				UIUtil.showToast(getActivity(), statusCode + statusMsg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}