package com.dingapp.biz.page.fourpage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import view.refresh.PullToRefreshBase;
import view.refresh.PullToRefreshBase.Mode;
import view.refresh.PullToRefreshListView;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.orm.CouponBean;
import com.dingapp.biz.net.SingleRequestQueue;
import com.dingapp.biz.net.StringPostRequest;
import com.dingapp.biz.page.adapters.CouponAdapter;
import com.dingapp.biz.util.LogoutUtils;
import com.dingapp.biz.util.StopRefresh;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.AndroidUtil;
import com.dingapp.core.util.UIUtil;

public class MemberCenterCouponListFragment extends BaseFragment {
	/**
	 * listview
	 */
	private PullToRefreshListView pull_listview;
	private String status;
	private RequestQueue mQueue;
	private int index = 0;
	private List<CouponBean> mList = new ArrayList<CouponBean>();
	private CouponAdapter couponAdapter;

	public enum MODE {
		DOWN, UP
	}

	private Listener<String> listener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			if (pull_listview != null) {
				pull_listview.onRefreshComplete();
			}
			parseCouponData(response, MODE.DOWN);
		}
	};
	private Listener<String> moreListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			if (pull_listview != null) {
				pull_listview.onRefreshComplete();
			}
			parseCouponData(response, MODE.UP);
		}
	};
	private ErrorListener errorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {

		}
	};

	protected void parseCouponData(String response, MODE mode) {
		try {
			mList.clear();
			JSONObject jsonObject = new JSONObject(response);
			String statusCode = jsonObject.getString("statusCode");
			String statusMsg = jsonObject.getString("statusMsg");
			if (statusCode.equals("200")) {
				if (jsonObject.has("data")) {
					JSONObject json_data = jsonObject.getJSONObject("data");
					int[] coupon = new int[3];
					if (json_data.has("available_cnt")) {
						int available_cnt = json_data.getInt("available_cnt");
						coupon[0] = available_cnt;
					}
					if (json_data.has("used_cnt")) {
						int used_cnt = json_data.getInt("used_cnt");
						coupon[1] = used_cnt;
					}
					if (json_data.has("expired_cnt")) {
						int expired_cnt = json_data.getInt("expired_cnt");
						coupon[2] = expired_cnt;
					}
					if (refreshListener != null) {
						refreshListener.refresh(coupon);
					}
					// 将优惠券的数据设置一下。
					setCouponCount(coupon);
					if (json_data.has("coupons")) {
						JSONArray jsonArray_coupons = json_data
								.getJSONArray("coupons");
						if (jsonArray_coupons != null
								&& jsonArray_coupons.length() > 0) {
							for (int i = 0; i < jsonArray_coupons.length(); i++) {
								CouponBean couponBean = new CouponBean();
								JSONObject json_coupon = jsonArray_coupons
										.getJSONObject(i);
								if (json_coupon.has("coupon_id")) {
									long coupon_id = json_coupon
											.getLong("coupon_id");
									couponBean.setCoupon_id(coupon_id);
								}
								if (json_coupon.has("money")) {
									double money = json_coupon
											.getDouble("money");
									couponBean.setMoney(money);
								}
								if (json_coupon.has("start_date")) {
									String start_date = json_coupon
											.getString("start_date");
									couponBean.setStart_date(start_date);
								}
								if (json_coupon.has("end_date")) {
									String end_date = json_coupon
											.getString("end_date");
									couponBean.setEnd_date(end_date);
								}
								if (json_coupon.has("left_day")) {
									int left_day = json_coupon
											.getInt("left_day");
									couponBean.setLeft_day(left_day);
								}
								if (json_coupon.has("create_time")) {
									String create_time = json_coupon
											.getString("create_time");
									couponBean.setCreate_time(create_time);
								}
								if (json_coupon.has("status")) {
									String status = json_coupon
											.getString("status");
									couponBean.setStatus(status);
								}
								mList.add(couponBean);
							}
							if (mode == MODE.DOWN) {
								if (mList.size() > 0) {
									index = 0;
									couponAdapter.setData(mList);

								}
							} else if (mode == MODE.UP) {
								if (mList.size() > 0) {
									index++;
									couponAdapter.setMoreData(mList);
								}

							}
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

	private OnRefreshCouponCountListener refreshListener;

	public void setOnRefreshCouponCountListener(
			OnRefreshCouponCountListener refreshListener) {
		this.refreshListener = refreshListener;
	}

	public interface OnRefreshCouponCountListener {
		void refresh(int[] counts);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return View.inflate(getActivity(),
				R.layout.member_center_coupon_list,
				null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mQueue = SingleRequestQueue.getreRequestQueue(getActivity()
				.getApplicationContext());
		if (getArguments() != null && getArguments().containsKey("status")) {
			status = getArguments().getString("status");
		}
		initView();

		requestCouponData(status, 0, listener);
	}

	private void initView() {
		pull_listview = (PullToRefreshListView) getView().findViewById(
				R.id.pull_listview);
		initPullToRefreshListView();
		 ArrayList<CouponBean> arrayList = new ArrayList<CouponBean>();
		 for (int i = 0; i < 10; i++) {
		 CouponBean couponBean = new CouponBean();
		 couponBean.setCoupon_id(1002);
		 couponBean.setCreate_time("2016-12-12");
		 couponBean.setEnd_date("2017-12-12");
		 couponBean.setLeft_day(20);
		 couponBean.setMoney(100.00);
		 couponBean.setStart_date("2001.12.12");
		 couponBean.setStatus("已过期");
		 arrayList.add(couponBean);
		 }
		couponAdapter = new CouponAdapter(getActivity());
		pull_listview.setAdapter(couponAdapter);
	}

	private void requestCouponData(String status, int index,
			Listener<String> mlistener) {
		if (AndroidUtil.isNetworkAvailable(getActivity())) {
			Map<String, String> postMap = new HashMap<String, String>();
			postMap.put("platform", AppConstants.PLATFORM);
			postMap.put("session_id", AppConstants.member.getSessionId());
			postMap.put("page_idx", index + "");
			postMap.put("status", status);
			String url = AppConstants.COUPON_LIST;
			StringPostRequest request = new StringPostRequest(postMap, url,
					mlistener, errorListener);
			mQueue.add(request);
		} else {
			UIUtil.showToast(getActivity(), AppConstants.NetNotifice);
		}
	}

	private int[] couponCounts;

	public void setCouponCount(int[] ints) {
		this.couponCounts = ints;
	}

	public int[] getCouponCount() {
		return couponCounts;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initPullToRefreshListView() {
		pull_listview.setMode(Mode.BOTH);
		pull_listview.getLoadingLayoutProxy(false, true)
				.setPullLabel("上拉加载...");
		pull_listview.getLoadingLayoutProxy(false, true).setRefreshingLabel(
				"正在加载...");
		pull_listview.getLoadingLayoutProxy(false, true).setReleaseLabel(
				"松开加载更多...");

		// 设置PullRefreshListView下拉加载时的加载提示
		pull_listview.getLoadingLayoutProxy(true, false)
				.setPullLabel("下拉刷新...");
		pull_listview.getLoadingLayoutProxy(true, false).setRefreshingLabel(
				"正在加载...");
		pull_listview.getLoadingLayoutProxy(true, false).setReleaseLabel(
				"松开加载更多...");
		pull_listview
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase refreshView) {
						StopRefresh.stopRefreash(pull_listview);
						requestCouponData(status, 0, listener);
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase refreshView) {
						StopRefresh.stopRefreash(pull_listview);
						requestCouponData(status, index + 1, moreListener);
					}

				});

	}
}
