package com.dingapp.biz.page;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;

import com.android.volley.Response.Listener;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.net.RequestDataUtil;
import com.dingapp.biz.util.WebViewUtils;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.AndroidUtil;
import com.dingapp.core.util.LoggerUtil;
import com.dingapp.core.util.UIUtil;

public class NormalQuestionFragment extends BaseFragment {
	private ImageView iv_back;
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
		return inflater.inflate(R.layout.normal_question, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		iv_back = (ImageView) getView().findViewById(
				R.id.iv_normal_question_back);
		iv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popBack(null);
			}
		});
		wv = (WebView) getView().findViewById(R.id.webview);
		WebViewUtils.initWebView(wv);
		requestAddressData();
	}

	private void requestAddressData() {
		String url = "/api/v1/member/help_info";
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("type", "normal");
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
					if (dataJson.has("help_info")) {
						String detial = dataJson.getString("help_info");
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
