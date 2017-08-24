package com.dingapp.biz.page.thirdpage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import view.refresh.PullToRefreshBase;
import view.refresh.PullToRefreshBase.Mode;
import view.refresh.PullToRefreshListView;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
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
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.orm.CartListBean;
import com.dingapp.biz.net.RequestDataUtil;
import com.dingapp.biz.page.CenterMyOrderListFragment.MODE;
import com.dingapp.biz.page.adapters.GoodsCartsAdapter;
import com.dingapp.biz.page.thirdpage.SureOrderFragment.SURE_PAYTYPE;
import com.dingapp.biz.util.LogoutUtils;
import com.dingapp.biz.util.StopRefresh;
import com.dingapp.biz.util.Utils;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.app.NavigationFragment;
import com.dingapp.core.app.StubActivity;
import com.dingapp.core.util.UIUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GoodsCartFragment extends BaseFragment implements OnClickListener {
	private ImageView iv_back;
	private PullToRefreshListView pullListView;
	private LinearLayout ll_all_choose;
	private ImageView iv_all_choose;
	private TextView tv_all_price;
	private TextView tv_pay;
	private TextView tv_gotohome;
	private LinearLayout ll_empty_carts;
	private LinearLayout ll_bottom;
	private GoodsCartsAdapter adapter;
	private int index = 0;
	private TextView tv_thirdpager_del;
	private boolean isAllChoose = false;
	private CartListBean bean;
	private Double allPrice = 0.0;
	private boolean isFormHome = true;
	private List<CartListBean> dataList = new ArrayList<CartListBean>();
	private Listener<String> refreshListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			pareserListData(response, MODE.DOWN);
		}
	};
	private Listener<String> moreListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			pareserListData(response, MODE.UP);
		}
	};
	private Listener<String> addCartListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserAddCart(response);
		}
	};
	private Listener<String> delCartListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserDelData(response);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.thirdpager_home, null);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!TextUtils.isEmpty(AppConstants.member.getSessionId())) {
			pullListView.setVisibility(View.VISIBLE);
			requestListData(refreshListener, 0);
			iv_all_choose.setImageResource(R.drawable.pay_unchecked);
			isAllChoose = false;
		} else {
			ll_empty_carts.setVisibility(View.VISIBLE);
			pullListView.setVisibility(View.GONE);
			ll_bottom.setVisibility(View.GONE);
			StopRefresh.initRefreshView(pullListView, Mode.DISABLED);
			tv_thirdpager_del.setVisibility(View.GONE);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments() != null && getArguments().containsKey("isFrom")) {
			isFormHome = getArguments().getBoolean("isFrom");
		}
		initView(getView());
		adapter = new GoodsCartsAdapter(getActivity(),
				new GoodsCartsAdapter.AdapterListenr() {

					@Override
					public void onClickListener(CartListBean bean, int tag) {
						if (tag == 3) {
							dataList = adapter.getNewList();
							refreshGoodsPrice();
						} else {
							requestAddCart(bean, tag);
						}
					}
				});
		pullListView.setAdapter(adapter);
		if (!TextUtils.isEmpty(AppConstants.member.getSessionId())) {
			requestListData(refreshListener, 0);
			iv_all_choose.setImageResource(R.drawable.pay_unchecked);
			isAllChoose = false;
		} else {
			ll_empty_carts.setVisibility(View.VISIBLE);
			ll_bottom.setVisibility(View.GONE);
			StopRefresh.initRefreshView(pullListView, Mode.DISABLED);
			tv_thirdpager_del.setVisibility(View.GONE);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initView(View v) {
		iv_back = (ImageView) v.findViewById(R.id.iv_thirdpager_back);
		if (isFormHome) {
			iv_back.setVisibility(View.GONE);
		} else {
			iv_back.setVisibility(View.VISIBLE);
		}
		pullListView = (PullToRefreshListView) v
				.findViewById(R.id.lv_thirdpager);
		StopRefresh.initRefreshView(pullListView, Mode.PULL_FROM_START);
		ll_all_choose = (LinearLayout) v
				.findViewById(R.id.ll_thirdpager_allchoose);
		iv_all_choose = (ImageView) v
				.findViewById(R.id.iv_thirdpager_allchoose);
		tv_all_price = (TextView) v.findViewById(R.id.tv_price_total);
		tv_pay = (TextView) v.findViewById(R.id.tv_pay);
		ll_empty_carts = (LinearLayout) v.findViewById(R.id.ll_empty);
		ll_bottom = (LinearLayout) v.findViewById(R.id.rl_cart_pay);
		tv_thirdpager_del = (TextView) v.findViewById(R.id.tv_thirdpager_del);
		tv_gotohome = (TextView) v.findViewById(R.id.tv_go_home);
		tv_gotohome.setOnClickListener(this);
		tv_thirdpager_del.setOnClickListener(this);
		iv_back.setOnClickListener(this);
		tv_pay.setOnClickListener(this);
		ll_all_choose.setOnClickListener(this);
		pullListView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase refreshView) {
						StopRefresh.stopRefreash(pullListView);
						requestListData(refreshListener, 0);
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase refreshView) {
						StopRefresh.stopRefreash(pullListView);
						requestListData(moreListener, index + 1);
					}
				});
		pullListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				CartListBean bean = (CartListBean) adapter.getItem((int) arg3);
				Bundle bundle = new Bundle();
				bundle.putInt("prd_id", bean.getGoods_id());
				openPage("goods_detail_pager", bundle, true);
			}
		});
	}

	// 需要支付的总价格
	private void refreshGoodsPrice() {
		allPrice = 0.0;
		boolean hasChoose = false;
		for (int i = 0; i < dataList.size(); i++) {
			CartListBean bean = dataList.get(i);
			if (bean.getIs_select() != null
					&& bean.getIs_select().equals("true")) {
				int cnt = bean.getCnt();
				double itemPrice = 0.0;
				if (bean.getIs_vip() != null && bean.getIs_vip().equals("true")) {
					itemPrice = bean.getVip_price();
				} else {
					itemPrice = bean.getGoods_price();
				}
				allPrice = allPrice + itemPrice * cnt;
				hasChoose = true;
			}
		}
		tv_all_price.setText("￥" + Utils.keepDouble2(allPrice));
		// 改变按钮颜色
		if (hasChoose) {
			tv_pay.setBackgroundColor(0xffff6100);
		} else {
			tv_pay.setBackgroundColor(0xff808080);
		}
	}

	private boolean isSelect = false;

	@Override
	public void onClick(View v) {
		if (v == iv_back) {
			popBack(null);
			return;
		}
		if (v == tv_thirdpager_del) {
			for (int i = 0; i < dataList.size(); i++) {
				CartListBean bean = dataList.get(i);
				if (bean.getIs_select() != null
						&& bean.getIs_select().equals("true")) {
					isSelect = true;
				}
			}
			if (isSelect) {
				showDialog();
			} else {
				UIUtil.showToast(getActivity(), "请选择要删除的商品");
			}

			return;
		}
		if (v == tv_gotohome) {
			doInentToEntryFragment(getActivity());
			return;
		}
		if (v == ll_all_choose) {
			if (isAllChoose) {
				for (int i = 0; i < dataList.size(); i++) {
					CartListBean bean = dataList.get(i);
					bean.setIs_select("false");
				}
				iv_all_choose.setImageResource(R.drawable.pay_unchecked);
				adapter.notifyDataSetChanged();
				isAllChoose = false;
			} else {
				for (int i = 0; i < dataList.size(); i++) {
					CartListBean bean = dataList.get(i);
					bean.setIs_select("true");
				}
				iv_all_choose.setImageResource(R.drawable.pay_checked);
				adapter.notifyDataSetChanged();
				isAllChoose = true;
			}
			refreshGoodsPrice();
			return;
		}
		if (v == tv_pay) {
			Bundle bundle = new Bundle();
			ArrayList<CartListBean> listBean = new ArrayList<CartListBean>();
			for (int i = 0; i < dataList.size(); i++) {
				CartListBean bean = dataList.get(i);
				if (bean.getIs_select() != null
						&& bean.getIs_select().equals("true")) {
					listBean.add(bean);
				}
			}
			if (listBean.size() <= 0) {
				UIUtil.showToast(getActivity(), "请选择您要购买的商品");
				return;
			}
			bundle.putParcelableArrayList("cart_list", listBean);
			bundle.putDouble("price", allPrice);
			bundle.putString("sure_paytype", SURE_PAYTYPE.CARTLIST.name());
			openPage("sure_order", bundle, false);
			return;
		}
	}

	private void doInentToEntryFragment(Activity activity) {
		JSONObject extraObj = new JSONObject();
		try {
			extraObj.put(NavigationFragment.sPageNames,
					"home_page,making_friends,goods_carts,member_center");
			extraObj.put(NavigationFragment.sTabNames, "首页,分类,购物车,我的");
			extraObj.put(NavigationFragment.sNormalIcons,
					"tab1_normal,tab2_normal,tab3_normal,tab4_normal");
			extraObj.put(NavigationFragment.sFocusIcons,
					"tab1_focused,tab2_focused,tab3_focused,tab4_focused");
		} catch (JSONException e) {
			e.printStackTrace();
			activity.finish();
			return;
		}
		activity.startActivity(StubActivity.ctorJmpIntent("navigation",
				extraObj.toString()));
		activity.finish();
	}

	private void requestListData(Listener<String> listener, int index) {
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("page_idx", index + "");
		RequestDataUtil.getRequestInstance().requestData(listener, postMap,
				AppConstants.cart_list, getActivity(), null, "true");
	}

	private void pareserListData(String response, MODE mode) {
		try {
			JSONObject jsbJson = new JSONObject(response);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			Gson gson = new Gson();
			if (statusCode.equals("200")) {
				List<CartListBean> taskList = gson.fromJson(
						jsbJson.getString("data"),
						new TypeToken<List<CartListBean>>() {
						}.getType());
				if (mode == MODE.DOWN) {
					index = 0;
					dataList.clear();
				} else if (mode == MODE.UP) {
					if (taskList != null && taskList.size() > 0) {
						index++;
					} else {
						if (getActivity() != null) {
							UIUtil.showToast(
									getActivity(),
									getActivity().getResources().getString(
											R.string.no_more_data));
						}
					}
				}
				if (taskList != null) {
					dataList = new ArrayList<CartListBean>();
					dataList.addAll(taskList);
				}
				refreshGoodsPrice();
				adapter.setList(dataList);
				if (dataList.size() <= 0) {
					ll_empty_carts.setVisibility(View.VISIBLE);
					ll_bottom.setVisibility(View.GONE);
					StopRefresh.initRefreshView(pullListView, Mode.DISABLED);
					tv_thirdpager_del.setVisibility(View.GONE);
				} else {
					ll_empty_carts.setVisibility(View.GONE);
					ll_bottom.setVisibility(View.VISIBLE);
					StopRefresh.initRefreshView(pullListView,
							Mode.PULL_FROM_START);
					tv_thirdpager_del.setVisibility(View.VISIBLE);
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

	// 购物车加数量
	private void requestAddCart(CartListBean bean, int tag) {
		this.bean = bean;
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("cart_id", bean.getCart_id() + "");
		if (tag == 1) {
			if (bean.getCnt() + 1 > bean.getGoods_cnt()) {
				UIUtil.showToast(getActivity(), "库存不足");
				return;
			} else {
				postMap.put("cnt", (bean.getCnt() + 1) + "");
			}
		} else {
			if (bean.getCnt() - 1 <= 0) {
				UIUtil.showToast(getActivity(), "购买商品不能为0");
				return;
			} else {
				postMap.put("cnt", (bean.getCnt() - 1) + "");
			}
		}
		RequestDataUtil.getRequestInstance().requestData(addCartListener,
				postMap, AppConstants.cart_modify, getActivity(), null, "true");
	}

	private void parserAddCart(String response) {
		try {
			JSONObject jsbJson = new JSONObject(response);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			if (statusCode.equals("200")) {
				JSONObject dataJson = jsbJson.getJSONObject("data");
				if (dataJson.has("suc")) {
					String suc = dataJson.getString("suc");
					if (suc.equals("true")) {
						int cnt = dataJson.getInt("cnt");
						bean.setCnt(cnt);
						refreshGoodsPrice();
						adapter.notifyDataSetChanged();
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

	private Dialog builder;

	public void showDialog() {
		if (builder == null) {
			builder = new Dialog(getActivity(), R.style.CustomDialog);
			View view = LayoutInflater.from(getActivity()).inflate(
					R.layout.cancled_layout, null);
			((TextView) (view.findViewById(R.id.tv_content)))
					.setText("你确定要删除吗？");
			TextView tv_send = (TextView) view.findViewById(R.id.tv_confirm);
			TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancled);
			tv_send.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					builder.dismiss();
					requestDelCart();
				}
			});
			tv_cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					builder.dismiss();
				}
			});
			builder.setContentView(view);
			Window window = builder.getWindow();
			window.setBackgroundDrawable(new BitmapDrawable());
			window.setGravity(Gravity.CENTER);
			LayoutParams params = window.getAttributes();
			params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
			params.width = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		}
		builder.show();
	}

	private void requestDelCart() {
		HashMap<String, String> postMap = new HashMap<String, String>();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < dataList.size(); i++) {
			CartListBean bean = dataList.get(i);
			if (bean.getIs_select() != null
					&& bean.getIs_select().equals("true")) {
				builder.append(bean.getCart_id());
				if (i != dataList.size() - 1) {
					builder.append(",");
				}
			}
		}
		if (builder.length() <= 0) {
			UIUtil.showToast(getActivity(), "请选择要删除的商品");
			return;
		}
		postMap.put("cart_ids", builder.toString());
		RequestDataUtil.getRequestInstance().requestData(delCartListener,
				postMap, AppConstants.cart_delete, getActivity(), null, "true");
	}

	private void parserDelData(String json) {
		try {
			JSONObject jsbJson = new JSONObject(json);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			if (statusCode.equals("200")) {
				JSONObject dataJson = jsbJson.getJSONObject("data");
				if (dataJson.has("suc")) {
					String suc = dataJson.getString("suc");
					if (suc.equals("true")) {
						requestListData(refreshListener, 0);
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
}
