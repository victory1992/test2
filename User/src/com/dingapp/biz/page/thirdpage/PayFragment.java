package com.dingapp.biz.page.thirdpage;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.orm.AlipayConfBean;
import com.dingapp.biz.db.orm.PayOrderBean;
import com.dingapp.biz.db.orm.WxConfBean;
import com.dingapp.biz.net.SingleRequestQueue;
import com.dingapp.biz.net.StringPostRequest;
import com.dingapp.biz.pay.alipay.PayResult;
import com.dingapp.biz.pay.weixin.WxConstants;
import com.dingapp.biz.pay.weixin.WxPayReceiver;
import com.dingapp.biz.pay.weixin.WxPayReceiver.WxCallback;
import com.dingapp.biz.util.LogoutUtils;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.app.IPageSwitcher;
import com.dingapp.core.util.AndroidUtil;
import com.dingapp.core.util.UIUtil;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class PayFragment extends BaseFragment implements OnClickListener,
		WxCallback {
	private ImageView iv_back;
	private TextView tv_offline_pay;
	private RelativeLayout rl_zfb;
	private ImageView iv_zfb, iv_wx;
	private RelativeLayout rl_wx;
	private TextView tv_price;
	private TextView btn_sure;

	private enum PayType {
		ZFB, WX;
	}

	private PayType payType;
	private String order_id;
	private String price;
	// 支付
	/**
	 * 微信分享
	 */
	private IWXAPI wxApi;
	private MyHandler mHandler;
	/**
	 * 接收微信支付的结果
	 */
	private WxPayReceiver receiver;
	private RequestQueue mQueue;
	private Listener<String> payListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			Log.i("pay", response);
			parserData(response);
		}
	};
	private ErrorListener errorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {

		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.order_pay, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		wxApi = WXAPIFactory.createWXAPI(getActivity(), WxConstants.WX_APP_ID,
				true);
		wxApi.registerApp(WxConstants.WX_APP_ID);
		mQueue = SingleRequestQueue.getreRequestQueue(getActivity()
				.getApplicationContext());
		BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

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
		if (getArguments() != null
				&& getArguments().containsKey(AppConstants.KEY)) {
			order_id = getArguments().getString(AppConstants.KEY);
			price = getArguments().getString("price");
		}
		mHandler = new MyHandler(getActivity());
		receiver = new WxPayReceiver(PayFragment.this);
		IntentFilter filter = new IntentFilter();
		filter.addAction(AppConstants.WX_PAY_SUCCESS_ACTION);
		getActivity().registerReceiver(receiver, filter);
		initView();
		initListener();
	}

	private void initView() {
		iv_back = (ImageView) getView().findViewById(R.id.iv_left_back);
		rl_zfb = (RelativeLayout) getView().findViewById(R.id.ll_zfb);
		iv_zfb = (ImageView) getView().findViewById(R.id.iv_zfb);
		iv_wx = (ImageView) getView().findViewById(R.id.iv_wx);
		rl_wx = (RelativeLayout) getView().findViewById(R.id.ll_wx);
		tv_price = (TextView) getView().findViewById(R.id.tv_price);
		btn_sure = (TextView) getView().findViewById(R.id.tv_pay);
		tv_price.setText("￥" + price);
	}

	private void initListener() {
		iv_back.setOnClickListener(this);
		rl_wx.setOnClickListener(this);
		rl_zfb.setOnClickListener(this);
		btn_sure.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == iv_back) {
			popStack(null);
			return;
		}
		if (v == rl_wx) {
			iv_wx.setImageResource(R.drawable.pay_checked);
			iv_zfb.setImageResource(R.drawable.pay_unchecked);
			payType = PayType.WX;
			return;
		}
		if (v == rl_zfb) {
			iv_zfb.setImageResource(R.drawable.pay_checked);
			iv_wx.setImageResource(R.drawable.pay_unchecked);
			payType = PayType.ZFB;
			return;
		}
		if (v == btn_sure) {
			requestData();
			return;
		}
	}

	@Override
	public void callback() {

	}

	/**
	 * 请求支付
	 */
	private void requestData() {
		if (AndroidUtil.isNetworkAvailable(getActivity())) {
			String url = AppConstants.pay_order;
			HashMap<String, String> postMap = new HashMap<String, String>();
			postMap.put("session_id", AppConstants.member.getSessionId());
			postMap.put("platform", AppConstants.PLATFORM);
			postMap.put("order_id", order_id);
			if (payType == PayType.WX) {
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

	private void parserData(String response) {
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
					Bundle bundle = new Bundle();
					bundle.putBoolean("refresh", true);
					mPageSwitcher.popStack(bundle);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
				break;
			}
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			popStack(null);
			return true;
		}
		return false;
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(receiver);
		mQueue.cancelAll(this);
		super.onDestroy();
	}
}
