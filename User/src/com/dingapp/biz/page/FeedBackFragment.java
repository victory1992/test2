package com.dingapp.biz.page;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.net.SingleRequestQueue;
import com.dingapp.biz.net.StringPostRequest;
import com.dingapp.biz.util.MemberUtil;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.AndroidUtil;
import com.dingapp.core.util.UIUtil;
/**
 * 留言反馈页面
 * @author king
 *
 */
public class FeedBackFragment extends BaseFragment implements OnClickListener {
	private ImageView iv_back;
	private TextView tv_send;
	private EditText et_back;
	private RequestQueue mQueue;
	/**
	 * 输入的字数
	 */
	private TextView tvNumber;
	private ErrorListener errorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {

		}
	};
	private Listener<String> listener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserData(response);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.feedback, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mQueue = SingleRequestQueue.getreRequestQueue(getActivity());
		initView();
		initListener();
	}

	private void initView() {
		iv_back = (ImageView) getView().findViewById(R.id.iv_feedback_back);
		tv_send = (TextView) getView().findViewById(R.id.tv_feedback_submit);
		et_back = (EditText) getView().findViewById(R.id.et_feedback_content);
		tvNumber = (TextView) getView().findViewById(R.id.tv_feedback_number);
	}

	private void initListener() {
		iv_back.setOnClickListener(this);
		tv_send.setOnClickListener(this);
		et_back.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				int num = s.length();
				tvNumber.setText(num + "/100");
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v == iv_back) {
			popBack(null);
			return;
		}
		if (v == tv_send) {
			String content = et_back.getText().toString();
			if (TextUtils.isEmpty(content)) {
				UIUtil.showToast(getActivity(), "反馈内容不能为空");
				return;
			}
			requestData();
			return;
		}
	}

	// 反馈接口
	private void requestData() {
		if (AndroidUtil.isNetworkAvailable(getActivity())) {
			String url = AppConstants.BaseUrl + AppConstants.feedback;
			HashMap<String, String> postMap = new HashMap<String, String>();
			postMap.put("session_id", AppConstants.member.getSessionId());
			postMap.put("platform", AppConstants.PLATFORM);
			postMap.put("content", et_back.getText().toString());

			StringPostRequest request = new StringPostRequest(postMap, url,
					listener, errorListener);
			mQueue.add(request);
		} else {
			UIUtil.showToast(getActivity(), AppConstants.NetNotifice);
		}
	}

	// 解析反馈返回数据
	private void parserData(String response) {
		try {
			JSONObject jsonObj = new JSONObject(response);
			String statusCode = jsonObj.getString("statusCode");
			String statusMsg = jsonObj.getString("statusMsg");
			if (TextUtils.equals(statusCode, "200")) {
				UIUtil.showToast(getActivity(), "反馈成功");
				popBack(null);
			} else {
				if (statusCode.equals("1001")) {
					MemberUtil.reLogin(getActivity());
				} else {
					UIUtil.showToast(getActivity(), statusMsg);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		mQueue.cancelAll(this);
		super.onDestroy();
	}
}
