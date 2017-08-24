package com.dingapp.biz.page.thirdpage;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.android.volley.Response.Listener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.orm.AddressBean;
import com.dingapp.biz.db.orm.AlipayConfBean;
import com.dingapp.biz.db.orm.CartListBean;
import com.dingapp.biz.db.orm.CouponBean;
import com.dingapp.biz.db.orm.CouponTotalBean;
import com.dingapp.biz.db.orm.OrderPrdAttrsBean;
import com.dingapp.biz.db.orm.PayOrderBean;
import com.dingapp.biz.db.orm.ShopListBean;
import com.dingapp.biz.db.orm.WxConfBean;
import com.dingapp.biz.net.RequestDataUtil;
import com.dingapp.biz.page.adapters.DiscountListViewAdapter;
import com.dingapp.biz.page.adapters.SureOrderCartAdapter;
import com.dingapp.biz.pay.LogUtils;
import com.dingapp.biz.pay.alipay.PayResult;
import com.dingapp.biz.pay.weixin.WxConstants;
import com.dingapp.biz.pay.weixin.WxPayReceiver.WxCallback;
import com.dingapp.biz.util.LogoutUtils;
import com.dingapp.biz.util.Utils;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.app.IPageSwitcher;
import com.dingapp.core.app.PageJumper;
import com.dingapp.core.db.dao.MemberDao;
import com.dingapp.core.util.UIUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 确认订单页
 * 
 * @author king
 * 
 */
public class SureOrderFragment extends BaseFragment implements OnClickListener,
		WxCallback {
	private ImageView iv_back;
	private TextView tv_price;
	private TextView tv_num;
	private ListView lv_sureorder;
	private TextView tv_address;
	private ArrayList<AddressBean> mList;
	private TextView tv_sureorder_yunfei;
	private LinearLayout rl_address1;
	private LinearLayout rl_address2;
	private TextView tv_name;
	private TextView tv_phone;
	private TextView tv_address_detail;
	private TextView tv_discount;
	// 到店服务和平台发货
	private TextView tv_platform;
	private TextView tv_toshops;
	private RelativeLayout rl_zfb;
	private RelativeLayout rl_wx;
	private ImageView iv_zfb;
	private ImageView iv_wx;
	private TextView tv_sure;
	private TextView tv_choose_logistics;
	private TextView tv_sureorder_shoppers;
	private View v_sureorder_line;
	private LinearLayout ll_sureoder_shopers;
	// 到店服务不需要快递公司
	private LinearLayout ll_choose_logistics;
	private View v_line_logistics;
	private TextView tv_sureorder_sendscore;
	private int send_socre = 0;
	private int numGoods = 0;
	private double total_weiht = 0.0;
	private AddressBean returnBean;
	private int merchant_id = -1;

	// 定制，普通电商，购物车
	public enum SURE_PAYTYPE {
		CUSTOM, NORMAL, CARTLIST
	}

	private String sure_paytype = SURE_PAYTYPE.CUSTOM.name();

	private enum PAY_TYPE {
		alipay, wxpay
	}

	private enum SHOP_TYPE {
		platform, toshopper
	}

	private SHOP_TYPE shopType = SHOP_TYPE.platform;
	private PAY_TYPE payType = PAY_TYPE.alipay;
	// 优惠券
	private Dialog dialog;
	private ListView lv_discount_dialog;
	private TextView tv_no_used_discount;
	private DiscountListViewAdapter discountListViewAdapter;
	private double cuponValue;
	/**
	 * 选择的优惠券Bean
	 */
	private CouponBean selected_coupon;
	private List<CouponBean> couponList = new ArrayList<CouponBean>();
	/**
	 * 商品价格
	 */
	private EditText et_extramsg;
	private View view;
	public String order_id;
	// 地图的反地理编码
	private GeoCoder mPoiSearch;
	private String geo_city = "";
	private String geo_address = "";
	private String geo_xian = "";
	private double lat = -1;
	private double lng = -1;
	// 只进行两次地理编码
	private boolean isSecondTime = false;
	// 默认地址
	private Listener<String> alddressListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parseAddressData(response);
		}
	};
	// 优惠券列表
	private Listener<String> couponListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			LogUtils.i(SureOrderFragment.class.getSimpleName(), response);
			parserCouponData(response);
		}
	};
	// 商家列表
	private Listener<String> shopperListListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserShopersData(response);
		}
	};
	// 保存订单接口
	private Listener<String> orderSaveListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			LogUtils.i(SureOrderFragment.class.getSimpleName(), response);
			parserOrderSave(response);
		}
	};
	// 支付接口
	/**
	 * 微信分享
	 */
	private IWXAPI wxApi;
	private MyHandler mpayHandler;
	private BroadcastReceiver broadcastReceiver;
	private Listener<String> payListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserPay(response);
		}
	};
	// 保存订单所需要的参数
	private String address_id;
	private long coupon_id = -1;
	private String extraMsg;
	private String goods_id = "";
	private double allPrice = 0.0;
	private String express_id;
	private String express_name;
	// 到店服务
	private String shop_name;
	private int shop_id = -1;
	// 运费
	private double yuefei = 0.0;
	// 上界面传过来的cart_list
	private ArrayList<CartListBean> cartList = new ArrayList<CartListBean>();
	private SureOrderCartAdapter cartAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.sure_order, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments() != null) {
			if (getArguments().containsKey("cart_list")) {
				cartList = getArguments().getParcelableArrayList("cart_list");
			}
			if (getArguments().containsKey("sure_paytype")) {
				sure_paytype = getArguments().getString("sure_paytype");
			}
		}
		mPoiSearch = GeoCoder.newInstance();
		mPoiSearch.setOnGetGeoCodeResultListener(geocoderListener);
		wxApi = WXAPIFactory.createWXAPI(getActivity(), WxConstants.WX_APP_ID,
				true);
		wxApi.registerApp(WxConstants.WX_APP_ID);
		broadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action.equals(AppConstants.WX_PAY_SUCCESS_ACTION)) {
					Bundle bundle = new Bundle();
					bundle.putString("order_id", order_id);
					openPage("order_detail", bundle, false);
				}
			}

		};
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(AppConstants.WX_PAY_SUCCESS_ACTION);
		getActivity().registerReceiver(broadcastReceiver, intentFilter);
		mpayHandler = new MyHandler(getActivity());
		initView(getView());
		initListener();
		cartAdapter = new SureOrderCartAdapter(getActivity(), cartList);
		lv_sureorder.setAdapter(cartAdapter);
		requestCouponList();
		requestAddressData();
	}

	@Override
	protected void onDataReset(Bundle bundle) {
		super.onDataReset(bundle);
		if (bundle != null) {
			if (bundle.containsKey("shop_name")) {
				tv_sureorder_shoppers.setText(bundle.getString("shop_name"));
			}
			if (bundle.containsKey("shop_id")) {
				shop_id = bundle.getInt("shop_id");
			}
			if (bundle.containsKey("address_info")) {
				returnBean = bundle.getParcelable("address_info");
			}
			if (bundle.containsKey("address_refresh")) {
				requestAddressData();
			}
			if (bundle.containsKey("express_id")) {
				express_id = bundle.getString("express_id");
			}
			if (bundle.containsKey("total_price")) {
				yuefei = bundle.getDouble("total_price");
				tv_sureorder_yunfei.setText("运费" + yuefei + "元");
				initPriceTotla();
			}
			if (bundle.containsKey("express_name")) {
				express_name = bundle.getString("express_name");
				if (tv_choose_logistics != null) {
					tv_choose_logistics.setText(express_name);
				}
			}
		}
	}

	private void initView(View v) {
		iv_back = (ImageView) v.findViewById(R.id.img_sureorder_back);
		tv_price = (TextView) v.findViewById(R.id.tv_sureorder_totalprice);
		tv_num = (TextView) v.findViewById(R.id.tv_sureorder_numgoods);
		tv_address = (TextView) v.findViewById(R.id.tv_sureorder_address1);
		rl_address1 = (LinearLayout) v.findViewById(R.id.ll_sureorder_address1);
		rl_address2 = (LinearLayout) v.findViewById(R.id.ll_sureorder_address2);
		tv_name = (TextView) v.findViewById(R.id.tv_sureorder_name);
		tv_phone = (TextView) v.findViewById(R.id.tv_sureorder_phone);
		tv_address_detail = (TextView) v
				.findViewById(R.id.tv_sureorder_address2);
		tv_discount = (TextView) v.findViewById(R.id.tv_sureorder_discount);
		rl_zfb = (RelativeLayout) v.findViewById(R.id.rl_zfb);
		rl_wx = (RelativeLayout) v.findViewById(R.id.rl_wx);
		iv_zfb = (ImageView) v.findViewById(R.id.iv_zfb);
		iv_wx = (ImageView) v.findViewById(R.id.iv_wx);
		et_extramsg = (EditText) v.findViewById(R.id.et_sureorder_extramsg);
		tv_choose_logistics = (TextView) v
				.findViewById(R.id.tv_sureorder_wuliu);
		tv_sureorder_yunfei = (TextView) v
				.findViewById(R.id.tv_sureorder_yunfei);
		lv_sureorder = (ListView) v.findViewById(R.id.lv_sureorder);
		tv_sureorder_shoppers = (TextView) v
				.findViewById(R.id.tv_sureorder_shoppers);
		v_sureorder_line = v.findViewById(R.id.v_sureorder_line);
		ll_sureoder_shopers = (LinearLayout) v
				.findViewById(R.id.ll_sureoder_shopers);
		ll_choose_logistics = (LinearLayout) v
				.findViewById(R.id.ll_choose_logistic);
		v_line_logistics = v.findViewById(R.id.view_choose_logistic);
		tv_sureorder_sendscore = (TextView) v
				.findViewById(R.id.tv_sureorder_sendscore);
		lv_sureorder.setFocusable(false);
		for (int i = 0; i < cartList.size(); i++) {
			CartListBean bean = cartList.get(i);
			if (bean.getIs_select() != null
					&& bean.getIs_select().equals("true")) {

				numGoods = numGoods + bean.getCnt();
				total_weiht = total_weiht + bean.getCnt()
						* bean.getGoods_weight();
				send_socre = send_socre + bean.getSend_score();
				if (bean.getIs_vip() != null && bean.getIs_vip().equals("true")) {
					allPrice = allPrice + bean.getCnt() * bean.getVip_price();
				} else {
					allPrice = allPrice + bean.getCnt() * bean.getGoods_price();
				}

			}
		}
		tv_price.setText("￥" + Utils.keepDouble2(allPrice));
		tv_num.setText("共" + numGoods + "件商品");
		tv_sureorder_sendscore.setText("赠送积分: " + send_socre);
		tv_sure = (TextView) v.findViewById(R.id.tv_sureorder_sure);
		tv_toshops = (TextView) v.findViewById(R.id.tv_sureorder_toshop);
		tv_platform = (TextView) v.findViewById(R.id.tv_sureorder_platform);
	}

	private void initListener() {
		iv_back.setOnClickListener(this);
		tv_address.setOnClickListener(this);
		rl_address2.setOnClickListener(this);
		tv_discount.setOnClickListener(this);
		rl_zfb.setOnClickListener(this);
		tv_choose_logistics.setOnClickListener(this);
		tv_toshops.setOnClickListener(this);
		tv_platform.setOnClickListener(this);
		rl_wx.setOnClickListener(this);
		tv_sure.setOnClickListener(this);
		tv_sureorder_shoppers.setOnClickListener(this);
		lv_sureorder.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				CartListBean bean = (CartListBean) cartAdapter
						.getItem((int) arg3);
				Bundle bundle = new Bundle();
				bundle.putInt("prd_id", bean.getGoods_id());
				openPage("goods_detail_pager", bundle, true);
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v == iv_back) {
			popBack(null);
			return;
		}
		if (v == tv_choose_logistics) {
			Bundle bundle = new Bundle();
			bundle.putInt("num_goods", numGoods);
			bundle.putDouble("total_weight", total_weiht);
			bundle.putDouble("total_price", allPrice);
			openPage("choose_logistics", bundle, false);
		}
		if (v == tv_address) {
			openPage("select_address", null, false);
			return;
		}
		if (v == rl_address2) {
			openPage("select_address", null, false);
			return;
		}
		if (v == tv_discount) {
			showDiscountDialog();
			return;
		}
		if (v == rl_zfb) {
			payType = PAY_TYPE.alipay;
			iv_zfb.setImageResource(R.drawable.pay_checked);
			iv_wx.setImageResource(R.drawable.pay_unchecked);
			return;
		}
		if (v == rl_wx) {
			payType = PAY_TYPE.wxpay;
			iv_zfb.setImageResource(R.drawable.pay_unchecked);
			iv_wx.setImageResource(R.drawable.pay_checked);
			return;
		}
		if (v == tv_sure) {
			requestCreateOrder();
			return;
		}
		if (v == tv_no_used_discount) {
			tv_discount.setText("未使用优惠券");
			selected_coupon = null;
			coupon_id = -1;
			cuponValue = 0.0;
			initPriceTotla();
			dialog.dismiss();
			return;
		}
		if (v == tv_toshops) {
			tv_toshops.setTextColor(0xfff66e00);
			tv_platform.setTextColor(0xff333333);
			tv_toshops.setBackgroundResource(R.drawable.iv_order_yellow);
			tv_platform.setBackgroundResource(R.drawable.iv_order_gray);
			v_sureorder_line.setVisibility(View.VISIBLE);
			ll_sureoder_shopers.setVisibility(View.VISIBLE);
			ll_choose_logistics.setVisibility(View.GONE);
			v_line_logistics.setVisibility(View.GONE);
			shopType = SHOP_TYPE.toshopper;
			return;
		}
		if (v == tv_platform) {
			tv_toshops.setTextColor(0xff333333);
			tv_platform.setTextColor(0xfff66e00);
			tv_toshops.setBackgroundResource(R.drawable.iv_order_gray);
			tv_platform.setBackgroundResource(R.drawable.iv_order_yellow);
			v_sureorder_line.setVisibility(View.GONE);
			ll_sureoder_shopers.setVisibility(View.GONE);
			ll_choose_logistics.setVisibility(View.VISIBLE);
			v_line_logistics.setVisibility(View.VISIBLE);
			shopType = SHOP_TYPE.platform;
			return;
		}
		if (v == tv_sureorder_shoppers) {
			if (address_id != null && !TextUtils.isEmpty(address_id)) {
				Bundle bundle = new Bundle();
				bundle.putString("address_id", address_id);
				bundle.putString("lat", lat + "");
				bundle.putString("lng", lng + "");
				openPage("choose_shops", bundle, false);
			} else {
				UIUtil.showToast(getActivity(), "请先选择配送地址");
				return;
			}
			return;
		}
	}

	/**
	 * 展示优惠券信息
	 */
	@SuppressWarnings("deprecation")
	private void showDiscountDialog() {
		if (dialog == null) {
			dialog = new Dialog(getActivity(), R.style.dialog_style);
			View view = View.inflate(getActivity(), R.layout.discount_view,
					null);
			lv_discount_dialog = (ListView) view
					.findViewById(R.id.lv_discount_dialog);

			tv_no_used_discount = (TextView) view
					.findViewById(R.id.tv_no_used_discount);
			discountListViewAdapter = new DiscountListViewAdapter(getActivity());
			if (couponList != null) {
				discountListViewAdapter.setData(couponList);
			}
			lv_discount_dialog.setAdapter(discountListViewAdapter);
			lv_discount_dialog
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int position, long arg3) {

							discountListViewAdapter
									.setSelectedPosition(position);
							discountListViewAdapter.notifyDataSetChanged();
							selected_coupon = discountListViewAdapter
									.getItem((int) arg3);
							cuponValue = selected_coupon.getMoney();
							initPriceTotla();
							coupon_id = selected_coupon.getCoupon_id();
							tv_discount.setText("已使用"
									+ Utils.keepDouble2(selected_coupon
											.getMoney()) + "元优惠券");
							mHandler.sendEmptyMessage(100);
						}
					});
			tv_no_used_discount.setOnClickListener(this);
			dialog.setContentView(view);
			Window window = dialog.getWindow();
			window.setBackgroundDrawable(new BitmapDrawable());
			window.setGravity(Gravity.BOTTOM);
			LayoutParams params = window.getAttributes();
			params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
			params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
		}

		if (couponList == null || couponList.size() == 0) {
			UIUtil.showToast(getActivity(), "您无可用优惠券！");
		} else if (couponList != null || couponList.size() > 0) {
			dialog.show();
		}

	}

	/**
	 * 初始化总价信息。，选择了优惠券的时候需要变化
	 */
	private void initPriceTotla() {

		double newallPrice = allPrice - cuponValue;
		if (newallPrice <= 0) {
			if (yuefei <= 0) {
				tv_price.setText("￥0.01");
			} else {
				tv_price.setText("￥" + Utils.keepDouble2(yuefei));
			}
		} else {
			tv_price.setText("￥" + Utils.keepDouble2(newallPrice + yuefei));
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (dialog != null) {
				dialog.dismiss();
			}
		};
	};

	private void initDefaultAddress(AddressBean bean) {
		if (bean == null) {
			rl_address1.setVisibility(View.VISIBLE);
			rl_address2.setVisibility(View.GONE);
			address_id = "";
			return;
		}
		address_id = bean.getAddress_id() + "";
		rl_address1.setVisibility(View.GONE);
		rl_address2.setVisibility(View.VISIBLE);
		if (bean.getReceiver_mobile() != null) {
			tv_phone.setText(bean.getReceiver_mobile());
		}
		if (bean.getReceiver_name() != null) {
			tv_name.setText("联系人: " + bean.getReceiver_name());
		}
		tv_address_detail.setText(bean.getProvince_name() + bean.getCity_name()
				+ bean.getCounty_name() + bean.getAddress());
		geo_address = bean.getAddress();
		geo_xian = bean.getCounty_name();
		geo_city = bean.getCity_name();
		isSecondTime = false;
		mPoiSearch.geocode(new GeoCodeOption().city(geo_city).address(
				geo_xian + geo_address));
	}

	private OnGetGeoCoderResultListener geocoderListener = new OnGetGeoCoderResultListener() {

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {

		}

		@Override
		public void onGetGeoCodeResult(GeoCodeResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR
					|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
				// 没有检索到结果
				if (!isSecondTime) {
					mPoiSearch.geocode(new GeoCodeOption().city(geo_city)
							.address(geo_xian));
				}
				isSecondTime = true;
				return;
			}
			// 获取地理编码结果
			LatLng ll = result.getLocation();
			lat = ll.latitude;
			lng = ll.longitude;
			requestShopperList();
		}
	};

	// 请求匹配的商家列表
	private void requestShopperList() {
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("address_id", address_id);
		postMap.put("match_mode", "platform");
		postMap.put("longitude", lng + "");
		postMap.put("latitude", lat + "");
		// 1有地理编码，0无地理编码
		postMap.put("type", "1");
		postMap.put("page_idx", 0 + "");
		RequestDataUtil.getRequestInstance().requestData(shopperListListener,
				postMap, AppConstants.merchant_match, getActivity(), null,
				"true");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mPoiSearch.destroy();
	}

	// 解析商家列表
	private void parserShopersData(String json) {
		try {
			JSONObject jsbJson = new JSONObject(json);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			Gson gson = new Gson();
			if (statusCode.equals("200")) {
				String dataJson = jsbJson.getString("data");
				List<ShopListBean> shopList = gson.fromJson(dataJson,
						new TypeToken<List<ShopListBean>>() {
						}.getType());
				if (shopList != null && shopList.size() > 0) {
					ShopListBean bean = shopList.get(0);
					merchant_id = bean.getMerchant_id();
				}
			} else {
				if (statusCode.equals("1001")) {
					UIUtil.showToast(getActivity(), "身份登录信息失效");
					LogoutUtils.logout(getActivity());
				} else {
					UIUtil.showToast(getActivity(), statusMsg);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 请求优惠券列表
	private void requestCouponList() {
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("status", "available");
		RequestDataUtil.getRequestInstance().requestData(couponListener,
				postMap, AppConstants.coupon_list_nopage, getActivity(), null,
				"true");
	}

	private void parserCouponData(String json) {
		try {
			JSONObject jsbJson = new JSONObject(json);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			Gson gson = new Gson();
			if (statusCode.equals("200")) {
				String dataJson = jsbJson.getString("data");
				CouponTotalBean couponBean = gson.fromJson(dataJson,
						CouponTotalBean.class);
				couponList = couponBean.getCoupons();
			} else {
				if (statusCode.equals("1001")) {
					UIUtil.showToast(getActivity(), "身份登录信息失效");
					LogoutUtils.logout(getActivity());
				} else {
					UIUtil.showToast(getActivity(), statusMsg);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 创建订单
	private void requestCreateOrder() {
		HashMap<String, String> postMap = new HashMap<String, String>();
		if (sure_paytype.equals(SURE_PAYTYPE.NORMAL.name())) {
			CartListBean cartbean = cartList.get(0);
			List<OrderPrdAttrsBean> listAttr = cartbean.getGoods_attrs();
			StringBuilder sb = new StringBuilder();
			if (listAttr != null) {
				for (int i = 0; i < listAttr.size(); i++) {
					OrderPrdAttrsBean attrBean = listAttr.get(i);
					sb.append(",");
					sb.append(attrBean.getAttr_id());
				}
				postMap.put("goods_attr_ids", sb.toString().substring(1));
			}
			postMap.put("goods_id", cartbean.getGoods_id() + "");
		}

		if (address_id != null && !TextUtils.isEmpty(address_id)) {
			postMap.put("address_id", address_id);
		} else {
			UIUtil.showToast(getActivity(), "请选择配送地址");
			return;
		}
		if (shopType == SHOP_TYPE.platform) {
			if (merchant_id != -1) {
				postMap.put("merchant_id", merchant_id + "");
			}
			postMap.put("deliver_type", "1");
		} else {
			postMap.put("deliver_type", "2");
			if (shop_id == -1) {
				UIUtil.showToast(getActivity(), "您未选择商家，系统会为你分配商家哦");
			} else {
				postMap.put("merchant_id", shop_id + "");
			}
		}
		if (coupon_id != -1) {
			postMap.put("coupon_id", coupon_id + "");
		}
		extraMsg = et_extramsg.getText().toString();
		if (extraMsg != null && !TextUtils.isEmpty(extraMsg)) {
			postMap.put("buyer_message", extraMsg);
		}
		if (shopType == SHOP_TYPE.platform) {
			if (express_id != null && !TextUtils.isEmpty(express_id)) {
				postMap.put("express_id", express_id);
			} else {
				UIUtil.showToast(getActivity(), "请选择物流公司");
				return;
			}
		}
		if (sure_paytype.equals(SURE_PAYTYPE.CARTLIST.name())) {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < cartList.size(); i++) {
				CartListBean bean = cartList.get(i);
				builder.append(bean.getCart_id());
				if (i != cartList.size() - 1) {
					builder.append(",");
				}
			}
			if (builder.length() >= 0) {
				postMap.put("cart_ids", builder.toString());
			}
		}
		int goods_cnt = 0;
		for (int i = 0; i < cartList.size(); i++) {
			CartListBean bean = cartList.get(i);
			goods_cnt = bean.getCnt() + goods_cnt;
		}
		postMap.put("goods_cnt", goods_cnt + "");
		RequestDataUtil.getRequestInstance().requestData(orderSaveListener,
				postMap, AppConstants.order_save, getActivity(), null, "true");
	}

	private void parserOrderSave(String json) {
		try {
			JSONObject jsbJson = new JSONObject(json);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			if (statusCode.equals("200")) {
				JSONObject dataJson = jsbJson.getJSONObject("data");
				if (dataJson.has("order_id")) {
					order_id = dataJson.getInt("order_id") + "";
				}
				if (order_id != null && !TextUtils.isEmpty(order_id)) {
					UIUtil.showToast(getActivity(), "正在唤起支付界面，请等待...");
					// 调用支付接口
					requestPay();
				} else {
					UIUtil.showToast(getActivity(), "创建订单失败");
				}
			} else {
				if (statusCode.equals("1001")) {
					MemberDao memberDao = new MemberDao();
					memberDao.deleteAll();
					AppConstants.member.setSessionId("");
				} else {
					UIUtil.showToast(getActivity(), statusMsg);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 支付接口
	private void requestPay() {
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("order_id", order_id);
		postMap.put("pay_type", payType.name());
		RequestDataUtil.getRequestInstance().requestData(payListener, postMap,
				AppConstants.pay_order, getActivity(), null, "true");
	}

	private void parserPay(String json) {
		try {
			JSONObject jsbJson = new JSONObject(json);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			if (statusCode.equals("200")) {
				JSONObject dataJson = jsbJson.getJSONObject("data");
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
			} else {
				if (statusCode.equals("1001")) {
					MemberDao memberDao = new MemberDao();
					memberDao.deleteAll();
					AppConstants.member.setSessionId("");
				} else {
					UIUtil.showToast(getActivity(), statusMsg);
				}
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
					msg.arg1 = Integer.parseInt(order_id);
					mpayHandler.sendMessage(msg);
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
	@SuppressWarnings("unused")
	private class MyHandler extends Handler {
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
					bundle.putString("order_id", msg.arg1 + "");
					PageJumper pg = new PageJumper();
					pg.mNewActivity = true;
					pg.mArgs = bundle;
					pg.mContainerId = R.id.fragment_container;
					mPageSwitcher.openPage("order_detail", pg);
				} catch (Exception e) {
				}
			}
				break;
			}
		}
	}

	private void requestAddressData() {
		String url = "/api/v1/member/address_list";
		HashMap<String, String> postMap = new HashMap<String, String>();
		RequestDataUtil.getRequestInstance().requestData(alddressListener,
				postMap, url, getActivity(), null, "true");
	}

	protected void parseAddressData(String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			String statusCode = jsonObject.getString("statusCode");
			String statusMsg = jsonObject.getString("statusMsg");
			if (statusCode.equals("200")) {
				if (jsonObject.has("data")) {
					mList = new ArrayList<AddressBean>();
					JSONArray jsonArray_data = jsonObject.getJSONArray("data");
					AddressBean bean = null;
					for (int i = 0; i < jsonArray_data.length(); i++) {
						bean = new AddressBean();
						JSONObject json_address = (JSONObject) jsonArray_data
								.get(i);
						if (json_address.has("address_id")) {
							bean.setAddress_id(json_address
									.getInt("address_id"));
						}
						if (json_address.has("province_name")) {
							bean.setProvince_name(json_address
									.getString("province_name"));
						}
						if (json_address.has("city_name")) {
							bean.setCity_name(json_address
									.getString("city_name"));
						}
						if (json_address.has("county_name")) {
							bean.setCounty_name(json_address
									.getString("county_name"));
						}
						if (json_address.has("address")) {
							bean.setAddress(json_address.getString("address"));
						}
						if (json_address.has("receiver_name")) {
							bean.setReceiver_name(json_address
									.getString("receiver_name"));
						}
						if (json_address.has("receiver_mobile")) {
							bean.setReceiver_mobile(json_address
									.getString("receiver_mobile"));
						}
						if (json_address.has("default_tag")) {
							bean.setDefault_tag(json_address
									.getString("default_tag"));
						}
						if (mList == null) {
							mList = new ArrayList<AddressBean>();
						}
						if (returnBean != null
								&& returnBean.getAddress_id() == bean
										.getAddress_id()) {
							initDefaultAddress(bean);
							return;
						}
						mList.add(bean);
					}
					// 刷新地址数据
					if (mList != null && mList.size() > 0) {
						initDefaultAddress(mList.get(0));
					} else {
						initDefaultAddress(null);
					}
				}
			} else {
				UIUtil.showToast(getActivity(), statusCode + statusMsg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		if (getActivity() != null) {
			getActivity().unregisterReceiver(broadcastReceiver);
		}
		super.onDestroy();
	}

	@Override
	public void callback() {

	}
}
