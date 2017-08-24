package com.dingapp.biz.page;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.orm.AlipayConfBean;
import com.dingapp.biz.db.orm.CartListBean;
import com.dingapp.biz.db.orm.OrderDetailBean;
import com.dingapp.biz.db.orm.OrederGoodsItemBean;
import com.dingapp.biz.db.orm.PayOrderBean;
import com.dingapp.biz.db.orm.WxConfBean;
import com.dingapp.biz.net.SingleRequestQueue;
import com.dingapp.biz.net.StringPostRequest;
import com.dingapp.biz.page.adapters.SureOrderCartAdapter;
import com.dingapp.biz.page.customview.MyListView;
import com.dingapp.biz.pay.alipay.PayResult;
import com.dingapp.biz.pay.weixin.WxConstants;
import com.dingapp.biz.pay.weixin.WxPayReceiver.WxCallback;
import com.dingapp.biz.util.LogoutUtils;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.app.IPageSwitcher;
import com.dingapp.core.util.AndroidUtil;
import com.dingapp.core.util.UIUtil;
import com.google.gson.Gson;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.dbcontacts.dbcontact.ContactDao;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class MemberCenterorderDetaiFragment extends BaseFragment implements
		OnClickListener, WxCallback {
	private ImageView img_back;
	/**
	 * 底部左边取消订单
	 */
	private TextView tv_order_status1;
	/**
	 * 底部右边取消订单
	 */
	private TextView tv_order_status2;
	/**
	 * 订单状态：待定制
	 */
	private TextView tv_order_statu;
	/**
	 * 订单编号：2015090122112
	 */
	private TextView tv_order_number;
	/**
	 * 下单时间：2016-02-26 09：16：23
	 */
	private TextView tv_order_time;
	/**
	 * 赠送积分
	 */
	private TextView tv_orderdetail_sendscore;
	private LinearLayout ll_orderdetail_express;
	// 收货方式
	private TextView tv_order_express_style;
	private TextView tv_order_contact;
	private MyListView my_lv_goods;
	private SureOrderCartAdapter goodsAdapter;
	private LinearLayout ll_order_status;
	private View inflate;
	private TextView tv_receiver_address;
	private TextView tv_express;
	private TextView tv_numgoods;
	private TextView tv_yunfei;
	private TextView tv_totalprice;
	private TextView tv_coupon_price;
	private TextView tv_buyer_massage;
	private View v_order_detail;
	// 退货拒绝理由
	private TextView tv_return_refuse;
	private LinearLayout ll_return_refuse;
	private RequestQueue mQueue;
	private String order_id;
	private Dialog cancled_dialog;
	private OrderDetailBean detailBean;
	private TextView tv_contact_shopper;
	// 支付
	private ImageView iv_zfb;
	private ImageView iv_wx;
	private PAY_TYPE payType;

	private enum PAY_TYPE {
		zfb, wx
	}

	private LinearLayout ll_pay;
	private RelativeLayout rl_zfb;
	private RelativeLayout rl_wx;
	// 判断是退货状态，是否显示确认收货按钮
	private String isVisableSureReceive = "true";
	/**
	 * 微信分享
	 */
	private IWXAPI wxApi;
	private MyHandler mHandler;
	/**
	 * 接收微信支付的结果
	 */
	private BroadcastReceiver broadcastReceiver;
	private Listener<String> payListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			Log.i("pay", response);
			parserPayData(response);
		}
	};
	private Listener<String> listener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parseData(response);
		}
	};
	private Listener<String> cancledListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parseCancledData(response);
		}
	};
	private Listener<String> confirmListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parseConfirmData(response);
		}
	};
	private ErrorListener errorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {

		}
	};
	private Dialog confirm_dialog;

	protected void parseData(String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			String statusCode = jsonObject.getString("statusCode");
			String statusMsg = jsonObject.getString("statusMsg");
			Gson gson = new Gson();
			if ("200".equals(statusCode)) {
				detailBean = gson.fromJson(jsonObject.getString("data"),
						OrderDetailBean.class);
				initData(detailBean);
			} else if ("1001".equals(statusCode)) {
				UIUtil.showToast(getActivity(), "登录信息失效，请重新登录！");
				LogoutUtils.logout(getActivity());
			} else {
				UIUtil.showToast(getActivity(), statusMsg);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void parseCancledData(String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			String statusCode = jsonObject.getString("statusCode");
			String statusMsg = jsonObject.getString("statusMsg");
			if ("200".equals(statusCode)) {
				if (jsonObject.has("data")) {
					JSONObject jsonObject_data = jsonObject
							.getJSONObject("data");
					if (jsonObject_data.has("suc")) {
						if (jsonObject_data.getBoolean("suc")) {
							cancled_dialog.dismiss();
							UIUtil.showToast(getActivity(), "取消订单成功");
							requestData();
						} else {
							cancled_dialog.dismiss();
							UIUtil.showToast(getActivity(), "取消订单失败,请重试！");
						}
					}
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

	protected void parseConfirmData(String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			String statusCode = jsonObject.getString("statusCode");
			String statusMsg = jsonObject.getString("statusMsg");
			if ("200".equals(statusCode)) {
				if (jsonObject.has("data")) {
					JSONObject jsonObject_data = jsonObject
							.getJSONObject("data");
					if (jsonObject_data.has("suc")) {
						if (jsonObject_data.getBoolean("suc")) {
							confirm_dialog.dismiss();
							UIUtil.showToast(getActivity(), "收货成功");
							requestData();
						} else {
							confirm_dialog.dismiss();
							UIUtil.showToast(getActivity(), "收货失败,请重试！");
						}
					}
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

	private void initData(final OrderDetailBean detailBean) {
		if (detailBean == null) {
			return;
		}
		if (detailBean.getMember_info() == null) {
			tv_contact_shopper.setVisibility(View.GONE);
		}
		ll_pay.setVisibility(View.GONE);
		tv_order_status1.setVisibility(View.GONE);
		tv_order_status2.setVisibility(View.GONE);
		if (detailBean.getStatus() != null) {
			v_order_detail.setVisibility(View.GONE);
			if ("returned".equals(detailBean.getStatus())) {
				tv_order_statu.setText("退货成功");
			}
			if ("paying".equals(detailBean.getStatus())) {
				v_order_detail.setVisibility(View.VISIBLE);
				ll_pay.setVisibility(View.VISIBLE);
				tv_contact_shopper.setVisibility(View.GONE);
				tv_order_statu.setText("待付款");
				ll_order_status.setVisibility(View.VISIBLE);
				tv_order_status1.setVisibility(View.VISIBLE);
				tv_order_status2.setVisibility(View.VISIBLE);
				tv_order_status1.setText("取消订单");
				tv_order_status2.setText("去支付");
				tv_order_status1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 取消订单
						showCancledDilog(detailBean.getOrder_id());
					}
				});
				tv_order_status2.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// 支付
						requestPayData();
					}
				});
			} else if ("shipping".equals(detailBean.getStatus())) {
				tv_order_statu.setText("待发货");
				ll_order_status.setVisibility(View.GONE);
			} else if ("receipting".equals(detailBean.getStatus())) {
				tv_order_statu.setText("待收货");
				ll_pay.setVisibility(View.GONE);
				if (isVisableSureReceive.equals("true")) {
					ll_order_status.setVisibility(View.VISIBLE);
					tv_order_status1.setVisibility(View.GONE);
					tv_order_status2.setVisibility(View.VISIBLE);
					tv_order_status2.setText("确认收货");
					tv_order_status2.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// 确认收货
							showComfirmDilog(detailBean.getOrder_id());
						}
					});
				}
			} else if ("evaluating".equals(detailBean.getStatus())) {
				tv_order_statu.setText("待评价");
				ll_order_status.setVisibility(View.VISIBLE);
				tv_order_status2.setVisibility(View.VISIBLE);
				tv_order_status1.setVisibility(View.GONE);
				tv_order_status2.setText("去评价");
				tv_order_status2.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 去评价
						Bundle bundle4 = new Bundle();
						bundle4.putString(AppConstants.KEY, order_id + "");
						ArrayList<String> picList = new ArrayList<String>();
						ArrayList<String> nameList = new ArrayList<String>();
						ArrayList<String> idList = new ArrayList<String>();
						List<OrederGoodsItemBean> list = detailBean
								.getGoods_list();
						for (int i = 0; i < list.size(); i++) {
							OrederGoodsItemBean goodsBean = list.get(i);
							picList.add(goodsBean.getGoods_pic() == null ? ""
									: goodsBean.getGoods_pic().getDetail_url());
							nameList.add(goodsBean.getGoods_name());
							idList.add(goodsBean.getGoods_id() + "");
						}
						bundle4.putStringArrayList("goodsPiclist", picList);
						bundle4.putStringArrayList("goodsNamelist", nameList);
						bundle4.putStringArrayList("goodsIdlist", idList);
						openPage("comment", bundle4, false);
					}
				});
			} else if ("finished".equals(detailBean.getStatus())) {
				tv_order_statu.setText("已完结");
				ll_order_status.setVisibility(View.GONE);
			} else if ("cancelled".equals(detailBean.getStatus())) {
				tv_order_statu.setText("已取消");
				ll_order_status.setVisibility(View.GONE);
			}
		}
		if (detailBean.getRefuse_msg() != null
				&& !TextUtils.isEmpty(detailBean.getRefuse_msg())) {
			tv_return_refuse.setText(detailBean.getRefuse_msg());
			ll_return_refuse.setVisibility(View.VISIBLE);
		}
		tv_order_number.setText(detailBean.getOrder_no());
		tv_order_time.setText(detailBean.getCreate_time());
		tv_order_contact.setText(detailBean.getReceiver_name() + "    "
				+ detailBean.getReceiver_mobile());
		List<OrederGoodsItemBean> goodsList = detailBean.getGoods_list();
		List<CartListBean> cartList = new ArrayList<CartListBean>();
		if (goodsList != null && goodsList.size() > 0) {
			for (int i = 0; i < goodsList.size(); i++) {
				CartListBean cartBean = new CartListBean();
				OrederGoodsItemBean goodsBean = goodsList.get(i);
				cartBean.setGoods_attrs(goodsBean.getGoods_attrs());
				cartBean.setCnt(goodsBean.getGoods_cnt());
				cartBean.setGoods_id(goodsBean.getGoods_id());
				cartBean.setGoods_title(goodsBean.getGoods_name());
				cartBean.setGoods_img(goodsBean.getGoods_pic());
				cartBean.setGoods_price(goodsBean.getGoods_price());
				// cartBean.setIs_vip(goodsBean.geti)
				cartList.add(cartBean);
			}
			goodsAdapter.setList(cartList);
		}
		if (detailBean.getBuyer_message() != null
				&& !TextUtils.isEmpty(detailBean.getBuyer_message())) {
			tv_buyer_massage.setText(detailBean.getBuyer_message());
		} else {
			tv_buyer_massage.setText("买家未留言");
		}
		int goods_cnt = 0;
		for (int i = 0; i < goodsList.size(); i++) {
			OrederGoodsItemBean bean = goodsList.get(i);
			goods_cnt = goods_cnt + bean.getGoods_cnt();
		}
		tv_orderdetail_sendscore.setText("赠送积分: " + detailBean.getGive_score());
		tv_numgoods.setText("共" + goods_cnt + "件商品");
		tv_yunfei.setText("运费:￥" + detailBean.getExpress_price());
		tv_totalprice.setText("￥" + detailBean.getPay_price());
		tv_receiver_address.setText(detailBean.getReceive_address());
		if (detailBean.getCoupon_price() != null
				&& detailBean.getCoupon_price() != 0.0) {
			tv_coupon_price.setText("已使用" + detailBean.getCoupon_price()
					+ "元优惠券");
		} else {
			tv_coupon_price.setText("未使用" + "优惠券");
		}
		if (detailBean.getDeliver_type() != null
				&& detailBean.getDeliver_type().equals("platform")) {
			ll_orderdetail_express.setVisibility(View.VISIBLE);
			tv_order_express_style.setText("平台发货");
			if (detailBean.getExpress_id() != null
					&& !TextUtils.isEmpty(detailBean.getExpress_id())) {
				tv_express.setText(detailBean.getExpress_name() + "  "
						+ detailBean.getExpress_id());
			} else {
				tv_express.setText(detailBean.getExpress_name());
			}
		} else {
			ll_orderdetail_express.setVisibility(View.GONE);
			tv_order_express_style.setText("到店服务");
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		inflate = View.inflate(getActivity(), R.layout.order_detail, null);
		return inflate;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments() != null && getArguments().containsKey("order_id")) {
			order_id = getArguments().getString("order_id");
			if (getArguments().containsKey("isVisableSureReceive")) {
				isVisableSureReceive = getArguments().getString(
						"isVisableSureReceive");
			}
		}

		mQueue = SingleRequestQueue.getreRequestQueue(getActivity());
		wxApi = WXAPIFactory.createWXAPI(getActivity(), WxConstants.WX_APP_ID,
				true);
		wxApi.registerApp(WxConstants.WX_APP_ID);
		mQueue = SingleRequestQueue.getreRequestQueue(getActivity());
		broadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action.equals(AppConstants.WX_PAY_SUCCESS_ACTION)) {
					popStack(null);
				}
			}
		};
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(AppConstants.WX_PAY_SUCCESS_ACTION);
		getActivity().registerReceiver(broadcastReceiver, intentFilter);
		mHandler = new MyHandler(getActivity());
		initView(getView());
		initListener();
		goodsAdapter = new SureOrderCartAdapter(getActivity(),
				new ArrayList<CartListBean>());
		my_lv_goods.setFocusable(false);
		my_lv_goods.setAdapter(goodsAdapter);
		requestData();
	}

	private void requestData() {
		if (AndroidUtil.isNetworkAvailable(getActivity())) {
			Map<String, String> postMap = new HashMap<String, String>();
			String url = AppConstants.BaseUrl + AppConstants.ORDER_DETAIL;
			postMap.put("session_id", AppConstants.member == null ? ""
					: AppConstants.member.getSessionId());
			postMap.put("platform", AppConstants.PLATFORM);
			postMap.put("order_id", order_id);
			postMap.put("type", "member");
			StringRequest sr = new StringPostRequest(postMap, url, listener,
					errorListener);
			mQueue.add(sr);
		} else {
			UIUtil.showToast(getActivity(), AppConstants.NetNotifice);
		}
	}

	private void initListener() {
		img_back.setOnClickListener(this);
		tv_order_status1.setOnClickListener(this);
		tv_order_status2.setOnClickListener(this);
		rl_wx.setOnClickListener(this);
		rl_zfb.setOnClickListener(this);
		my_lv_goods.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				CartListBean bean = (CartListBean) goodsAdapter
						.getItem((int) arg3);
				Bundle bundle = new Bundle();
				bundle.putInt("prd_id", bean.getGoods_id());
				openPage("goods_detail_pager", bundle, true);
			}
		});
		tv_contact_shopper.setOnClickListener(this);
	}

	private void initView(View v) {
		tv_receiver_address = (TextView) v
				.findViewById(R.id.tv_order_receiveraddress);
		tv_express = (TextView) v.findViewById(R.id.tv_order_express);
		tv_numgoods = (TextView) v.findViewById(R.id.tv_orderdetail_numgoods);
		tv_yunfei = (TextView) v.findViewById(R.id.tv_orderdetail_yunfei);
		tv_totalprice = (TextView) v
				.findViewById(R.id.tv_orderdetail_totalprice);
		tv_coupon_price = (TextView) v.findViewById(R.id.tv_sureorder_discount);
		img_back = (ImageView) v.findViewById(R.id.img_back);
		tv_order_status1 = (TextView) getView().findViewById(
				R.id.tv__order_status1);
		tv_order_status2 = (TextView) v.findViewById(R.id.tv_order_status2);
		tv_order_statu = (TextView) v.findViewById(R.id.tv_order_statu);
		tv_order_number = (TextView) v.findViewById(R.id.tv_order_number);
		tv_order_time = (TextView) v.findViewById(R.id.tv_order_time);
		tv_order_contact = (TextView) v.findViewById(R.id.tv_order_receiveman);
		my_lv_goods = (MyListView) v.findViewById(R.id.lv_orderdetail);
		ll_order_status = (LinearLayout) v.findViewById(R.id.ll_order_status);
		v_order_detail = v.findViewById(R.id.v_order_detail);
		tv_buyer_massage = (TextView) v
				.findViewById(R.id.tv_order_buyermessage);
		tv_contact_shopper = (TextView) v
				.findViewById(R.id.tv_orderdetail_contactshopper);
		tv_return_refuse = (TextView) v
				.findViewById(R.id.tv_order_refusereason);
		ll_return_refuse = (LinearLayout) v
				.findViewById(R.id.ll_orderdetail_refusereason);
		ll_pay = (LinearLayout) v.findViewById(R.id.ll_pay);
		rl_zfb = (RelativeLayout) v.findViewById(R.id.rl_zfb);
		rl_wx = (RelativeLayout) v.findViewById(R.id.rl_wx);
		iv_zfb = (ImageView) v.findViewById(R.id.iv_zfb);
		iv_wx = (ImageView) v.findViewById(R.id.iv_wx);
		tv_order_express_style = (TextView) v
				.findViewById(R.id.tv_order_express_style);
		ll_orderdetail_express = (LinearLayout) v
				.findViewById(R.id.ll_orderdetail_express);
		tv_orderdetail_sendscore = (TextView) v
				.findViewById(R.id.tv_orderdetail_sendscore);
	}

	@Override
	public void onClick(View v) {
		if (v == img_back) {
			popStack(null);
			return;
		}
		if (v == tv_contact_shopper) {
			if (detailBean.getMember_info() != null) {
				EaseUser user = new EaseUser("merchant"
						+ detailBean.getMember_info().getMember_id());
				user.setNickName(detailBean.getMember_info().getMember_nick());
				if (detailBean.getMember_info().getMember_header() != null
						&& !TextUtils.isEmpty(detailBean.getMember_info()
								.getMember_header().getDetail_url())) {
					user.setAvatar(detailBean.getMember_info()
							.getMember_header().getDetail_url());
				}
				user.setIs_corp("true");
				if (new ContactDao(getActivity()).update(user) == 0) {
					new ContactDao(getActivity()).add(user);
				}
				EaseUserUtils.helpMap.put("merchant"
						+ detailBean.getMember_info().getMember_id(), user);
			}
			showContactShopperDialog();
			return;
		}
		if (v == ll_chat_contact) {
			if (dialog_contact != null) {
				dialog_contact.dismiss();
				// 聊天
				if (detailBean.getMember_info() == null) {
					UIUtil.showToast(getActivity(), "暂未匹配到商家");
					return;
				}
				Bundle bundle = new Bundle();
				bundle.putString(EaseConstant.EXTRA_USER_ID, detailBean
						.getMember_info().getHx_id());
				bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE,
						EaseConstant.CHATTYPE_SINGLE);
				openPage("wechat_chat", bundle, false);
			}
			return;
		}
		if (v == tv_cancle_contact) {
			if (dialog_contact != null) {
				dialog_contact.dismiss();
			}

			return;
		}
		if (v == ll_phone_contact) {
			if (dialog_contact != null) {
				dialog_contact.dismiss();
			}
			// 打电话
			if (detailBean.getMember_info() == null) {
				UIUtil.showToast(getActivity(), "暂未匹配到商家");
				return;
			}
			String phone = detailBean.getMember_info().getMember_mobile();
			if (phone != null && !TextUtils.isEmpty(phone)) {
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
						+ phone));
				startActivity(intent);
			} else {
				UIUtil.showToast(getActivity(), "商家电话不可用");
			}
			return;
		}
		if (v == rl_zfb) {
			payType = PAY_TYPE.zfb;
			iv_zfb.setImageResource(R.drawable.pay_checked);
			iv_wx.setImageResource(R.drawable.pay_unchecked);
			return;
		} else if (v == rl_wx) {
			payType = PAY_TYPE.wx;
			iv_zfb.setImageResource(R.drawable.pay_unchecked);
			iv_wx.setImageResource(R.drawable.pay_checked);
			return;
		}
	}

	private void showCancledDilog(final int id) {
		cancled_dialog = new Dialog(getActivity(), R.style.dialog_style);
		View inflate = View.inflate(getActivity(), R.layout.cancled_layout,
				null);
		((TextView) (inflate.findViewById(R.id.tv_content)))
				.setText("你确定要取消订单吗？");
		inflate.findViewById(R.id.tv_cancled).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						cancled_dialog.dismiss();
					}
				});
		inflate.findViewById(R.id.tv_confirm).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						requestCancledOrder(id);
					}
				});
		cancled_dialog.setContentView(inflate);
		Window window = cancled_dialog.getWindow();
		window.setBackgroundDrawable(new BitmapDrawable());
		window.setGravity(Gravity.CENTER);
		LayoutParams params = window.getAttributes();
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		cancled_dialog.show();
	}

	// 联系商家弹出框
	private Dialog dialog_contact;
	private LinearLayout ll_chat_contact;
	private LinearLayout ll_phone_contact;
	private TextView tv_cancle_contact;

	private void showContactShopperDialog() {
		if (dialog_contact == null) {
			dialog_contact = new Dialog(getActivity(), R.style.dialog_style);
			View view = View.inflate(getActivity(), R.layout.contact_dialog,
					null);
			ll_chat_contact = (LinearLayout) view
					.findViewById(R.id.ll_chat_dialog);
			ll_phone_contact = (LinearLayout) view
					.findViewById(R.id.ll_phone_dialog);
			tv_cancle_contact = (TextView) view.findViewById(R.id.cancle_share);
			ll_chat_contact.setOnClickListener(this);
			ll_phone_contact.setOnClickListener(this);
			tv_cancle_contact.setOnClickListener(this);
			dialog_contact.setContentView(view);
			Window window = dialog_contact.getWindow();
			window.setBackgroundDrawable(new BitmapDrawable());
			window.setGravity(Gravity.BOTTOM);
			LayoutParams params = window.getAttributes();
			params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
			params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
		}
		dialog_contact.show();
	}

	private void showComfirmDilog(final int id) {
		confirm_dialog = new Dialog(getActivity(), R.style.dialog_style);
		View inflate = View.inflate(getActivity(), R.layout.cancled_layout,
				null);
		((TextView) (inflate.findViewById(R.id.tv_content)))
				.setText("你确定货已经收到吗？");
		inflate.findViewById(R.id.tv_cancled).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						confirm_dialog.dismiss();
					}
				});
		inflate.findViewById(R.id.tv_confirm).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						requestConfirmOrder(id);
					}
				});
		confirm_dialog.setContentView(inflate);
		Window window = confirm_dialog.getWindow();
		window.setBackgroundDrawable(new BitmapDrawable());
		window.setGravity(Gravity.CENTER);
		LayoutParams params = window.getAttributes();
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		confirm_dialog.show();
	}

	protected void requestConfirmOrder(int id) {
		if (AndroidUtil.isNetworkAvailable(getActivity())) {
			String url = AppConstants.BaseUrl
					+ "/api/v1/goods/order_confirm_receipt";
			Map<String, String> postMap = new HashMap<String, String>();
			postMap.put("platform", AppConstants.PLATFORM);
			postMap.put(
					"session_id",
					AppConstants.member != null ? AppConstants.member
							.getSessionId() : "");
			postMap.put("order_id", id + "");
			StringPostRequest stringPostRequest = new StringPostRequest(
					postMap, url, confirmListener, errorListener);
			mQueue.add(stringPostRequest);
		} else {
			UIUtil.showToast(getActivity(), AppConstants.NetNotifice);
		}
	}

	protected void requestCancledOrder(int id) {
		if (AndroidUtil.isNetworkAvailable(getActivity())) {
			String url = AppConstants.BaseUrl + "/api/v1/goods/order_cancel";
			Map<String, String> postMap = new HashMap<String, String>();
			postMap.put("platform", AppConstants.PLATFORM);
			postMap.put(
					"session_id",
					AppConstants.member != null ? AppConstants.member
							.getSessionId() : "");
			postMap.put("order_id", id + "");
			StringPostRequest stringPostRequest = new StringPostRequest(
					postMap, url, cancledListener, errorListener);
			mQueue.add(stringPostRequest);
		} else {
			UIUtil.showToast(getActivity(), AppConstants.NetNotifice);
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			requestData();
		}
	}

	@Override
	public void callback() {
	}

	/**
	 * 请求支付
	 */
	private void requestPayData() {
		if (AndroidUtil.isNetworkAvailable(getActivity())) {
			String url = AppConstants.pay_order;
			HashMap<String, String> postMap = new HashMap<String, String>();
			postMap.put("session_id", AppConstants.member.getSessionId());
			postMap.put("platform", AppConstants.PLATFORM);
			postMap.put("order_id", order_id);
			if (payType == PAY_TYPE.wx) {
				postMap.put("pay_type", "wxpay");
			} else {
				postMap.put("pay_type", "alipay");
			}
			StringPostRequest request = new StringPostRequest(postMap,
					AppConstants.BaseUrl + url, payListener, errorListener);
			mQueue.add(request);
		} else {
			UIUtil.showToast(getActivity(), AppConstants.NetNotifice);
		}
	}

	private void parserPayData(String response) {
		try {
			JSONObject jsonObj = new JSONObject(response);
			String statusCode = jsonObj.getString("statusCode");
			String statusMsg = jsonObj.getString("statusMsg");
			if (TextUtils.equals(statusCode, "200")) {
				UIUtil.showToast(getActivity(), "正在唤起支付界面，请等待...");
				JSONObject dataJson = jsonObj.getJSONObject("data");
				PayOrderBean payBean = new PayOrderBean();
				if (dataJson.has("alipay_conf")) {
					AlipayConfBean alipayBean = new AlipayConfBean();
					JSONObject aliJson = dataJson.getJSONObject("alipay_conf");
					if (aliJson.has("checkcode")) {
						alipayBean.setCheckcode(aliJson.getString("checkcode"));
					}
					if (aliJson.has("param")) {
						alipayBean.setParam(aliJson.getString("param"));
					}
					payBean.setAlipay_conf(alipayBean);
					serverSignPay("0", payBean);
				}
				if (dataJson.has("app_wx_conf")) {
					WxConfBean wxBean = new WxConfBean();
					JSONObject wxJson = dataJson.getJSONObject("app_wx_conf");
					if (wxJson.has("app_id")) {
						wxBean.setApp_id(wxJson.getString("app_id"));
					}
					if (wxJson.has("checkcode")) {
						wxBean.setCheckcode(wxJson.getString("checkcode"));
					}
					if (wxJson.has("nonce_str")) {
						wxBean.setNonce_str(wxJson.getString("nonce_str"));
					}
					if (wxJson.has("package")) {
						wxBean.setPackagename(wxJson.getString("package"));
					}
					if (wxJson.has("partner_id")) {
						wxBean.setPartner_id(wxJson.getString("partner_id"));
					}
					if (wxJson.has("prepay_id")) {
						wxBean.setPrepay_id(wxJson.getString("prepay_id"));
					}
					if (wxJson.has("sign")) {
						wxBean.setSign(wxJson.getString("sign"));
					}
					if (wxJson.has("timestamp")) {
						wxBean.setTimestamp(wxJson.getString("timestamp"));
					}
					payBean.setWx_conf(wxBean);
					serverSignPay("1", payBean);
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

	/**
	 * 服务器签名 支付
	 * 
	 * @param service_sign
	 *            服务器签名
	 */
	public void serverSignPay(String pay_type, PayOrderBean payBean) {
		if (pay_type.equals("0")) {
			final AlipayConfBean alipayBean = payBean.getAlipay_conf();
			Runnable payRunnable = new Runnable() {
				@Override
				public void run() {
					// 构造PayTask 对象
					PayTask alipay = new PayTask(getActivity());
					// 调用支付接口，获取支付结果
					String result = alipay.pay(alipayBean.getParam());
					Message msg = new Message();
					msg.what = 1;
					msg.obj = result;
					mHandler.sendMessage(msg);
				}
			};
			// 必须异步调用
			Thread payThread = new Thread(payRunnable);
			payThread.start();
		} else {
			WxConfBean wxBean = payBean.getWx_conf();
			PayReq req = new PayReq();
			req.appId = wxBean.getApp_id();
			req.partnerId = wxBean.getPartner_id();
			req.prepayId = wxBean.getPrepay_id();
			req.packageValue = wxBean.getPackagename();
			req.nonceStr = wxBean.getNonce_str();
			req.timeStamp = wxBean.getTimestamp();
			req.sign = wxBean.getSign();
			wxApi.sendReq(req);
		}
	}

	/**
	 * 
	 * @author
	 * 
	 */
	private static class MyHandler extends Handler {
		WeakReference<Context> ref;

		MyHandler(Context context) {
			ref = new WeakReference<Context>(context);
		}

		@Override
		public void handleMessage(Message msg) {
			Context act = ref.get();
			if (act == null) {

				return;
			}
			switch (msg.what) {
			case 1: {
				PayResult payResult = new PayResult((String) msg.obj);
				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();
				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					UIUtil.showToast(act, "支付成功");
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						UIUtil.showToast(act, "支付结果确认中");
					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						UIUtil.showToast(act, "支付失败");
					}
				}
				try {
					IPageSwitcher mPageSwitcher = (IPageSwitcher) act;
					mPageSwitcher.popBack(null);
				} catch (Exception e) {
				}
			}
				break;
			}
		}
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(broadcastReceiver);
		mQueue.cancelAll(this);
		super.onDestroy();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			popStack(null);
			return true;
		}
		return false;
	}
}
