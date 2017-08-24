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

import com.android.volley.Response.Listener;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.net.RequestDataUtil;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.db.dao.MemberDao;
import com.dingapp.core.util.UIUtil;

/**
 * 修改昵称
 * 
 * @author king
 * 
 */
public class MemberModifyNickNameFragment extends BaseFragment implements
		OnClickListener {
	private ImageView iv_back;
	private EditText et_nickname;
	private TextView tv_sure;
	private String nick_name;
	private Listener<String> modifyHeaderListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserQueryData(response);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.member_modify_nickname, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments() != null) {
			nick_name = getArguments().getString(AppConstants.KEY);
		}
		initView(getView());
		initListener();
	}

	private void initView(View view) {
		iv_back = (ImageView) view.findViewById(R.id.iv_modify_nickname_back);
		et_nickname = (EditText) view.findViewById(R.id.et_modify_nickname);
		tv_sure = (TextView) view.findViewById(R.id.tv_modify_nickname_sure);
		if (!TextUtils.isEmpty(nick_name)) {
			et_nickname.setText(nick_name);
		}
	}

	private void initListener() {
		iv_back.setOnClickListener(this);
		tv_sure.setOnClickListener(this);
		et_nickname.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				String text = arg0.toString();
				if (text != null && text.length() == 10) {
					UIUtil.showToast(getActivity(), "最多只能输入10个字");
					return;
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v == iv_back) {
			popBack(null);
			return;
		}
		if (v == tv_sure) {
			String nickname = et_nickname.getText().toString();
			if (nickname == null || TextUtils.isEmpty(nickname)) {
				UIUtil.showToast(getActivity(), "昵称不能为空");
				return;
			}
			requestModifyNick(nickname);
			return;
		}
	}

	// 修改昵称
	private void requestModifyNick(String nick_name) {
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("nick_name", nick_name);
		RequestDataUtil.getRequestInstance().requestData(modifyHeaderListener,
				postMap, AppConstants.nickname_modification, getActivity(),
				null, "true");
	}

	private void parserQueryData(String response) {
		try {
			JSONObject jsbJson = new JSONObject(response);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			if (statusCode.equals("200")) {
				UIUtil.showToast(getActivity(), "修改成功");
				AppConstants.member.setNickName(et_nickname.getText()
						.toString());
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
