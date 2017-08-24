package com.dingapp.biz.page;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
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
import com.dingapp.biz.hx.HXContactUtils;
import com.dingapp.biz.hx.HXUtils;
import com.dingapp.biz.net.RequestDataUtil;
import com.dingapp.biz.pay.weixin.MD5;
import com.dingapp.biz.util.MD5Util;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.db.dao.MemberDao;
import com.dingapp.core.db.orm.Member;
import com.dingapp.core.util.UIUtil;

@SuppressLint("NewApi")
public class LoginFragment extends BaseFragment implements OnClickListener {
	/**
	 * 登录界面
	 */
	private ImageView iv_back;
	private EditText et_phone;
	private EditText et_code;
	private TextView tv_login;
	private TextView tv_forget_password;
	private TextView tv_register;
	// 登录的接口
	private Listener<String> loginListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			Log.i("login", response);
			parserLoginData(response);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.login_page, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView(getView());
		initListener();
	}

	private void initView(View view) {
		iv_back = (ImageView) view.findViewById(R.id.iv_login_back);
		et_phone = (EditText) view.findViewById(R.id.et_login_phone);
		et_code = (EditText) view.findViewById(R.id.et_login_valicode);
		tv_login = (TextView) view.findViewById(R.id.tv_login);
		tv_forget_password = (TextView) view
				.findViewById(R.id.tv_login_foget_password);
		tv_register = (TextView) view.findViewById(R.id.tv_login_register);
	}

	private void initListener() {
		iv_back.setOnClickListener(this);
		tv_login.setOnClickListener(this);
		tv_forget_password.setOnClickListener(this);
		tv_register.setOnClickListener(this);
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		if (v == iv_back) {
			popBack(null);
			return;
		}
		if (v == tv_login) {
			requestLogin();
			return;
		}
		if (v == tv_forget_password) {
			openPage("foget_password", null, false);
			return;
		}
		if (v == tv_register) {
			openPage("register", null, false);
			return;
		}
	}

	@Override
	protected void onDataReset(Bundle bundle) {
		super.onDataReset(bundle);
		if (bundle != null) {
			if (bundle.containsKey("phone_num")) {
				et_phone.setText(bundle.getString("phone_num"));
			}
			if (bundle.containsKey("password")) {
				et_code.setText(bundle.getString("password"));
			}
		}
	}

	private void requestLogin() {
		if (TextUtils.isEmpty(et_phone.getText().toString())) {
			UIUtil.showToast(getActivity(), "手机号不能为空");
			return;
		}
		if (TextUtils.isEmpty(et_code.getText().toString())) {
			UIUtil.showToast(getActivity(), "密码不能为空");
			return;
		}
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("password",
				MD5.getMessageDigest(et_code.getText().toString().getBytes())
						.toUpperCase());
		postMap.put("type", "normal");
		postMap.put("mobile", et_phone.getText().toString());
		RequestDataUtil.getRequestInstance().requestData(loginListener,
				postMap, AppConstants.login, getActivity(), null, "false");
	}

	// 解析登录后数据
	private void parserLoginData(String response) {
		try {
			JSONObject jsbJson = new JSONObject(response);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			if (statusCode.equals("200")) {
				JSONObject dataJson = jsbJson.getJSONObject("data");
				Member member = AppConstants.member;
				if (dataJson.has("session_id")) {
					member.setSessionId(dataJson.getString("session_id"));
				}
				if (dataJson.has("re_login_token")) {
					member.setToken(dataJson.getString("re_login_token"));
				}
				if (dataJson.has("member_id")) {
					member.setMemberId((long) dataJson.getInt("member_id"));
				}
				if (dataJson.has("mobile")) {
					member.setLoginName(dataJson.getString("mobile"));
				}
				if (dataJson.has("nick_name")) {
					member.setNickName(dataJson.getString("nick_name"));
				}
				if (dataJson.has("header_profile")) {
					JSONObject headerJson = dataJson
							.getJSONObject("header_profile");
					if (headerJson.has("detail_url")) {
						member.setHeaderProfile(headerJson
								.getString("detail_url"));
					}
				}
				if (dataJson.has("member_type")) {
					AppConstants.isVipMember = dataJson.get("member_type")
							.equals("vip") ? true : false;
				}
				if (dataJson.has("score")) {
					member.setScore(dataJson.getInt("score"));
				} else {
					member.setScore(0);
				}
				if (dataJson.has("gender")) {
					member.setWx_nick_name(dataJson.getString("gender"));
				}
				MemberDao memberDao = new MemberDao();
				memberDao.add(member);
				AppConstants.member = member;
				hxLogin();
				 HXContactUtils.getInstance(getActivity()).requestHxInfos(0);
				popBack(null);
			} else {
				UIUtil.showToast(getActivity(), statusMsg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void hxLogin() {
		HXUtils.login("appmember" + AppConstants.member.getMemberId(),
				MD5Util.MD5(AppConstants.member.getMemberId() + "appmember"),
				getActivity());
	}
}
