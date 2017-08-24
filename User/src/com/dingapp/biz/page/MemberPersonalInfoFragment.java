package com.dingapp.biz.page;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.orm.ImageBean;
import com.dingapp.biz.net.RequestDataUtil;
import com.dingapp.biz.net.SingleRequestQueue;
import com.dingapp.biz.net.StringPostRequest;
import com.dingapp.biz.page.adapters.SexWheelAdapter;
import com.dingapp.biz.page.timepackers.OnWheelScrollListener;
import com.dingapp.biz.page.timepackers.WheelAdapter;
import com.dingapp.biz.page.timepackers.WheelView;
import com.dingapp.biz.util.ImageUtils;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.db.dao.MemberDao;
import com.dingapp.core.db.orm.Member;
import com.dingapp.core.util.AndroidUtil;
import com.dingapp.core.util.CameralCtrl;
import com.dingapp.core.util.UIUtil;
import com.dingapp.imageloader.core.ImageLoader;

/**
 * 会员个人资料
 * 
 * @author king
 * 
 */
public class MemberPersonalInfoFragment extends BaseFragment implements
		OnClickListener {
	private ImageView iv_back;
	private ImageView iv_head;
	private LinearLayout ll_nickname;
	private TextView tv_nickname;
	private LinearLayout ll_sex;
	private TextView tv_sex;
	private LinearLayout ll_area;
	private LinearLayout ll_birthday;
	private TextView tv_birthday;
	private String head_url = "";
	private RequestQueue mQueue;
	private String nick_name;
	private String birthday = "";

	private Listener<String> upLoadListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserUpload(response);
		}
	};
	private ErrorListener errorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {

		}
	};
	private Listener<String> modifyHeaderListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserModifyHead(response);
		}
	};
	private Listener<String> modifyBirthListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserModifyBirth(response);
		}
	};
	private String nowPicUrl;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.member_personal_info, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mQueue = SingleRequestQueue.getreRequestQueue(getActivity()
				.getApplicationContext());
		initView(getView());
		initListener();
		initData();
		refreshData();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		refreshData();
	}

	private void initView(View view) {
		iv_back = (ImageView) view.findViewById(R.id.iv_member_personinfo_back);
		iv_head = (ImageView) view
				.findViewById(R.id.iv_member_personal_touxiang);
		ll_nickname = (LinearLayout) view
				.findViewById(R.id.ll_member_personal_nickname);
		tv_nickname = (TextView) view
				.findViewById(R.id.tv_member_personal_nickname2);
		ll_sex = (LinearLayout) view.findViewById(R.id.ll_member_personal_sex);
		tv_sex = (TextView) view.findViewById(R.id.tv_member_personal_sex2);
		ll_area = (LinearLayout) view
				.findViewById(R.id.ll_member_personal_address);
		tv_birthday = (TextView) view
				.findViewById(R.id.tv_member_personal_birthday);
		ll_birthday = (LinearLayout) view
				.findViewById(R.id.ll_member_personal_birthday);
	}

	private void initListener() {
		iv_back.setOnClickListener(this);
		iv_head.setOnClickListener(this);
		ll_nickname.setOnClickListener(this);
		ll_sex.setOnClickListener(this);
		ll_birthday.setOnClickListener(this);
		ll_area.setOnClickListener(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Bundle bundle = new Bundle();
			bundle.putString("refresh", "true");
			popBack(bundle);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		if (v == iv_back) {
			Bundle bundle = new Bundle();
			bundle.putString("refresh", "true");
			popBack(bundle);
			return;
		}
		if (v == iv_head) {
			CameralCtrl.start(MemberPersonalInfoFragment.this,
					"member_header.png");
			return;
		}
		if (v == ll_nickname) {
			Bundle bundle = new Bundle();
			if (nick_name != null) {
				bundle.putString(AppConstants.KEY, nick_name);
			} else {
				bundle.putString(AppConstants.KEY, "");
			}
			openPage("modify_nickname", bundle, false);
			return;
		}
		if (v == ll_sex) {
			openPage("modify_sex", null, false);
			return;
		}
		if (v == ll_birthday) {
			showBirthDialog();
			return;
		}
		if (v == birthday_cancel_btn) {
			if (birthday_dialog != null) {
				birthday_dialog.dismiss();
				return;
			}
		}
		if (v == birthday_ok_btn) {
			if (birthday_dialog != null) {
				birthday_dialog.dismiss();
			}
			try {
				String birthday = birthdayAdapter_year.getItem(
						year_wheel.getCurrentItem()).replace("年", "-")
						+ birthdayAdapter_month.getItem(
								month_wheel.getCurrentItem()).replace('月', '-')
						+ birthdayAdapter_day.getItem(
								day_wheel.getCurrentItem()).replace("日", "");
				requestModifyBirth(birthday);
			} catch (Exception e) {
			}
		}
		if (v == ll_area) {
			openPage("select_address", null, false);
			return;
		}
	}

	private void initData() {
		yearList = new ArrayList<String>();
		for (int i = 0; i < 117; i++) {
			yearList.add((2016 - i) + "年");
		}
		monthList = new ArrayList<String>();
		for (int i = 0; i < 12; i++) {
			monthList.add((1 + i) + "月");
		}
		dayList_31 = new ArrayList<String>();
		for (int i = 0; i < 31; i++) {
			dayList_31.add((1 + i) + "日");
		}
		dayList_30 = new ArrayList<String>();
		for (int i = 0; i < 30; i++) {
			dayList_30.add((1 + i) + "日");
		}
		dayList_29 = new ArrayList<String>();
		for (int i = 0; i < 29; i++) {
			dayList_29.add((1 + i) + "日");
		}
		dayList_28 = new ArrayList<String>();
		for (int i = 0; i < 28; i++) {
			dayList_28.add((1 + i) + "日");
		}
	}

	private WheelView year_wheel;
	private WheelView month_wheel;
	private WheelView day_wheel;
	private Button birthday_cancel_btn;
	private Button birthday_ok_btn;
	private SexWheelAdapter birthdayAdapter_year;
	private SexWheelAdapter birthdayAdapter_month;
	private SexWheelAdapter birthdayAdapter_day;
	private ArrayList<String> yearList;
	private ArrayList<String> monthList;
	private ArrayList<String> dayList_31;
	private ArrayList<String> dayList_30;
	private ArrayList<String> dayList_29;
	private ArrayList<String> dayList_28;
	private Dialog birthday_dialog;

	private void showBirthDialog() {
		if (birthday_dialog == null) {
			birthday_dialog = new Dialog(getActivity(), R.style.dialog_style);
			View view = View
					.inflate(getActivity(), R.layout.birth_dialog, null);
			birthday_cancel_btn = (Button) view.findViewById(R.id.cancel_btn);
			birthday_ok_btn = (Button) view.findViewById(R.id.ok_btn);
			year_wheel = (WheelView) view.findViewById(R.id.province_wheel);
			month_wheel = (WheelView) view.findViewById(R.id.city_wheel);
			day_wheel = (WheelView) view.findViewById(R.id.area_wheel);
			int textSize = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_SP, 15, getResources()
							.getDisplayMetrics());
			year_wheel.TEXT_SIZE = textSize;
			month_wheel.TEXT_SIZE = textSize;
			day_wheel.TEXT_SIZE = textSize;
			birthday_cancel_btn.setOnClickListener(this);
			birthday_ok_btn.setOnClickListener(this);
			birthday_dialog.setContentView(view);

			Window window = birthday_dialog.getWindow();
			window.setBackgroundDrawable(new BitmapDrawable());
			window.setGravity(Gravity.BOTTOM);
			LayoutParams params = window.getAttributes();
			params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
			params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
			initWheelBirthdayData();
		}
		birthday_dialog.show();
	}

	private void initWheelBirthdayData() {
		birthdayAdapter_year = new SexWheelAdapter(yearList);
		birthdayAdapter_month = new SexWheelAdapter(monthList);
		birthdayAdapter_day = new SexWheelAdapter(dayList_31);
		year_wheel.setAdapter(birthdayAdapter_year);
		month_wheel.setAdapter(birthdayAdapter_month);
		day_wheel.setAdapter(birthdayAdapter_day);
		year_wheel.setCurrentItem(0);
		month_wheel.setCurrentItem(0);
		day_wheel.setCurrentItem(0);
		try {
			year_wheel.addScrollingListener(new OnWheelScrollListener() {

				@Override
				public void onScrollingStarted(WheelView wheel) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onScrollingFinished(WheelView wheel) {
					int currentItem = wheel.getCurrentItem();
					int parseYear = Integer.parseInt(yearList.get(currentItem)
							.replace("年", ""));
					int currentItem2 = month_wheel.getCurrentItem();
					int parseMonth = Integer.parseInt(monthList.get(
							currentItem2).replace("月", ""));
					WheelAdapter adapter = day_wheel.getAdapter();
					int day = Integer.parseInt(adapter.getItem(
							day_wheel.getCurrentItem()).replace("日", ""));
					if (parseMonth == 2) {
						if (isLeapYear(parseYear)) {
							SexWheelAdapter birthdayAdapter = new SexWheelAdapter(
									dayList_29);
							day_wheel.setAdapter(birthdayAdapter);
							day_wheel.setCurrentItem(0);
						} else {
							SexWheelAdapter birthdayAdapter = new SexWheelAdapter(
									dayList_28);
							day_wheel.setAdapter(birthdayAdapter);
							day_wheel.setCurrentItem(0);
						}
					}

				}
			});
			month_wheel.addScrollingListener(new OnWheelScrollListener() {

				@Override
				public void onScrollingStarted(WheelView wheel) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onScrollingFinished(WheelView wheel) {
					WheelAdapter madapter = wheel.getAdapter();
					int parseMonth = Integer.parseInt(madapter.getItem(
							month_wheel.getCurrentItem()).replace("月", ""));
					WheelAdapter yadapter = year_wheel.getAdapter();
					int parseYear = Integer.parseInt(yadapter.getItem(
							year_wheel.getCurrentItem()).replace("年", ""));
					WheelAdapter aadapter = day_wheel.getAdapter();
					int parseDay = Integer.parseInt(aadapter.getItem(
							day_wheel.getCurrentItem()).replace("日", ""));
					if (parseMonth == 1 || parseMonth == 3 || parseMonth == 5
							|| parseMonth == 7 || parseMonth == 8
							|| parseMonth == 10 || parseMonth == 12) {
						SexWheelAdapter birthdayAdapter = new SexWheelAdapter(
								dayList_31);
						day_wheel.setAdapter(birthdayAdapter);
						day_wheel.setCurrentItem(0);
					} else if (parseMonth == 4 || parseMonth == 6
							|| parseMonth == 9 || parseMonth == 11) {
						SexWheelAdapter birthdayAdapter = new SexWheelAdapter(
								dayList_30);
						day_wheel.setAdapter(birthdayAdapter);
						day_wheel.setCurrentItem(0);
					} else if (parseMonth == 2) {
						if (isLeapYear(parseYear)) {
							SexWheelAdapter birthdayAdapter = new SexWheelAdapter(
									dayList_29);
							day_wheel.setAdapter(birthdayAdapter);
							day_wheel.setCurrentItem(0);
						} else {
							SexWheelAdapter birthdayAdapter = new SexWheelAdapter(
									dayList_28);
							day_wheel.setAdapter(birthdayAdapter);
							day_wheel.setCurrentItem(0);
						}
					}
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean isLeapYear(int year) {
		GregorianCalendar gregorianCalendar = new java.util.GregorianCalendar();
		if (gregorianCalendar.isLeapYear(year)) {
			return true;
		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (CameralCtrl.isCropResult(this, requestCode)) {
			head_url = CameralCtrl.getPortraitFile().getAbsolutePath();
			CameralCtrl.procCropResult(this, resultCode, data, iv_head, "icon");
			if (BitmapFactory.decodeFile(head_url) != null) {
				iv_head.setImageBitmap(BitmapFactory.decodeFile(head_url));
				upLoadFile(head_url);
			}
			return;
		}
		if (CameralCtrl.isOpenAlbumResult(this, requestCode)) {
			CameralCtrl.procOpenAlbumResult(this, resultCode, data, iv_head,
					"icon");
			return;
		}
		if (CameralCtrl.isOpenPhtoResult(this, requestCode)) {
			CameralCtrl.procOpenPhotoResult(this, resultCode, data);
			return;
		}
	}

	private void refreshData() {
		Member bean = AppConstants.member;
		nick_name = bean.getNickName();
		if (bean.getHeaderProfile() != null
				&& !TextUtils.isEmpty(bean.getHeaderProfile())) {
			ImageLoader.getInstance().displayImage(bean.getHeaderProfile(),
					iv_head);
		}
		if (bean.getNickName() != null
				&& !TextUtils.isEmpty(bean.getNickName())) {
			tv_nickname.setText(bean.getNickName());
		} else {
			tv_nickname.setText("请设置昵称");
		}
		if (bean.getWx_nick_name() != null) {
			if (bean.getWx_nick_name().equals("female")) {
				tv_sex.setText("女");
			} else {
				tv_sex.setText("男");
			}
		}
		if (bean.getBirthday() != null
				&& !TextUtils.isEmpty(bean.getBirthday())) {
			tv_birthday.setText(bean.getBirthday());
		} else {
			tv_birthday.setText("请设置生日");
		}
	}

	// 上传图片
	private void upLoadFile(String pic_url) {
		if (AndroidUtil.isNetworkAvailable(getActivity())) {
			String url = AppConstants.BaseUrl + "/api/v1/app/upload_file";
			HashMap<String, String> postMap = new HashMap<String, String>();
			postMap.put("platform", AppConstants.PLATFORM);
			postMap.put("session_id", AppConstants.member.getSessionId());
			postMap.put("auth", "false");
			List<ImageBean> imageList = new ArrayList<ImageBean>();
			ImageBean imgBean = new ImageBean();
			Bitmap bm = ImageUtils.decodeSampledBitmapFromResource(pic_url,
					640, 640);
			int digree = ImageUtils.getDigree(pic_url);
			if (digree != 0) {
				if (digree != 0) {// 旋转图片
					Matrix m = new Matrix();
					m.postRotate(digree);
					Bitmap rotateBitmap = Bitmap.createBitmap(bm, 0, 0,
							bm.getWidth(), bm.getHeight(), m, true);
					bm.recycle();
					bm = rotateBitmap;
				}
			}
			byte[] data = ImageUtils.Bitmap2Bytes(bm, CompressFormat.PNG);
			if (data == null) {
				bm.recycle();
				UIUtil.showToast(getActivity(), "图片出错");
				return;
			}
			bm.recycle();
			imgBean.setData(data);
			imgBean.setName("resource");
			imgBean.setFileName(System.currentTimeMillis() + ".png");
			imageList.add(imgBean);
			StringPostRequest request = null;
			if (imageList.size() <= 0) {
				request = new StringPostRequest(postMap, url, upLoadListener,
						errorListener);
			} else {
				request = new StringPostRequest(postMap, url, upLoadListener,
						errorListener, imageList);
			}
			mQueue.add(request);
		} else {
			UIUtil.showToast(getActivity(), AppConstants.NetNotifice);
		}
	}

	private void parserUpload(String response) {
		try {
			JSONObject jsonObj = new JSONObject(response);
			String statusCode = jsonObj.getString("statusCode");
			String statusMsg = jsonObj.getString("statusMsg");
			if (TextUtils.equals(statusCode, "200")) {
				JSONObject dataJson = jsonObj.getJSONObject("data");
				if (dataJson.has("detail_url")) {
					nowPicUrl = dataJson.getString("detail_url");
					requestModifyHead();
				}
			} else {
				UIUtil.showToast(getActivity(), statusMsg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 修改头像
	private void requestModifyHead() {
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("header_url", nowPicUrl);
		RequestDataUtil.getRequestInstance().requestData(modifyHeaderListener,
				postMap, AppConstants.modify_profile, getActivity(), null,
				"true");
	}

	// 修改生日
	private void requestModifyBirth(String birth) {
		HashMap<String, String> postMap = new HashMap<String, String>();
		birthday = birth;
		postMap.put("birthday_date", birth);
		RequestDataUtil.getRequestInstance().requestData(modifyBirthListener,
				postMap, AppConstants.birth_modification, getActivity(), null,
				"true");
	}

	// 修改生日
	private void requestModifyAddress(String address) {
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("region", address);
		// RequestDataUtil.getRequestInstance().requestData(modifyHeaderListener,
		// postMap, AppConstants.modify_profile, getActivity(), null,
		// "true");
	}

	private void parserModifyHead(String response) {
		try {
			JSONObject jsonObj = new JSONObject(response);
			String statusCode = jsonObj.getString("statusCode");
			String statusMsg = jsonObj.getString("statusMsg");
			if (TextUtils.equals(statusCode, "200")) {
				UIUtil.showToast(getActivity(), "修改头像成功");
				JSONObject dataJson = jsonObj.getJSONObject("data");
				if (dataJson.has("detail_url")) {
					AppConstants.member.setHeaderProfile(dataJson
							.getString("detail_url"));
					new MemberDao().add(AppConstants.member);
					refreshData();
				}
			} else {
				UIUtil.showToast(getActivity(), statusMsg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void parserModifyBirth(String response) {
		try {
			JSONObject jsonObj = new JSONObject(response);
			String statusCode = jsonObj.getString("statusCode");
			String statusMsg = jsonObj.getString("statusMsg");
			if (TextUtils.equals(statusCode, "200")) {

				JSONObject dataJson = jsonObj.getJSONObject("data");
				if (dataJson.has("suc")
						&& dataJson.getString("suc").equals("true")) {
					UIUtil.showToast(getActivity(), "修改生日成功");
					AppConstants.member.setBirthday(birthday);
					new MemberDao().add(AppConstants.member);
					refreshData();
				}

			} else {
				UIUtil.showToast(getActivity(), statusMsg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
