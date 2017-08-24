package com.dingapp.biz.page;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.MemberCenterBean;
import com.dingapp.biz.net.RequestDataUtil;
import com.dingapp.biz.util.ImageViewHWutils;
import com.dingapp.biz.util.LogoutUtils;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.db.dao.MemberDao;
import com.dingapp.core.db.orm.Member;
import com.dingapp.core.util.UIUtil;
import com.dingapp.imageloader.core.ImageLoader;
import com.google.gson.Gson;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

public class MemberCenterFragment extends BaseFragment implements
		OnClickListener {
	private ImageView iv_head;
	private TextView tv_nick;
	private LinearLayout ll_distribution_center;
	private LinearLayout ll_jifen_shop;
	private LinearLayout ll_jifen_trade_record;
	private LinearLayout ll_more;
	private TextView tv_jifen;
	private RelativeLayout rl_coupon;
	private TextView tv_coupon_num;
	private RelativeLayout rl_myfav;
	private TextView tv_myfav_num;
	private LinearLayout ll_personal;
	private RelativeLayout rl_see_order;
	private RelativeLayout rl_pay;
	private RelativeLayout rl_deliver;
	private RelativeLayout rl_receiver;
	private TextView tv_pay_count;
	private TextView tv_deliver_count;
	private TextView tv_receiver_count;
	private TextView tv_eveluate_count;
	private TextView tv_order_return_count;
	private RelativeLayout rl_eveluate;
	private RelativeLayout rl_order_return;
	private LinearLayout ll_jifenmingxi;
	private MemberCenterBean bean;
	private LinearLayout ll_haslogin;
	private LinearLayout ll_nologin;
	private TextView tv_login;
	private ImageView tv_msg_notify;
	private ImageView iv_member_vip;
	private LinearLayout ll_message;
	private Listener<String> msgListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserMsgData(response);
		}
	};
	private EMMessageListener messageListener;
	private int systemMsgCnt = 0;
	private Listener<String> infosListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserInfoData(response);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		int layoutId = R.layout.member_center;
		return inflater.inflate(layoutId, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView(getView());
		requestMemberInfos();
		initMessages();
	}

	private void initMessages() {
		if (TextUtils.isEmpty(AppConstants.member.getSessionId())) {
			return;
		}
		initAnimation(0);
		messageListener = new EMMessageListener() {

			@Override
			public void onMessageReceived(List<EMMessage> arg0) {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						initAnimation(1);
					}
				});
			}

			@Override
			public void onMessageReadAckReceived(List<EMMessage> arg0) {

			}

			@Override
			public void onMessageDeliveryAckReceived(List<EMMessage> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMessageChanged(EMMessage arg0, Object arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onCmdMessageReceived(List<EMMessage> arg0) {
				// TODO Auto-generated method stub

			}
		};
		EMClient.getInstance().chatManager()
				.addMessageListener(messageListener);
		int chatMsg = getCount();
		if (chatMsg > 0) {
			initAnimation(1);
		} else {
			initAnimation(0);
		}
		requestMsgData();
	}

	public int getCount() {
		int unreadMsgCount = 0;
		EMClient.getInstance().chatManager().loadAllConversations();
		Map<String, EMConversation> conversations = EMClient.getInstance()
				.chatManager().getAllConversations();
		Set<Entry<String, EMConversation>> entrySet = conversations.entrySet();
		Iterator<Entry<String, EMConversation>> iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Map.Entry<java.lang.String, com.hyphenate.chat.EMConversation> entry = (Map.Entry<java.lang.String, com.hyphenate.chat.EMConversation>) iterator
					.next();
			EMConversation emConversation = entry.getValue();
			unreadMsgCount += emConversation.getUnreadMsgCount();
		}
		return unreadMsgCount;
	}

	private void initView(View v) {
		iv_head = (ImageView) v.findViewById(R.id.iv_member_touxiang);
		tv_nick = (TextView) v.findViewById(R.id.tv_member_name);
		ll_distribution_center = (LinearLayout) v
				.findViewById(R.id.ll_member_distribution_center);
		ll_jifen_shop = (LinearLayout) v
				.findViewById(R.id.ll_member_jifen_shop);
		ll_jifen_trade_record = (LinearLayout) v
				.findViewById(R.id.ll_member_jifen_trade_record);
		ll_more = (LinearLayout) v.findViewById(R.id.ll_member_more);
		tv_jifen = (TextView) v.findViewById(R.id.tv_member_jifen);
		rl_coupon = (RelativeLayout) v.findViewById(R.id.rl_member_mycounpons);
		tv_coupon_num = (TextView) v.findViewById(R.id.tv_member_coupons_num);
		rl_myfav = (RelativeLayout) v.findViewById(R.id.rl_member_myfav);
		tv_myfav_num = (TextView) v.findViewById(R.id.tv_member_myfav_num);
		rl_see_order = (RelativeLayout) getView().findViewById(
				R.id.rl_see_order);
		rl_pay = (RelativeLayout) getView().findViewById(R.id.rl_pay);
		rl_deliver = (RelativeLayout) getView().findViewById(R.id.rl_deliver);
		rl_receiver = (RelativeLayout) getView().findViewById(R.id.rl_receiver);
		rl_eveluate = (RelativeLayout) getView().findViewById(R.id.rl_eveluate);
		tv_eveluate_count = (TextView) getView().findViewById(
				R.id.tv_eveluate_count);
		tv_receiver_count = (TextView) getView().findViewById(
				R.id.tv_receiver_count);
		tv_deliver_count = (TextView) getView().findViewById(
				R.id.tv_deliver_count);
		tv_pay_count = (TextView) getView().findViewById(R.id.tv_pay_count);
		rl_order_return = (RelativeLayout) getView().findViewById(
				R.id.rl_order_return);
		tv_order_return_count = (TextView) getView().findViewById(
				R.id.tv_order_return_count);
		ll_jifenmingxi = (LinearLayout) getView().findViewById(
				R.id.ll_member_jifenmingxi);
		ll_haslogin = (LinearLayout) getView().findViewById(R.id.ll_has_login);
		ll_nologin = (LinearLayout) getView().findViewById(R.id.ll_no_login);
		tv_login = (TextView) getView()
				.findViewById(R.id.tv_membercenter_login);
		tv_msg_notify = (ImageView) v.findViewById(R.id.tv_msg_notify_member);
		ll_message = (LinearLayout) v.findViewById(R.id.ll_member_message);
		iv_member_vip = (ImageView) v.findViewById(R.id.iv_member_vip);
		tv_login.setOnClickListener(this);
		ll_jifenmingxi.setOnClickListener(this);
		iv_head.setOnClickListener(this);
		ll_distribution_center.setOnClickListener(this);
		ll_jifen_shop.setOnClickListener(this);
		ll_jifen_trade_record.setOnClickListener(this);
		ll_more.setOnClickListener(this);
		rl_coupon.setOnClickListener(this);
		rl_myfav.setOnClickListener(this);
		rl_see_order.setOnClickListener(this);
		rl_pay.setOnClickListener(this);
		rl_receiver.setOnClickListener(this);
		rl_deliver.setOnClickListener(this);
		rl_eveluate.setOnClickListener(this);
		rl_order_return.setOnClickListener(this);
		ll_message.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == iv_head) {
			if (AppConstants.member.getSessionId() == null
					|| TextUtils.isEmpty(AppConstants.member.getSessionId())) {
				openPage("login", null, false);
				return;
			}
			openPage("member_personal_info", null, false);
			return;
		}
		if (v == ll_distribution_center) {
			if (AppConstants.member.getSessionId() == null
					|| TextUtils.isEmpty(AppConstants.member.getSessionId())) {
				openPage("login", null, false);
				return;
			}
			openPage("dis_home", null, false);
			return;
		}
		if (v == ll_jifen_shop) {
			if (AppConstants.member.getSessionId() == null
					|| TextUtils.isEmpty(AppConstants.member.getSessionId())) {
				openPage("login", null, false);
				return;
			}
			openPage("jifen_home", null, false);
			return;
		}
		if (v == ll_jifen_trade_record) {
			if (AppConstants.member.getSessionId() == null
					|| TextUtils.isEmpty(AppConstants.member.getSessionId())) {
				openPage("login", null, false);
				return;
			}
			openPage("jifen_orderlist", null, false);
			return;
		}
		if (v == ll_more) {
			openPage("member_more_setting", null, false);
			return;
		}
		if (v == rl_coupon) {
			if (AppConstants.member.getSessionId() == null
					|| TextUtils.isEmpty(AppConstants.member.getSessionId())) {
				openPage("login", null, false);
				return;
			}
			openPage("coupon_list", null, false);
			return;
		}
		if (v == rl_myfav) {
			if (AppConstants.member.getSessionId() == null
					|| TextUtils.isEmpty(AppConstants.member.getSessionId())) {
				openPage("login", null, false);
				return;
			}
			openPage("center_myfav", null, false);
			return;
		}
		if (v == rl_see_order) {
			if (AppConstants.member.getSessionId() == null
					|| TextUtils.isEmpty(AppConstants.member.getSessionId())) {
				openPage("login", null, false);
				return;
			}
			Bundle bundle = new Bundle();
			bundle.putString("status", "all");
			openPage("order_list", bundle, true);
			return;
		}
		if (v == rl_pay) {
			if (AppConstants.member.getSessionId() == null
					|| TextUtils.isEmpty(AppConstants.member.getSessionId())) {
				openPage("login", null, false);
				return;
			}
			Bundle bundle = new Bundle();
			bundle.putString("status", "paying");
			bundle.putString("title", "待付款");
			openPage("order_list", bundle, true);
			return;
		}
		if (v == rl_deliver) {
			if (AppConstants.member.getSessionId() == null
					|| TextUtils.isEmpty(AppConstants.member.getSessionId())) {
				openPage("login", null, false);
				return;
			}
			Bundle bundle = new Bundle();
			bundle.putString("status", "shipping");
			bundle.putString("title", "待发货");
			openPage("order_list", bundle, true);
			return;
		}
		if (v == rl_eveluate) {
			if (AppConstants.member.getSessionId() == null
					|| TextUtils.isEmpty(AppConstants.member.getSessionId())) {
				openPage("login", null, false);
				return;
			}
			Bundle bundle = new Bundle();
			bundle.putString("status", "evaluating");
			bundle.putString("title", "待评价");
			openPage("order_list", bundle, true);
			return;
		}
		if (v == rl_receiver) {
			if (AppConstants.member.getSessionId() == null
					|| TextUtils.isEmpty(AppConstants.member.getSessionId())) {
				openPage("login", null, false);
				return;
			}
			Bundle bundle = new Bundle();
			bundle.putString("status", "receipting");
			bundle.putString("title", "待收货");
			openPage("order_list", bundle, true);
			return;
		}
		if (v == rl_order_return) {
			if (AppConstants.member.getSessionId() == null
					|| TextUtils.isEmpty(AppConstants.member.getSessionId())) {
				openPage("login", null, false);
				return;
			}
			Bundle bundle = new Bundle();
			bundle.putString("status", "saleReturn");
			bundle.putString("title", "退款/售后");
			openPage("order_list", bundle, true);
			return;
		}
		if (v == ll_jifenmingxi) {
			if (AppConstants.member.getSessionId() == null
					|| TextUtils.isEmpty(AppConstants.member.getSessionId())) {
				openPage("login", null, false);
				return;
			}
			openPage("jifen_mingxi", null, true);
			return;
		}
		if (v == tv_login) {
			openPage("login", null, false);
		}
		if (v == ll_message) {
			if (AppConstants.member.getSessionId() == null
					|| TextUtils.isEmpty(AppConstants.member.getSessionId())) {
				openPage("login", null, false);
				return;
			}
			Bundle bundle = new Bundle();
			bundle.putInt(AppConstants.KEY, systemMsgCnt);
			openPage("wechat_home", bundle, false);
			return;
		}
	}

	private Animation animation;

	public void initAnimation(int count) {
		if (count > 0) {
			tv_msg_notify.setVisibility(View.VISIBLE);
			animation = AnimationUtils.loadAnimation(getActivity(),
					R.anim.message_notify);
			tv_msg_notify.setAnimation(animation);
			animation.startNow();
		} else {
			if (animation != null) {
				animation.cancel();
			}
			tv_msg_notify.clearAnimation();
			tv_msg_notify.setVisibility(View.GONE);
		}
	}

	private void initData() {
		if (TextUtils.isEmpty(AppConstants.member.getSessionId())) {
			ll_nologin.setVisibility(View.VISIBLE);
			ll_haslogin.setVisibility(View.GONE);
			iv_head.setImageResource(R.drawable.icon);
		} else {
			ll_nologin.setVisibility(View.GONE);
			ll_haslogin.setVisibility(View.VISIBLE);
			if (AppConstants.member == null) {
				return;
			}
			if (bean.getIs_vip() != null && bean.getIs_vip().equals("true")) {
				AppConstants.isVipMember = true;
				iv_member_vip.setVisibility(View.VISIBLE);
			} else {
				AppConstants.isVipMember = false;
				iv_member_vip.setVisibility(View.GONE);
			}
			if (AppConstants.member.getHeaderProfile() != null
					&& !TextUtils.isEmpty(AppConstants.member
							.getHeaderProfile())) {
				ImageLoader.getInstance().displayImage(
						bean.getHeader_profile().getDetail_url()
								+ ImageViewHWutils.getWHImageVIew(180, 180),
						iv_head);
			}
			if (AppConstants.member.getNickName() != null
					&& !TextUtils.isEmpty(bean.getNick_name())) {
				tv_nick.setText(bean.getNick_name());
			} else {
				tv_nick.setText("暂未设置昵称");
			}
			if (bean.getPaying() > 0) {
				tv_pay_count.setText(bean.getPaying() + "");
				tv_pay_count.setVisibility(View.VISIBLE);
			} else {
				tv_pay_count.setVisibility(View.INVISIBLE);
			}
			if (bean.getShipping() > 0) {
				tv_deliver_count.setText(bean.getShipping() + "");
				tv_deliver_count.setVisibility(View.VISIBLE);
			} else {
				tv_deliver_count.setVisibility(View.INVISIBLE);
			}
			if (bean.getEvaluating() > 0) {
				tv_eveluate_count.setText(bean.getEvaluating() + "");
				tv_eveluate_count.setVisibility(View.VISIBLE);
			} else {
				tv_eveluate_count.setVisibility(View.INVISIBLE);
			}
			if (bean.getReceipting() > 0) {
				tv_receiver_count.setText(bean.getReceipting() + "");
				tv_receiver_count.setVisibility(View.VISIBLE);
			} else {
				tv_receiver_count.setVisibility(View.INVISIBLE);
			}
			if (bean.getSalereturn() > 0) {
				tv_order_return_count.setText(bean.getSalereturn() + "");
				tv_order_return_count.setVisibility(View.VISIBLE);
			} else {
				tv_order_return_count.setVisibility(View.INVISIBLE);
			}
			tv_jifen.setText("我的积分: " + bean.getScore());
			tv_myfav_num.setText(bean.getCollect_cnt() + "");
			tv_coupon_num.setText(bean.getCoupon_cnt() + "");
			Member member = AppConstants.member;
			member.setScore(bean.getScore());
			if (bean.getHeader_profile() != null
					&& bean.getHeader_profile().getDetail_url() != null) {
				member.setHeaderProfile(bean.getHeader_profile()
						.getDetail_url());
			}
			member.setNickName(bean.getNick_name());
			member.setBirthday(bean.getBirth_date());
			member.setWx_nick_name(bean.getGender());
			new MemberDao().add(member);
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			requestMemberInfos();
			initMessages();
		}
	}

	// 请求数据总接口
	private void requestMemberInfos() {
		HashMap<String, String> postMap = new HashMap<String, String>();
		RequestDataUtil.getRequestInstance()
				.requestData(infosListener, postMap, AppConstants.member_infos,
						getActivity(), null, "true");
	}

	private void parserInfoData(String json) {
		try {
			JSONObject jsbJson = new JSONObject(json);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			Gson gson = new Gson();
			if (statusCode.equals("200")) {
				String dataJson = jsbJson.getString("data");
				bean = gson.fromJson(dataJson, MemberCenterBean.class);
				initData();
			} else if (TextUtils.equals(statusCode, "1001")) {
				UIUtil.showToast(getActivity(), "身份登录信息失效");
				LogoutUtils.logout(getActivity());
			} else {
				UIUtil.showToast(getActivity(), "未知错误" + statusMsg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void requestMsgData() {
		HashMap<String, String> postMap = new HashMap<String, String>();
		RequestDataUtil.getRequestInstance().requestData(msgListener, postMap,
				AppConstants.has_new_msg, getActivity(), null, "true");
	}

	private void parserMsgData(String json) {
		try {
			JSONObject jsbJson = new JSONObject(json);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			if (statusCode.equals("200")) {
				JSONObject dataJson = jsbJson.getJSONObject("data");
				if (dataJson.has("suc")
						&& dataJson.getString("suc").equals("true")) {
					initAnimation(1);
					systemMsgCnt = 1;
				} else {
					systemMsgCnt = 0;
				}
			} else if (TextUtils.equals(statusCode, "1001")) {
				UIUtil.showToast(getActivity(), "身份登录信息失效");
				LogoutUtils.logout(getActivity());
			} else {
				UIUtil.showToast(getActivity(), "未知错误" + statusMsg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
