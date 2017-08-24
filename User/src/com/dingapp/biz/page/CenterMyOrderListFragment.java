package com.dingapp.biz.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import view.refresh.PullToRefreshBase;
import view.refresh.PullToRefreshBase.Mode;
import view.refresh.PullToRefreshListView;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.orm.ItemOrderBean;
import com.dingapp.biz.db.orm.ModifyBean;
import com.dingapp.biz.db.orm.OrederGoodsItemBean;
import com.dingapp.biz.hx.HXContactUtils;
import com.dingapp.biz.net.RequestDataUtil;
import com.dingapp.biz.page.adapters.OrderListAdapter;
import com.dingapp.biz.util.StopRefresh;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.Constants;
import com.dingapp.core.util.UIUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.dbcontacts.dbcontact.ContactDao;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseUserUtils;

public class CenterMyOrderListFragment extends BaseFragment implements
		OnClickListener {
	private PullToRefreshListView pullListView;
	private TextView tv_title;
	private ImageView iv_back;
	private OrderListAdapter orderListViewAdapter;
	private FrameLayout fl_empty_data;
	private int index = 0;
	// type:设计师和分销订单以及普通订单:normal,designer,distributor
	private String type = "normal";
	private String title = "全部";

	public static enum MODE {
		UP, DOWN
	}

	private List<ItemOrderBean> dataList = new ArrayList<ItemOrderBean>();
	private Listener<String> listListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			if (pullListView != null) {
				pullListView.onRefreshComplete();
			}
			parserListData(response, MODE.DOWN);
		}
	};
	private Listener<String> moreListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			if (pullListView != null) {
				pullListView.onRefreshComplete();
			}
			parserListData(response, MODE.UP);
		}
	};
	private String status = "all";
	// 取消订单
	private Listener<String> cancelOrderListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			Log.i("cancel_order", response);
			parserCancel(response);
		}
	};
	// 申请退款
	private Listener<String> returnOrderListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserReturn(response);
		}
	};
	// 确认收货
	private Listener<String> receiveListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserReceive(response);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.center_myorder_list, null);
	}

	@Override
	protected void onDataReset(Bundle bundle) {
		super.onDataReset(bundle);
		if (bundle != null && bundle.containsKey("refresh")) {
			initData(listListener, 0);
		}
		pullListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments() != null) {
			if (getArguments().containsKey("status")) {
				status = getArguments().getString("status");
			}
			if (getArguments().containsKey("type")) {
				type = getArguments().getString("type");
			}
			if (getArguments().containsKey("title")) {
				title = getArguments().getString("title");
			}
		}
		initView();
		initListener();
		orderListViewAdapter = new OrderListAdapter(getActivity(),
				new OrderListAdapter.OrderItemClick() {

					@Override
					public void setOnclick(Bundle bundle, ItemOrderBean bean) {
						if (bundle != null) {
							// 联系商家
							if (bundle.containsKey("contact_shopper")
									&& bundle.getString("contact_shopper")
											.equals("true")) {
								if (bean.getMember_info() != null) {
									EaseUser user = new EaseUser("merchant"
											+ bean.getMember_info()
													.getMember_id());
									user.setNickName(bean.getMember_info()
											.getMember_nick());
									if (bean.getMember_info()
											.getMember_header() != null
											&& !TextUtils.isEmpty(bean
													.getMember_info()
													.getMember_header()
													.getDetail_url())) {
										user.setAvatar(bean.getMember_info()
												.getMember_header()
												.getDetail_url());
									}
									user.setIs_corp("true");
									if (new ContactDao(getActivity())
											.update(user) == 0) {
										new ContactDao(getActivity()).add(user);
									}
									EaseUserUtils.helpMap.put("merchant"
											+ bean.getMember_info()
													.getMember_id(), user);
								}

								showContactShopperDialog(bean);
								return;
							}
							int order_id = bundle.getInt("order_id");
							String status = bundle.getString("order_status");
							String isCancal = bundle.getString("isCancal");
							if (status.equals("paying")
									&& isCancal.equals("true")) {
								// 取消订单
								cancleOrder(order_id + "");
								return;
							}
							if (status.equals("paying")
									&& isCancal.equals("false")) {
								// 去支付
								Bundle bundle2 = new Bundle();
								bundle2.putString(AppConstants.KEY, order_id
										+ "");
								if (bundle.containsKey("price")) {
									bundle2.putString("price",
											bundle.getString("price"));
								} else {
									bundle2.putString("price", "0.0");
								}
								openPage("pay_order", bundle2, true);
								return;
							}
							if ((status.equals("shipping") || status
									.equals("receipting"))
									&& isCancal.equals("true")) {
								// 申请退款
								showReturnDialog(order_id + "");
								return;
							}
							if (status.equals("evaluating")) {
								Bundle bundle4 = new Bundle();
								bundle4.putString(AppConstants.KEY, order_id
										+ "");
								ArrayList<String> picList = new ArrayList<String>();
								ArrayList<String> nameList = new ArrayList<String>();
								ArrayList<String> idList = new ArrayList<String>();
								List<OrederGoodsItemBean> list = bean
										.getGoods_list();
								for (int i = 0; i < list.size(); i++) {
									OrederGoodsItemBean goodsBean = list.get(i);
									picList.add(goodsBean.getGoods_pic() == null ? ""
											: goodsBean.getGoods_pic()
													.getDetail_url());
									nameList.add(goodsBean.getGoods_name());
									idList.add(goodsBean.getGoods_id() + "");
								}
								bundle4.putStringArrayList("goodsPiclist",
										picList);
								bundle4.putStringArrayList("goodsNamelist",
										nameList);
								bundle4.putStringArrayList("goodsIdlist",
										idList);
								openPage("comment", bundle4, false);
								return;
							}
							if (status.equals("receipting")) {
								sureReceiveOrder(order_id + "");
								return;
							}
						}
					}
				}, CenterMyOrderListFragment.this, "normal");
		pullListView.setAdapter(orderListViewAdapter);
		initData(listListener, 0);
	}

	// 显示选择的弹出框
	private TextView tv_first, tv_second, tv_third, tv_four, tv_five, tv_six,
			tv_seven;
	private View v_parent;
	private PopupWindow popWindow;

	private void popDialog() {
		if (popWindow == null) {
			LayoutInflater inflater = LayoutInflater.from(getActivity());
			View dialogView = inflater.inflate(R.layout.choose_dialog, null);
			tv_first = (TextView) dialogView.findViewById(R.id.tv_first_choose);
			tv_second = (TextView) dialogView
					.findViewById(R.id.tv_second_choose);
			tv_third = (TextView) dialogView.findViewById(R.id.tv_third_choose);
			tv_four = (TextView) dialogView.findViewById(R.id.tv_four_choose);
			tv_five = (TextView) dialogView.findViewById(R.id.tv_five_choose);
			tv_six = (TextView) dialogView.findViewById(R.id.tv_six_choose);
			tv_seven = (TextView) dialogView.findViewById(R.id.tv_seven_choose);
			v_parent = dialogView.findViewById(R.id.v_parent);
			tv_first.setText("全部");
			tv_second.setText("待付款");
			tv_third.setText("待发货");
			tv_four.setText("待收货");
			tv_five.setText("待评价");
			tv_six.setText("已完成");
			tv_seven.setText("退款/售后");
			tv_four.setVisibility(View.VISIBLE);
			tv_five.setVisibility(View.VISIBLE);
			tv_six.setVisibility(View.VISIBLE);
			tv_seven.setVisibility(View.VISIBLE);
			tv_first.setOnClickListener(this);
			tv_second.setOnClickListener(this);
			tv_third.setOnClickListener(this);
			tv_five.setOnClickListener(this);
			tv_four.setOnClickListener(this);
			tv_second.setOnClickListener(this);
			v_parent.setOnClickListener(this);
			tv_six.setOnClickListener(this);
			tv_seven.setOnClickListener(this);
			popWindow = new PopupWindow();
			popWindow.setContentView(dialogView);
			popWindow.setWidth(Constants.getScreenWidth());
			popWindow.setHeight(Constants.getScreenHeight());
		}
		popWindow.showAsDropDown(tv_title);
	}

	private void initListener() {
		iv_back.setOnClickListener(this);
		tv_title.setOnClickListener(this);
	}

	private void initView() {
		pullListView = (PullToRefreshListView) getView().findViewById(
				R.id.lv_orderlist_works);
		iv_back = (ImageView) getView().findViewById(R.id.iv_orderlist_back);
		tv_title = (TextView) getView().findViewById(R.id.tv_orderlist_title);
		tv_title.setText(title);
		fl_empty_data = (FrameLayout) getView()
				.findViewById(R.id.fl_empty_data);
		initPullRefreshView();
	}

	// 联系商家弹出框
	private Dialog dialog_contact;
	private LinearLayout ll_chat_contact;
	private LinearLayout ll_phone_contact;
	private TextView tv_cancle_contact;
	private ItemOrderBean itemOrderBean;

	private void showContactShopperDialog(ItemOrderBean bean) {
		this.itemOrderBean = bean;
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

	// 申请退款
	private Dialog applay_dialog;

	private void showReturnDialog(final String order_id) {
		if (applay_dialog == null) {
			applay_dialog = new Dialog(getActivity(), R.style.dialog_style);
			View view = View.inflate(getActivity(),
					R.layout.applay_return_dialog, null);
			applay_dialog.setContentView(view);
			Window window = applay_dialog.getWindow();
			window.setBackgroundDrawable(new BitmapDrawable());
			window.setGravity(Gravity.BOTTOM);
			LayoutParams params = window.getAttributes();
			params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
			params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
			TextView tv_sure = (TextView) view
					.findViewById(R.id.tv_applayreturn_sure);
			TextView tv_cancel = (TextView) view
					.findViewById(R.id.tv_applayreturn_cancel);
			final EditText et_reason = (EditText) view
					.findViewById(R.id.et_applayreturn_reason);
			final EditText et_account = (EditText) view
					.findViewById(R.id.et_applayreturn_zfbaccount);
			final EditText et_name = (EditText) view
					.findViewById(R.id.et_applayreturn_name);
			final EditText et_phone = (EditText) view
					.findViewById(R.id.et_applayreturn_phone);
			tv_cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (applay_dialog != null) {
						applay_dialog.dismiss();
					}
				}
			});
			tv_sure.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (applay_dialog != null) {
						applay_dialog.dismiss();
					}
					String reason = et_reason.getText().toString();
					String account = et_account.getText().toString();
					String name = et_name.getText().toString();
					String phone = et_phone.getText().toString();
					requestReturnOrder(order_id, reason, account, name, phone);
				}
			});
		}
		applay_dialog.show();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initPullRefreshView() {
		StopRefresh.initRefreshView(pullListView, Mode.BOTH);

		pullListView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase refreshView) {
						StopRefresh.stopRefreash(pullListView);
						initData(listListener, 0);
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase refreshView) {
						StopRefresh.stopRefreash(pullListView);
						initData(moreListener, index + 1);
					}

				});
	}

	private void initData(Listener<String> listener, int index) {
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("page_idx", index + "");
		if (!status.equals("all")) {
			postMap.put("status", status);
		}
		postMap.put("type", type);
		RequestDataUtil.getRequestInstance().requestData(listener, postMap,
				AppConstants.ORDER_LIST, getActivity(), null, "true");
	}

	private void parserListData(String response, MODE mode) {
		try {
			JSONObject jsbJson = new JSONObject(response);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			Gson gson = new Gson();
			if (statusCode.equals("200")) {
				List<ItemOrderBean> taskList = gson.fromJson(
						jsbJson.getString("data"),
						new TypeToken<List<ItemOrderBean>>() {
						}.getType());
				if (mode == MODE.DOWN) {
					index = 0;
					dataList.clear();
				} else if (mode == MODE.UP) {
					if (taskList != null && taskList.size() > 0) {
						index++;
					} else {
						UIUtil.showToast(getActivity(), "暂无更多数据");
					}
				}
				if (taskList != null) {
					dataList.addAll(taskList);
				}
				setView();
				orderListViewAdapter.setList(dataList);
			} else {
				UIUtil.showToast(getActivity(), statusMsg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void setView() {
		if (dataList != null && dataList.size() > 0) {
			fl_empty_data.setVisibility(View.GONE);
		} else {
			fl_empty_data.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			initData(listListener, 0);
		}
	}

	public void cancleOrder(final String order_id) {
		final Dialog dialog = new Dialog(getActivity(), R.style.dialog_style);
		View view = View.inflate(getActivity(), R.layout.cancled_layout, null);
		TextView tv = (TextView) view.findViewById(R.id.tv_content);
		tv.setText("亲,您确定要取消订单吗？");
		dialog.setContentView(view);
		dialog.setCancelable(true);
		dialog.show();
		TextView bt_negative = (TextView) view.findViewById(R.id.tv_cancled);
		TextView bt_positive = (TextView) view.findViewById(R.id.tv_confirm);
		// 取消按钮
		bt_negative.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 取消
				dialog.dismiss();
			}
		});
		// 确定按钮
		bt_positive.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				requestCancelOrder(order_id);
			}
		});
		dialog.show();
	}

	// 申请退货接口
	private void requestReturnOrder(String order_id, String reason,
			String account, String name, String phone) {
		if (TextUtils.isEmpty(reason)) {
			UIUtil.showToast(getActivity(), "请填写退款原因");
			return;
		}
		if (TextUtils.isEmpty(account)) {
			UIUtil.showToast(getActivity(), "请输入您的支付宝账号");
			return;
		}
		if (TextUtils.isEmpty(name)) {
			UIUtil.showToast(getActivity(), "请输入您的收款姓名");
			return;
		}
		if (TextUtils.isEmpty(reason)) {
			UIUtil.showToast(getActivity(), "请输入您的联系电话");
			return;
		}
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("order_id", order_id);
		postMap.put("reason", reason);
		postMap.put("account", account);
		postMap.put("name", name);
		postMap.put("mobile", phone);
		boolean isLogin = RequestDataUtil.getRequestInstance().requestData(
				returnOrderListener, postMap, AppConstants.apply_return,
				getActivity(), null, "true");
		if (isLogin) {
			openPage("login_page", null, false);
		}
	}

	// 解析申请退货
	private void parserReturn(String response) {
		try {
			Gson gson = new Gson();
			ModifyBean bean = gson.fromJson(response, ModifyBean.class);
			if (bean.getStatusCode().equals("200")
					&& bean.getData().getSuc().equals("true")) {
				UIUtil.showToast(getActivity(), "申请成功");
				initData(listListener, 0);
			} else {
				UIUtil.showToast(getActivity(), bean.getStatusMsg());
			}
		} catch (Exception e) {
		}
	}

	// 删除订单接口
	private void requestCancelOrder(String order_id) {
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("order_id", order_id);
		boolean isLogin = RequestDataUtil.getRequestInstance().requestData(
				cancelOrderListener, postMap, AppConstants.cancel_order,
				getActivity(), null, "true");
		if (isLogin) {
			openPage("login_page", null, false);
		}
	}

	// 解析取消订单
	private void parserCancel(String response) {
		try {
			Gson gson = new Gson();
			ModifyBean bean = gson.fromJson(response, ModifyBean.class);
			if (bean.getStatusCode().equals("200")
					&& bean.getData().getSuc().equals("true")) {
				UIUtil.showToast(getActivity(), "取消成功");
				initData(listListener, 0);
			} else {
				UIUtil.showToast(getActivity(), bean.getStatusMsg());
			}
		} catch (Exception e) {
		}
	}

	public void sureReceiveOrder(final String order_id) {
		final Dialog dialog = new Dialog(getActivity(), R.style.dialog_style);
		View view = View.inflate(getActivity(), R.layout.cancled_layout, null);
		TextView tv = (TextView) view.findViewById(R.id.tv_content);
		tv.setText("亲,您确定要收货吗？");
		dialog.setContentView(view);
		dialog.setCancelable(true);
		dialog.show();
		TextView bt_negative = (TextView) view.findViewById(R.id.tv_cancled);
		TextView bt_positive = (TextView) view.findViewById(R.id.tv_confirm);
		// 取消按钮
		bt_negative.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 取消
				dialog.dismiss();
			}
		});
		// 确定按钮
		bt_positive.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				requestSureReceive(order_id);
			}
		});
		dialog.show();
	}

	// 确认收货
	private void requestSureReceive(String order_id) {
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("order_id", order_id);
		boolean isLogin = RequestDataUtil.getRequestInstance().requestData(
				receiveListener, postMap, AppConstants.order_receive,
				getActivity(), null, "true");
		if (isLogin) {
			openPage("login_page", null, false);
		}
	}

	// 解析确认收货
	private void parserReceive(String response) {
		try {
			Gson gson = new Gson();
			ModifyBean bean = gson.fromJson(response, ModifyBean.class);
			if (bean.getStatusCode().equals("200")
					&& bean.getData().getSuc().equals("true")) {
				UIUtil.showToast(getActivity(), "收货成功");
				initData(listListener, 0);
			} else {
				UIUtil.showToast(getActivity(), bean.getStatusMsg());
			}
		} catch (Exception e) {
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (popWindow != null && popWindow.isShowing()) {
			popWindow.dismiss();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		if (v == iv_back) {
			if (popWindow != null) {
				popWindow.dismiss();
			}
			popBack(null);
			return;
		}
		if (v == tv_title) {
			if (popWindow != null && popWindow.isShowing()) {
				popWindow.dismiss();
			} else {
				popDialog();
			}

			return;
		}
		if (v == tv_first) {
			if (popWindow != null) {
				popWindow.dismiss();
			}
			status = "all";
			tv_title.setText("全部");
			initData(listListener, 0);
			return;
		}
		if (v == tv_second) {
			if (popWindow != null) {
				popWindow.dismiss();
			}
			status = "paying";
			tv_title.setText("待付款");
			initData(listListener, 0);
			return;
		}
		if (v == tv_third) {
			if (popWindow != null) {
				popWindow.dismiss();
			}
			tv_title.setText("待发货");
			status = "shipping";
			initData(listListener, 0);
			return;
		}
		if (v == tv_four) {
			if (popWindow != null) {
				popWindow.dismiss();
			}
			tv_title.setText("待收货");
			status = "receipting";
			initData(listListener, 0);
			return;
		}
		if (v == tv_five) {
			if (popWindow != null) {
				popWindow.dismiss();
			}
			tv_title.setText("待评价");
			status = "evaluating";
			initData(listListener, 0);
			return;
		}
		if (v == tv_six) {
			if (popWindow != null) {
				popWindow.dismiss();
			}
			tv_title.setText("已完成");
			status = "finished";
			initData(listListener, 0);
			return;
		}
		if (v == tv_seven) {
			if (popWindow != null) {
				popWindow.dismiss();
			}
			tv_title.setText("退款/售后");
			status = "saleReturn";
			initData(listListener, 0);
			return;
		}
		if (v == v_parent) {
			if (popWindow != null) {
				popWindow.dismiss();
			}
			return;
		}
		if (v == ll_chat_contact) {
			if (dialog_contact != null) {
				dialog_contact.dismiss();
			}
			// 聊天
			if (itemOrderBean.getMember_info() == null) {
				UIUtil.showToast(getActivity(), "暂未匹配到商家");
				return;
			}
			HXContactUtils.getInstance(getActivity()).requestHxInfos(
					itemOrderBean.getMember_info().getMember_id());
			Bundle bundle = new Bundle();
			bundle.putString(EaseConstant.EXTRA_USER_ID, itemOrderBean
					.getMember_info().getHx_id());
			bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE,
					EaseConstant.CHATTYPE_SINGLE);
			openPage("wechat_chat", bundle, false);
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
			if (itemOrderBean.getMember_info() == null) {
				UIUtil.showToast(getActivity(), "暂未匹配到商家");
				return;
			}
			String phone = itemOrderBean.getMember_info().getMember_mobile();
			if (phone != null && !TextUtils.isEmpty(phone)) {
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
						+ phone));
				startActivity(intent);
			} else {
				UIUtil.showToast(getActivity(), "商家电话不可用");
			}
			return;
		}
	}
}
