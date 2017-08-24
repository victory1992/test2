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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.net.RequestDataUtil;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.db.dao.MemberDao;
import com.dingapp.core.util.UIUtil;

/**
 * 修改性别
 * 
 * @author king
 * 
 */
public class MemberModifySexFragment extends BaseFragment implements
		OnClickListener {
	private ImageView iv_back;
	private LinearLayout ll_maile;
	private ImageView iv_maile;
	private LinearLayout ll_femaile;
	private ImageView iv_femaile;
	private TextView tv_sure;
	private String sex = "male";
	private Listener<String> modifyHeaderListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserQueryData(response);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.member_modify_sex, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		sex = AppConstants.member.getWx_nick_name();
		initView(getView());
		initListener();
	}

	private void initView(View view) {
		iv_back = (ImageView) view.findViewById(R.id.iv_modify_sex_back);
		ll_maile = (LinearLayout) view.findViewById(R.id.ll_modify_sex_maile);
		iv_maile = (ImageView) view.findViewById(R.id.iv_modify_sex_maile);
		ll_femaile = (LinearLayout) view
				.findViewById(R.id.ll_modify_sex_femaile);
		iv_femaile = (ImageView) view.findViewById(R.id.iv_modify_sex_femaile);
		tv_sure = (TextView) view.findViewById(R.id.tv_modify_sex_sure);
		if (sex.equals("female")) {
			initImageView(iv_femaile);
		} else if (sex.equals("male")) {
			initImageView(iv_maile);
		}
	}

	private void initListener() {
		iv_back.setOnClickListener(this);
		ll_maile.setOnClickListener(this);
		ll_femaile.setOnClickListener(this);
		tv_sure.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == iv_back) {
			popBack(null);
			return;
		}
		if (v == ll_maile) {
			initImageView(iv_maile);
			sex = "male";
			return;
		}
		if (v == ll_femaile) {
			initImageView(iv_femaile);
			sex = "female";
			return;
		}
		if (v == tv_sure) {
			requestModifySex(sex);
			return;
		}
	}

	private void initImageView(ImageView iv) {
		iv_femaile.setVisibility(View.GONE);
		iv_maile.setVisibility(View.GONE);
		iv.setVisibility(View.VISIBLE);
	}

	// 修改性别
	private void requestModifySex(String sex) {
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("gender", sex);
		RequestDataUtil.getRequestInstance().requestData(modifyHeaderListener,
				postMap, AppConstants.gender_modification, getActivity(), null,
				"true");
	}

	private void parserQueryData(String response) {
		try {
			JSONObject jsbJson = new JSONObject(response);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			if (statusCode.equals("200")) {
				UIUtil.showToast(getActivity(), "修改成功");
				AppConstants.member.setWx_nick_name(sex);
				new MemberDao().add(AppConstants.member);
				popBack(null);
			} else {
				UIUtil.showToast(getActivity(), statusMsg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}
