package com.dingapp.biz.page.fourpage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import view.refresh.PullToRefreshBase;
import view.refresh.PullToRefreshBase.Mode;
import view.refresh.PullToRefreshListView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.Response.Listener;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.orm.ItemOrderBean;
import com.dingapp.biz.net.RequestDataUtil;
import com.dingapp.biz.page.adapters.OrderListAdapter;
import com.dingapp.biz.util.StopRefresh;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.UIUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DisOrderChildListFragment extends BaseFragment {
	private PullToRefreshListView pullListView;
	private OrderListAdapter orderListViewAdapter;
	private int index = 0;
	// type:设计师和分销订单以及普通订单:normal,designer,distributor
	private String type = "distributor";
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

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dis_child_orderlist, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments() != null) {
			status = getArguments().getString("type");
		}
		initView();
		initListener();
		orderListViewAdapter = new OrderListAdapter(getActivity(),
				new OrderListAdapter.OrderItemClick() {

					@Override
					public void setOnclick(Bundle bundle, ItemOrderBean bean) {
					}
				}, DisOrderChildListFragment.this, "distributor");
		pullListView.setAdapter(orderListViewAdapter);
		getView().postDelayed(new Runnable() {

			@Override
			public void run() {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						initData(listListener, 0);
					}
				});
			}
		}, 300);
	}

	private void initView() {
		pullListView = (PullToRefreshListView) getView().findViewById(
				R.id.membertaskslist_pulltorefreshlistview);
		StopRefresh.initRefreshView(pullListView, Mode.BOTH);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initListener() {
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
		pullListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});
	}

	private void initData(Listener<String> listener, int index) {
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("page_idx", index + "");
		postMap.put("if_withdraw", status);
		postMap.put("type", type);
		boolean isLogin = RequestDataUtil.getRequestInstance().requestData(
				listener, postMap, AppConstants.ORDER_LIST, getActivity(),
				null, "true");
		if (isLogin) {
			openPage("login_page", null, false);
		}
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
				orderListViewAdapter.setList(dataList);
			} else {
				UIUtil.showToast(getActivity(), statusMsg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}