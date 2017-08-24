package com.dingapp.biz.page;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.net.RequestDataUtil;
import com.dingapp.biz.pay.weixin.MD5;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.MobileUtil;
import com.dingapp.core.util.UIUtil;

/**
 * 忘记密码界面
 * 
 * @author king
 * 
 */
public class ForgetPasswordFragment extends BaseFragment implements
		OnClickListener {
	private ImageView img_back;
	private EditText et_input_phone;
	private EditText et_input_code;
	private TextView tv_send_code;
	private EditText et_input_pwd;
	private EditText et_re_input_pwd;
	private TextView tv_confirm;
	private String token;
	private Listener<String> listener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserSendValidCode(response);
		}
	};
	private Listener<String> forgetPwdListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parseData(response);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return View.inflate(getActivity(), R.layout.forgetpassword, null);
	}

	protected void parseData(String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			String statusMsg = jsonObject.getString("statusMsg");
			String stausCode = jsonObject.getString("statusCode");
			if ("200".equals(stausCode)) {
				if (jsonObject.has("data")) {
					JSONObject data = jsonObject.getJSONObject("data");
					if (data.has("suc")) {
						String suc = data.getString("suc");
						if ("true".equals(suc)) {
							UIUtil.showToast(getActivity(), "密码修改成功，请登录！");
							Bundle bundle = new Bundle();
							bundle.putString("phone_num", et_input_phone
									.getText().toString());
							bundle.putString("password", et_input_pwd.getText()
									.toString());
							popBack(bundle);
						} else {
							UIUtil.showToast(getActivity(), "密码修改失败，请重新修改！");
						}
					}
				}
			} else {
				UIUtil.showToast(getActivity(), statusMsg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void parserSendValidCode(String json) {
		try {
			JSONObject jsbJson = new JSONObject(json);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			if (statusCode.equals("200")) {
				JSONObject dataJson = jsbJson.getJSONObject("data");
				if (dataJson.has("token")) {
					token = dataJson.getString("token");
				}
			} else {
				UIUtil.showToast(getActivity(), statusMsg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initListener();
	}

	private void initListener() {
		tv_confirm.setOnClickListener(this);
		tv_send_code.setOnClickListener(this);
		img_back.setOnClickListener(this);
	}

	private void initView() {
		img_back = (ImageView) getView().findViewById(R.id.img_back);
		et_input_phone = (EditText) getView().findViewById(R.id.et_input_phone);
		et_input_code = (EditText) getView().findViewById(R.id.et_input_code);
		et_input_pwd = (EditText) getView().findViewById(R.id.et_input_pwd);
		tv_send_code = (TextView) getView().findViewById(R.id.tv_send_code);
		tv_confirm = (TextView) getView().findViewById(R.id.tv_confirm);
		et_re_input_pwd = (EditText) getView().findViewById(
				R.id.et_re_input_pwd);
	}

	@Override
	public void onClick(View v) {
		if (v == tv_confirm) {
			checkInfo();
			return;
		} else if (v == tv_send_code) {
			checkPhone();
			return;
		} else if (v == img_back) {
			popBack(null);
			return;
		}
	}

	private void checkInfo() {
		String phoneNum = et_input_phone.getText().toString();
		String code = et_input_code.getText().toString();
		String password = et_input_pwd.getText().toString();
		if (TextUtils.isEmpty(phoneNum)
				|| !MobileUtil.isValidPhoneNumber(phoneNum)) {
			UIUtil.showToast(getActivity(), "请输入合法手机号");
			return;
		}
		if (TextUtils.isEmpty(code)) {
			UIUtil.showToast(getActivity(), "验证码输入有误");
			return;
		}
		if (TextUtils.isEmpty(password)) {
			UIUtil.showToast(getActivity(), "请设置新的密码");
			return;
		}
		if (!TextUtils.equals(et_re_input_pwd.getText().toString(), password)) {
			UIUtil.showToast(getActivity(), "两次密码输入不一致,请重新输入");
			return;
		}
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("mobile", phoneNum);
		postMap.put("token", token);
		postMap.put("checkcode", code);
		postMap.put("type", "normal");
		String md5Password = MD5.getMessageDigest(password.getBytes())
				.toUpperCase();
		postMap.put("password", md5Password);
		postMap.put("platform", AppConstants.PLATFORM);
		postMap.put("type", "normal");
		RequestDataUtil.getRequestInstance().requestData(forgetPwdListener,
				postMap, "/api/v1/member/retrieve_pwd", getActivity(), null,
				"false");
	}

	@SuppressLint("NewApi")
	private void checkPhone() {
		if (et_input_phone.getText() == null
				|| TextUtils.isEmpty(et_input_phone.getText().toString())
				|| !MobileUtil.isValidPhoneNumber(et_input_phone.getText()
						.toString())) {
			UIUtil.showToast(getActivity(), "请输入合法手机号");
			return;
		}
		UIUtil.hideKeyboard(getActivity());
		// 修改 按钮颜色
		tv_send_code.setText("60S 已发送");
		tv_send_code.setOnClickListener(null);
		requestVerificationCode();
		startRetryTimer();
	}

	private void requestVerificationCode() {
		String phone = et_input_phone.getText().toString();
		if (TextUtils.isEmpty(phone)) {
			UIUtil.showToast(getActivity(), "手机号码不能为空");
			return;
		}

		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("mobile", phone);
		postMap.put("type", "fogot_pwd");
		postMap.put("member_type", "normal");
		RequestDataUtil.getRequestInstance().requestData(listener, postMap,
				AppConstants.send_valid_code, getActivity(), null, "false");

	}

	private void startRetryTimer() {
		tv_send_code.postDelayed(new Runnable() {

			@Override
			public void run() {
				updateTimerTv();
			}

		}, 1000);
	}

	@SuppressLint("NewApi")
	private void updateTimerTv() {
		if (isActivityFinished()) {
			return;
		}

		String previous = tv_send_code.getText().toString();
		if (TextUtils.isEmpty(previous)) {
			return;
		}

		int idx = previous.indexOf("S");
		if (idx <= 0) {
			return;
		}

		int previousTime = 0;
		try {
			previousTime = Integer.parseInt(previous.substring(0, idx));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (previousTime <= 1) {
			tv_send_code.setText("  重试  ");
			tv_send_code.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (et_input_phone.getText() == null
							|| TextUtils.isEmpty(et_input_phone.getText()
									.toString())
							|| !MobileUtil.isValidPhoneNumber(et_input_phone
									.getText().toString())) {
						UIUtil.showToast(getActivity(), "请输入合法手机号");
						return;
					}
					requestVerificationCode();
					tv_send_code.setText("60S 已发送");
					tv_send_code.setOnClickListener(null);
					startRetryTimer();
				}
			});
		} else {
			tv_send_code.setText((previousTime - 1) + "S 已发送");
			startRetryTimer();
		}
	}

}
