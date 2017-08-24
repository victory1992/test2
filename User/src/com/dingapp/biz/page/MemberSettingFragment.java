package com.dingapp.biz.page;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.net.RequestDataUtil;
import com.dingapp.biz.net.SingleRequestQueue;
import com.dingapp.biz.util.LogoutUtils;
import com.dingapp.biz.util.UpdateAppUtil;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.app.NavigationFragment;
import com.dingapp.core.app.StubActivity;
import com.dingapp.core.db.dao.MemberDao;
import com.dingapp.core.util.UIUtil;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

/**
 * 更多设置
 * 
 * @author king
 * 
 */
public class MemberSettingFragment extends BaseFragment implements
		OnClickListener {
	private ImageView iv_back;
	private LinearLayout ll_feedback;
	private LinearLayout ll_aboutus;
	private LinearLayout ll_newvewsiong;
	private LinearLayout ll_question;
	private LinearLayout ll_phone;
	private TextView tv_logout;
	private RequestQueue mQueue;
	private LinearLayout ll_member_setting_componey;
	private Listener<String> logoutListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserLogoutData(response);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.member_more_setting, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mQueue = SingleRequestQueue.getreRequestQueue(getActivity());
		initView(getView());
		initListener();
	}

	private void initView(View view) {
		iv_back = (ImageView) view.findViewById(R.id.iv_member_setting_back);
		ll_feedback = (LinearLayout) view
				.findViewById(R.id.ll_member_setting_feedback);
		ll_aboutus = (LinearLayout) view
				.findViewById(R.id.ll_member_setting_aboutus);
		ll_newvewsiong = (LinearLayout) view
				.findViewById(R.id.ll_member_setting_newversion);
		ll_question = (LinearLayout) view
				.findViewById(R.id.ll_member_setting_normal_question);
		ll_phone = (LinearLayout) view
				.findViewById(R.id.ll_member_setting_phone);
		tv_logout = (TextView) view.findViewById(R.id.tv_member_setting_logout);
		ll_member_setting_componey = (LinearLayout) view
				.findViewById(R.id.ll_member_setting_componey);
		if (AppConstants.member.getSessionId() == null
				|| TextUtils.isEmpty(AppConstants.member.getSessionId())) {
			tv_logout.setVisibility(View.GONE);
		} else {
			tv_logout.setVisibility(View.VISIBLE);
		}
	}

	private void initListener() {
		iv_back.setOnClickListener(this);
		ll_feedback.setOnClickListener(this);
		ll_aboutus.setOnClickListener(this);
		ll_newvewsiong.setOnClickListener(this);
		ll_question.setOnClickListener(this);
		ll_phone.setOnClickListener(this);
		tv_logout.setOnClickListener(this);
		ll_member_setting_componey.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == iv_back) {
			popBack(null);
			return;
		}
		if (v == ll_feedback) {
			openPage("feedback", null, false);
			return;
		}
		if (v == ll_aboutus) {
			openPage("about_us", null, false);
			return;
		}
		if (v == ll_newvewsiong) {
			UpdateAppUtil util = new UpdateAppUtil(getActivity(), mQueue);
			util.requestData(getActivity(), true);
			return;
		}
		if (v == ll_question) {
			openPage("normal_question", null, false);
			return;
		}
		if (v == ll_phone) {
			// CheckSessionIdUtils.ReLogin(getActivity());
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("提示框");
			builder.setMessage("确定要拨打" + "010-56017296" + "电话吗？");
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							Intent intent = new Intent(Intent.ACTION_CALL, Uri
									.parse("tel:" + "010-56017296"));
							startActivity(intent);

						}
					});
			builder.create().show();
			return;
		}
		if (v == tv_logout) {
			requestLogout();
			return;
		}
		if (v == ll_member_setting_componey) {
			Bundle bundle = new Bundle();
			bundle.putString("target_url",
					"http://123.57.12.28/data/h5/hulu1/ganggao.html");
			openPage("extranet", bundle, true);
			return;
		}
	}

	private void requestLogout() {
		HashMap<String, String> postMap = new HashMap<String, String>();
		RequestDataUtil.getRequestInstance().requestData(logoutListener,
				postMap, AppConstants.logout, getActivity(), null, "true");

	}

	private void parserLogoutData(String response) {
		try {
			JSONObject jsbJson = new JSONObject(response);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			if (statusCode.equals("200")) {
				LogoutUtils.logout(getActivity());
			} else {
				UIUtil.showToast(getActivity(), statusMsg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
