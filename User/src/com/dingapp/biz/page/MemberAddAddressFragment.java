package com.dingapp.biz.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.dao.DBDao;
import com.dingapp.biz.db.dao.DBDao.Area;
import com.dingapp.biz.db.dao.DBDao.City;
import com.dingapp.biz.db.dao.DBDao.Province;
import com.dingapp.biz.net.SingleRequestQueue;
import com.dingapp.biz.net.StringPostRequest;
import com.dingapp.biz.page.adapters.AreaWheelAdapter;
import com.dingapp.biz.page.adapters.CityWheelAdapter;
import com.dingapp.biz.page.adapters.ProWheelAdapter;
import com.dingapp.biz.page.timepackers.OnWheelScrollListener;
import com.dingapp.biz.page.timepackers.WheelAdapter;
import com.dingapp.biz.page.timepackers.WheelView;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.UIUtil;

public class MemberAddAddressFragment extends BaseFragment implements
		OnClickListener {
	private ImageView ivBack;
	/**
	 * 收货人名字
	 */
	private EditText etName;
	/**
	 * 手机
	 */
	private EditText etMobile;
	/**
	 * 省份
	 */
	private EditText etProvince;
	/**
	 * 城市
	 */
	private EditText etCity;
	/**
	 * 地区
	 */
	private EditText etArea;
	/**
	 * 详细地址
	 */
	private EditText etDetailAddress;
	/**
	 * 确定
	 */
	private TextView tvSure;
	/**
	 * 取消
	 */
	private TextView tvCancel;
	private String address_id;
	private String city_name;
	private String receiver_mobile;
	private String receiver_name;
	private String default_tag;
	private String address;
	private String province_name;
	private String isDefault = "false";
	private String county_name;
	private WheelView sex_wheel;
	private WheelView hour_wheel;
	private WheelView school_wheel;
	/**
	 * 确定
	 */
	private Button ok_btn;

	/**
	 * 取消
	 */
	private Button cancel_btn;
	private ProWheelAdapter proAdapter;
	private List<Province> proList;
	private List<City> cityList;
	private LinearLayout parent_layout;
	private RelativeLayout rl_empty;
	private List<Area> areaList;
	private RequestQueue mQueue;
	/**
	 * 更改 填写地址的标题栏
	 */
	private TextView tv_edit_school_title;
	private Listener<String> editListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserData(response);
		}
	};
	private Listener<String> delListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserData(response);
		}
	};
	private Listener<String> addListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
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

		return inflater.inflate(R.layout.member_add_address, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mQueue = SingleRequestQueue.getreRequestQueue(getActivity()
				.getApplicationContext());
		if (getArguments() != null) {
			address_id = getArguments().getString("address_id");
			province_name = getArguments().getString("province_name");
			city_name = getArguments().getString("city_name");
			county_name = getArguments().getString("county_name");
			address = getArguments().getString("address");
			receiver_name = getArguments().getString("receiver_name");
			receiver_mobile = getArguments().getString("receiver_mobile");
			default_tag = getArguments().getString("default_tag");
		}
		initView();
		initViews();
		initWheelData();
		initListener();
		// 判断进入是否传入地址。传入地址那么设置删除 可见否则不可见，并且修改 text 文字
		if (address_id != null && !TextUtils.isEmpty(address_id)) {
			tv_edit_school_title.setText("编辑地址");
			tvCancel.setVisibility(View.VISIBLE);
			initFirstData();
		} else {
			tv_edit_school_title.setText("填写地址");
			tvCancel.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 编辑状态 初始化 第一次初始化
	 */
	private void initFirstData() {
		etName.setText(receiver_name);
		etArea.setText(county_name);
		etDetailAddress.setText(address);
		etCity.setText(city_name);
		etMobile.setText(receiver_mobile);
		etProvince.setText(province_name);
	}

	private void initListener() {
		ivBack.setOnClickListener(this);
		tvSure.setOnClickListener(this);
		tvCancel.setOnClickListener(this);
		etProvince.setOnClickListener(this);
		etCity.setOnClickListener(this);
		etArea.setOnClickListener(this);
		ok_btn.setOnClickListener(this);
		cancel_btn.setOnClickListener(this);
		rl_empty.setOnClickListener(this);
	}

	private void initView() {
		// ivBack = (ImageView) getView().findViewById(
		// PkgResource.getIdResource("iv_left_back"));
		// etName = (EditText) getView().findViewById(
		// PkgResource.getIdResource("et_receive_username"));
		// etMobile = (EditText) getView().findViewById(
		// PkgResource.getIdResource("et_receive_mobile"));
		// etProvince = (EditText) getView().findViewById(
		// PkgResource.getIdResource("et_receive_province"));
		// etCity = (EditText) getView().findViewById(
		// PkgResource.getIdResource("et_receive_city"));
		// etArea = (EditText) getView().findViewById(
		// PkgResource.getIdResource("et_receive_area"));
		// etDetailAddress = (EditText) getView().findViewById(
		// PkgResource.getIdResource("et_receive_detail_address"));
		// ckDefaultAddress = (CheckBox) getView().findViewById(
		// PkgResource.getIdResource("ck_moren_address"));
		// tvSure = (TextView) getView().findViewById(
		// PkgResource.getIdResource("tv_sure"));
		// tvCancel = (TextView) getView().findViewById(
		// PkgResource.getIdResource("tv_cancel"));
		// tv_edit_school_title = (TextView) getView().findViewById(
		// PkgResource.getIdResource("tv_edit_school_title"));
	}

	public void initViews() {
		// hour_wheel = (WheelView) getView().findViewById(
		// PkgResource.getIdResource("hour_wheel"));
		// sex_wheel = (WheelView) getView().findViewById(
		// PkgResource.getIdResource("x_wheel"));
		// school_wheel = (WheelView) getView().findViewById(
		// PkgResource.getIdResource("school_wheel"));
		// cancel_btn = (Button) getView().findViewById(
		// PkgResource.getIdResource("cancel_btn"));
		// parent_layout = (LinearLayout) getView().findViewById(
		// PkgResource.getIdResource("parent_layout"));
		// rl_empty = (RelativeLayout) getView().findViewById(
		// PkgResource.getIdResource("rl_empty"));
		// ok_btn = (Button) getView().findViewById(
		// PkgResource.getIdResource("ok_btn"));
		//
		// int textSize = (int) TypedValue.applyDimension(
		// TypedValue.COMPLEX_UNIT_SP, 15, getResources()
		// .getDisplayMetrics());
		// sex_wheel.TEXT_SIZE = textSize;
		// hour_wheel.TEXT_SIZE = textSize;
		// school_wheel.TEXT_SIZE = textSize;

	}

	public void initWheelData() {
		proList = new DBDao(getActivity(), AppConstants.DINGAPP)
				.findAllProvicen("1");
		proAdapter = new ProWheelAdapter((ArrayList<Province>) proList);
		sex_wheel.setAdapter(proAdapter);
		cityList = new DBDao(getActivity(), AppConstants.DINGAPP)
				.findAllCity(proList.get(0).region_id);
		hour_wheel.setAdapter(new CityWheelAdapter((ArrayList<City>) cityList));
		hour_wheel.setCurrentItem(0);

		areaList = new DBDao(getActivity(), AppConstants.DINGAPP)
				.findAllArea(cityList.get(0).region_id);
		if (areaList != null) {
			school_wheel.setAdapter(new AreaWheelAdapter(
					(ArrayList<Area>) areaList));
			school_wheel.setCurrentItem(0);
		}
		try {
			sex_wheel.addScrollingListener(new OnWheelScrollListener() {

				@Override
				public void onScrollingStarted(WheelView wheel) {
				}

				@Override
				public void onScrollingFinished(WheelView wheel) {
					try {
						int focusitem = wheel.getCurrentItem();
						String proId = proList.get(focusitem).region_id;
						cityList = new DBDao(getActivity(),
								AppConstants.DINGAPP).findAllCity(proId);
						hour_wheel.setAdapter(new CityWheelAdapter(
								(ArrayList<City>) cityList));
						hour_wheel.setCurrentItem(0);

						String cityId = cityList.get(0).region_id;
						areaList = new DBDao(getActivity(),
								AppConstants.DINGAPP).findAllArea(cityId);
						school_wheel.setAdapter(new AreaWheelAdapter(
								(ArrayList<Area>) areaList));
						school_wheel.setCurrentItem(0);

					} catch (Exception e) {
					}
				}
			});
			hour_wheel.addScrollingListener(new OnWheelScrollListener() {

				@Override
				public void onScrollingStarted(WheelView wheel) {
				}

				@Override
				public void onScrollingFinished(WheelView wheel) {
					try {
						int focusitem = wheel.getCurrentItem();
						String cityId = cityList.get(focusitem).region_id;
						areaList = new DBDao(getActivity(),
								AppConstants.DINGAPP).findAllArea(cityId);
						school_wheel.setAdapter(new AreaWheelAdapter(
								(ArrayList<Area>) areaList));
						school_wheel.setCurrentItem(0);
					} catch (Exception e) {
					}
				}
			});
		} catch (Exception e) {
		}

	}

	@Override
	public void onClick(View v) {
		if (v == ivBack) {
			popBack(null);
			return;
		} else if (v == tvSure) {
			requestEditAddress();
			return;
		} else if (v == tvCancel) {
			requestDelAddress();
			return;
		} else if (v == etProvince || v == etCity || v == etArea) {
			UIUtil.hideKeyboard(getActivity());
			parent_layout.setVisibility(View.VISIBLE);
			UIUtil.hideKeyboard(getActivity());
			return;
		} else if (v == rl_empty) {
			parent_layout.setVisibility(View.GONE);
			return;
		} else if (v == ok_btn) {
			WheelAdapter adapter = sex_wheel.getAdapter();
			WheelAdapter adapter2 = hour_wheel.getAdapter();
			WheelAdapter adapter3 = school_wheel.getAdapter();
			String provice = adapter.getItem(sex_wheel.getCurrentItem());

			String city = adapter2.getItem(hour_wheel.getCurrentItem());
			String area = adapter3.getItem(school_wheel.getCurrentItem());
			etProvince.setText(provice);
			etCity.setText(city);
			etArea.setText(area);
			parent_layout.setVisibility(View.GONE);
			return;
		} else if (v == cancel_btn) {
			parent_layout.setVisibility(View.GONE);
			return;
		}

	}

	// 编辑地址或者新增
	private void requestEditAddress() {
		HashMap<String, String> postMap = new HashMap<String, String>();
		if (TextUtils.isEmpty(etName.getText().toString())) {
			UIUtil.showToast(getActivity(), "请输入收件人姓名");
			return;
		}
		if (TextUtils.isEmpty(etMobile.getText().toString())) {
			UIUtil.showToast(getActivity(), "请输入手机号码");
			return;
		}
		if (TextUtils.isEmpty(etProvince.getText().toString())) {
			UIUtil.showToast(getActivity(), "请输入所在省份");
			return;
		}
		if (TextUtils.isEmpty(etCity.getText().toString())) {
			UIUtil.showToast(getActivity(), "请输入所在城市");
			return;
		}
		if (TextUtils.isEmpty(etArea.getText().toString())) {
			UIUtil.showToast(getActivity(), "请输入所在地区");
			return;
		}
		if (TextUtils.isEmpty(etDetailAddress.getText().toString())) {
			UIUtil.showToast(getActivity(), "请输入详细地址");
			return;
		}
		postMap.put("province_name", etProvince.getText().toString());
		postMap.put("city_name", etCity.getText().toString());
		postMap.put("county_name", etArea.getText().toString());
		postMap.put("receiver_name", etName.getText().toString());
		postMap.put("receiver_mobile", etMobile.getText().toString());
		postMap.put("address", etDetailAddress.getText().toString());
		postMap.put("default_flag", isDefault);
		postMap.put("session_id", AppConstants.member.getSessionId());
		postMap.put("platform", AppConstants.PLATFORM);
		// 如果有address——id 是编辑状态
		if (address_id != null && !TextUtils.isEmpty(address_id)) {
			postMap.put("address_id", address_id);
			StringPostRequest request = new StringPostRequest(postMap,
					AppConstants.BaseUrl, editListener, errorListener);
			mQueue.add(request);
			UIUtil.showToast(getActivity(), "编辑状态");
		} else {
			StringPostRequest request = new StringPostRequest(postMap,
					AppConstants.BaseUrl, addListener, errorListener);
			mQueue.add(request);
			UIUtil.showToast(getActivity(), "添加地址");
		}

	}

	// 删除地址

	private void requestDelAddress() {
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("address_id", address_id);
		postMap.put("session_id", AppConstants.member.getSessionId());
		postMap.put("platform", AppConstants.PLATFORM);
		StringPostRequest request = new StringPostRequest(postMap,
				AppConstants.BaseUrl, delListener, errorListener);
		mQueue.add(request);

	}

	/**
	 * @param response
	 *            解析是否修改成功
	 */
	private void parserData(String response) {

		try {
			JSONObject jsonObject = new JSONObject(response);
			String statusCode = jsonObject.getString("statusCode");
			String statusMsg = jsonObject.getString("statusMsg");
			if (statusCode.equals("200")) {
				if (jsonObject.has("data")) {
					JSONObject json_data = jsonObject.getJSONObject("data");
					if (json_data.has("suc")) {
						String suc = json_data.getString("suc");
						if (suc.equals("true")) {
							UIUtil.showToast(getActivity(), "操作成功");
							// 返回刷新数据
							// Bundle bundle = new Bundle();
							// bundle.putString("refresh", "refresh");
							popBack(null);
						} else {
							UIUtil.showToast(getActivity(), "操作失败");
						}
					}
				}
			} else {
				UIUtil.showToast(getActivity(), statusMsg);
			}
		} catch (Exception e) {
		}
	}

}