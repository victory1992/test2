package com.dingapp.biz.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.dingapp.biz.page.adapters.AddressAdapter;
import com.dingapp.biz.page.customview.MyListView;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.AndroidUtil;
import com.dingapp.core.util.UIUtil;

public class MemberMyAddressFragment extends BaseFragment implements
		OnClickListener {
	private ImageView img_back;
	private MyListView lv_address;
	private RelativeLayout rl_add_address;
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
	private AddressAdapter addressAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.member_myaddress, null);
	}

	/**
	 * @param response
	 *            解析地址列表信息
	 */
	protected void parseAddressData(String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			String statusCode = jsonObject.getString("statusCode");
			String statusMsg = jsonObject.getString("statusMsg");
			if (statusCode.equals("200")) {
				if (jsonObject.has("data")) {
					JSONArray jsonArray_data = jsonObject.getJSONArray("data");
					mList = new ArrayList<AddressBean>();
					AddressBean bean = null;
					for (int i = 0; i < jsonArray_data.length(); i++) {
						bean = new AddressBean();
						JSONObject json_address = (JSONObject) jsonArray_data
								.get(i);
						if (json_address.has("address_id")) {
							bean.setAddress_id(json_address
									.getLong("address_id"));
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
						mList.add(bean);
					}
					addressAdapter.setData(mList);
					addressAdapter.notifyDataSetChanged();
				}
			} else {
				UIUtil.showToast(getActivity(), statusCode + statusMsg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
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

	// @Override
	// protected void onDataReset(Bundle bundle) {
	// super.onDataReset(bundle);
	// if (bundle.containsKey("refresh")) {
	// // 重新请求数据,刷新列表
	// requestAddressData();
	// }
	//
	// }

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			requestAddressData();
		}
	}

	private void requestAddressData() {
		if (AndroidUtil.isNetworkAvailable(getActivity())) {
			String url = AppConstants.BaseUrl;
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
		img_back.setOnClickListener(this);
		rl_add_address.setOnClickListener(this);
	}

	private void initView() {
		img_back = (ImageView) getView().findViewById(
				R.id.iv_member_myaddr_back);
		lv_address = (MyListView) getView().findViewById(R.id.lv_address);
		rl_add_address = (RelativeLayout) getView().findViewById(
				R.id.rl_add_address);
		addressAdapter = new AddressAdapter(getActivity(), this);
		lv_address.setAdapter(addressAdapter);
	}

	@Override
	public void onClick(View v) {
		if (v == img_back) {
			popBack(null);
			return;
		} else if (v == rl_add_address) {
			if (mList != null && mList.size() > 4) {
				UIUtil.showToast(getActivity(), "最多只能添加5个地址");
				return;
			}
			openPage("member_center_personal_data_add_address", null, false);
			return;
		}
	}
}
