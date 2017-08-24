package com.dingapp.biz.page;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.Listener;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.net.RequestDataUtil;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.db.dao.MemberDao;
import com.dingapp.core.db.orm.Member;
import com.dingapp.core.util.UIUtil;

/**
 * 提现页面
 * 
 * @author king
 * 
 */
public class GetMoneyFragment extends BaseFragment implements OnClickListener {
	private ImageView iv_back;
	private TextView tv_money_record;
	private TextView tv_sure;
	private EditText et_realname, et_zfbaccount, et_bankaccount, et_money_num;
	private TextView tv_yue;
	private TextView tv_money_limit;
	private Double money_limit;
	private RelativeLayout rl_zfb;
	private RelativeLayout rl_bank;
	private TextView tv_zfb;
	private TextView tv_bank;
	private View v_zfb;
	private View v_bank;
	private LinearLayout ll_zfb;
	private LinearLayout ll_bank;
	private boolean isCanGetmoney;
	// 上界面传的可提现金额
	private String yue;
	// 0代表zfb,1代表微信
	// private int type = 0;
	private Listener<String> getMoneyListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserGetMoney(response);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.getmoney, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments() != null) {
			yue = getArguments().getString(AppConstants.KEY);
			money_limit = getArguments().getDouble("money_limit");
			isCanGetmoney = getArguments().getBoolean("isCanGetmoney");
		}
		initView(getView());
		initListener();
		Date date = new Date(System.currentTimeMillis());
		String time = new SimpleDateFormat("yyyy:MM:dd").format(date);
		String time2 = getActivity().getSharedPreferences("is_getmoney",
				Context.MODE_PRIVATE).getString("is_getmoney", "");
		if (time.equals(time2)) {
			tv_sure.setOnClickListener(null);
			tv_sure.setBackgroundResource(R.drawable.bg_getmoney_not);
			tv_sure.setText("您今日已提现,客官明天再来");
		}
		initData();
	}

	private void initData() {
		try {
			tv_yue.setText(yue + "元");
			tv_money_limit.setText("满" + money_limit + "元可提现");
			if (AppConstants.member.getReal_name() != null
					&& !TextUtils.isEmpty(AppConstants.member.getReal_name())) {
				et_realname.setText(AppConstants.member.getReal_name());
			}
		} catch (Exception e) {
		}

	}

	private void initView(View view) {
		iv_back = (ImageView) view.findViewById(R.id.iv_getmoney_back);
		tv_money_record = (TextView) view.findViewById(R.id.tv_money_record);
		ll_zfb = (LinearLayout) view.findViewById(R.id.ll_getmoney_zfb);
		ll_bank = (LinearLayout) view.findViewById(R.id.ll_getmoney_bank);
		et_zfbaccount = (EditText) view
				.findViewById(R.id.et_getmoney_zfb_account);
		tv_yue = (TextView) view.findViewById(R.id.tv_getmoney_yue);
		tv_sure = (TextView) view.findViewById(R.id.tv_getmoney_sure);

		et_bankaccount = (EditText) view
				.findViewById(R.id.et_getmoney_bank_account);
		et_realname = (EditText) view.findViewById(R.id.et_getmoney_real_name);
		tv_money_limit = (TextView) getView().findViewById(R.id.tv_money_limit);
		tv_money_limit.setText("满" + money_limit + "元可提现，每日只限转账1次");
		rl_bank = (RelativeLayout) getView().findViewById(R.id.rl_bank);
		rl_zfb = (RelativeLayout) getView().findViewById(R.id.rl_zfb);
		tv_bank = (TextView) getView().findViewById(R.id.tv_bank);
		tv_zfb = (TextView) getView().findViewById(R.id.tv_zfb);
		v_bank = getView().findViewById(R.id.view_bank);
		v_zfb = getView().findViewById(R.id.view_zfb);
		et_money_num = (EditText) getView().findViewById(R.id.et_getmoney_num);
	}

	private void initListener() {
		iv_back.setOnClickListener(this);
		rl_zfb.setOnClickListener(this);
		rl_bank.setOnClickListener(this);
		tv_money_record.setOnClickListener(this);
		tv_sure.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == iv_back) {
			popBack(null);
			return;
		}
		if (v == rl_zfb) {
			ll_zfb.setVisibility(View.VISIBLE);
			ll_bank.setVisibility(View.GONE);
			tv_zfb.setTextColor(0xff0c62a9);
			tv_bank.setTextColor(0xff000000);
			v_bank.setBackgroundColor(0xffffffff);
			v_zfb.setBackgroundColor(0xff0c62a9);
			// type = 0;
			return;
		}
		if (v == rl_bank) {
			ll_zfb.setVisibility(View.GONE);
			ll_bank.setVisibility(View.VISIBLE);
			tv_zfb.setTextColor(0xff000000);
			tv_bank.setTextColor(0xff0c62a9);
			v_bank.setBackgroundColor(0xff0c62a9);
			v_zfb.setBackgroundColor(0xffffffff);
			// type = 1;
			return;
		}
		if (v == tv_money_record) {
			openPage("moneyrecord", null, true);
			return;
		}
		if (v == tv_sure) {
			if (!isCanGetmoney) {
				UIUtil.showToast(getActivity(), "已提现或金额不足,请隔日重试");
				return;
			}
			requestGetMoney();
			return;
		}
	}

	// 微信提现
	private void requestGetMoney() {
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("alipay_account", et_zfbaccount.getText().toString());
		postMap.put("real_name", et_realname.getText().toString());
		postMap.put("money", et_money_num.getText().toString());
		postMap.put("type", "normal");
		postMap.put("account_type", "alipay");
		RequestDataUtil.getRequestInstance().requestData(getMoneyListener,
				postMap, AppConstants.ali_withdraw_cash, getActivity(), null,
				"true");
	}

	private void parserGetMoney(String response) {
		try {
			JSONObject jsbJson = new JSONObject(response);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			if (statusCode.equals("200")) {
				JSONObject dataJson = jsbJson.getJSONObject("data");
				if (dataJson.has("suc")) {
					if (dataJson.getString("suc").equals("true")) {
						Member member = AppConstants.member;
						MemberDao memberDao = new MemberDao();
						member.setReal_name(et_realname.getText().toString());
						AppConstants.member = member;
						memberDao.add(member);
						Bundle bundle = new Bundle();
						openPage("moneyrecord", bundle, false);
					}
					if (dataJson.has("msg")) {
						Toast.makeText(getActivity(),
								dataJson.getString("msg"), Toast.LENGTH_LONG)
								.show();
					}
				}
			} else {
				UIUtil.showToast(getActivity(), statusMsg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
