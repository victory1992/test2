package com.dingapp.biz.page.thirdpage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.orm.AddressBean;
import com.dingapp.biz.net.SingleRequestQueue;
import com.dingapp.biz.net.StringPostRequest;
import com.dingapp.biz.page.adapters.SelectAddressAdapter;
import com.dingapp.biz.page.customview.MyListView;
import com.dingapp.biz.util.LogoutUtils;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.AndroidUtil;
import com.dingapp.core.util.UIUtil;

public class ShoppingSelectAddressFragment extends BaseFragment implements
		OnClickListener {
	private RelativeLayout rl_add_address;
	private MyListView lv_address;
	private ImageView img_back;
	private RequestQueue mQueue;
	private ArrayList<AddressBean> mList;
	private Listener<String> listener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parseAddressData(response);
		}
	};
	private ErrorListener errorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {

		}
	};
	private SelectAddressAdapter selectAddressAdapter;

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
						mList.add(bean);
					}
					// 刷新地址数据
					selectAddressAdapter.setData(mList);
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

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return View.inflate(getActivity(),
				R.layout.shop_order_select_address,null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mQueue = SingleRequestQueue.getreRequestQueue(getActivity()
				.getApplicationContext());
		initView();
		initListener();
		requestAddressData();
	}

	private void requestAddressData() {
		if (AndroidUtil.isNetworkAvailable(getActivity())) {
			String url = AppConstants.BaseUrl+"/api/v1/member/address_list";
			Map<String, String> postMap = new HashMap<String, String>();
			postMap.put("session_id", AppConstants.member.getSessionId());
			postMap.put("platform", AppConstants.PLATFORM);
			StringPostRequest request = new StringPostRequest(postMap, url,
					listener, errorListener);
			mQueue.add(request);
		} else {
			UIUtil.showToast(getActivity(), AppConstants.NetNotifice);
		}
	}

	private void initListener() {
		rl_add_address.setOnClickListener(this);
		img_back.setOnClickListener(this);
	}

	private void initView() {
		rl_add_address = (RelativeLayout) getView().findViewById(
				R.id.rl_add_address);
		lv_address = (MyListView) getView().findViewById(
				R.id.lv_address);
		img_back = (ImageView) getView().findViewById(
				R.id.img_back);
		selectAddressAdapter = new SelectAddressAdapter(getActivity(),
				ShoppingSelectAddressFragment.this);
		lv_address.setAdapter(selectAddressAdapter);
		lv_address.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// 将地址回传到上个地址
				selectAddressAdapter.setSelectedPosition((int) arg3);
				selectAddressAdapter.notifyDataSetChanged();
				AddressBean addressBean = selectAddressAdapter
						.getItem(position);
				// mHandler.sendEmptyMessage(0);
				
				Bundle bundle = new Bundle();
				bundle.putLong("address_id", addressBean.getAddress_id());
				bundle.putString("province_name",
						addressBean.getProvince_name());
				bundle.putString("city_name", addressBean.getCity_name());
				bundle.putString("county_name", addressBean.getCounty_name());
				bundle.putString("address", addressBean.getAddress());
				bundle.putString("receiver_name",
						addressBean.getReceiver_name());
				bundle.putString("receiver_mobile",
						addressBean.getReceiver_mobile());
				bundle.putString("default_tag", addressBean.getDefault_tag());
				bundle.putParcelable("address_info", addressBean);
				bundle.putString("address_refresh", "address_refresh");
				popBack(bundle);
			}
		});
	}
	@Override
	public void onClick(View v) {
		if (v == rl_add_address) {
			openPage("member_center_personal_data_add_address", null, false);
			return;
		} else if (v == img_back) {
			// 按回退键需要刷新页面
			Bundle bundle = new Bundle();
			bundle.putString("address_refresh", "address_refresh");
			popBack(bundle);
			return;
		}
	}

	// 消失进入该页面，重新刷新数据
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			requestAddressData();
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Bundle bundle = new Bundle();
			bundle.putString("address_refresh", "address_refresh");
			popBack(bundle);
			return true;
		}
		return false;
	}
}
