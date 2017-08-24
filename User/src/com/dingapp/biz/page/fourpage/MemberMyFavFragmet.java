package com.dingapp.biz.page.fourpage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import view.refresh.PullToRefreshBase;
import view.refresh.PullToRefreshBase.Mode;
import view.refresh.PullToRefreshScrollView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.Response.Listener;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.orm.GoodsFavBean;
import com.dingapp.biz.net.RequestDataUtil;
import com.dingapp.biz.page.adapters.FavGoodsAdapter;
import com.dingapp.biz.util.StopRefresh;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.UIUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MemberMyFavFragmet extends BaseFragment implements OnClickListener {
	private ImageView iv_back;
	private PullToRefreshScrollView pullScrollView;
	private ListView lv_tasks;
	private ImageView iv_no_data;
	private FavGoodsAdapter adapter;
	private List<GoodsFavBean> favDataList = new ArrayList<GoodsFavBean>();

	public static enum MODE {
		UP, DOWN
	}

	private int index = 0;
	private Listener<String> refreshListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserGoodsData(response, MODE.DOWN);
		}
	};
	private Listener<String> moreListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserGoodsData(response, MODE.UP);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fourpager_myfav, null);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			initGoodsData(refreshListener, 0);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initListener();
		adapter = new FavGoodsAdapter(getActivity());
		lv_tasks.setFocusable(false);
		lv_tasks.setAdapter(adapter);
		initGoodsData(refreshListener, 0);
	}

	private void initView() {
		iv_back = (ImageView) getView().findViewById(R.id.iv_myfav_back);
		pullScrollView = (PullToRefreshScrollView) getView().findViewById(
				R.id.member_myfav_pulltorefreshscrollview);
		StopRefresh.initRefreshView(pullScrollView, Mode.BOTH);
		iv_no_data = (ImageView) getView().findViewById(R.id.iv_fav_state_bg);
		lv_tasks = (ListView) getView()
				.findViewById(R.id.member_myfav_listview);
		lv_tasks.setVisibility(View.VISIBLE);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initListener() {
		pullScrollView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase refreshView) {
						StopRefresh.stopRefreash(pullScrollView);
						initGoodsData(refreshListener, 0);
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase refreshView) {
						StopRefresh.stopRefreash(pullScrollView);
						initGoodsData(moreListener, index + 1);
					}
				});
		lv_tasks.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Bundle bundle = new Bundle();
				GoodsFavBean bean = (GoodsFavBean) adapter.getItem((int) id);
				bundle.putInt("prd_id", bean.getGoods_id());
				bundle.putInt("design_id", bean.getDesign_info_id());
				openPage("goods_detail_pager", bundle, true);
			}
		});
		iv_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == iv_back) {
			popBack(null);
			return;
		}
	}

	private void initGoodsData(Listener<String> listener, int index) {
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("page_idx", index + "");
		RequestDataUtil.getRequestInstance().requestData(listener, postMap,
				AppConstants.collect_goods, getActivity(), null, "true");
	}

	private void parserGoodsData(String response, MODE mode) {
		try {
			JSONObject jsbJson = new JSONObject(response);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			Gson gson = new Gson();
			if (statusCode.equals("200")) {
				List<GoodsFavBean> taskList = gson.fromJson(
						jsbJson.getString("data"),
						new TypeToken<List<GoodsFavBean>>() {
						}.getType());
				if (mode == MODE.DOWN) {
					index = 0;
					favDataList.clear();
				} else if (mode == MODE.UP) {
					if (taskList != null && taskList.size() > 0) {
						index++;
					} else {
						UIUtil.showToast(getActivity(), "暂无更多数据");
					}
				}
				if (taskList != null) {
					favDataList.addAll(taskList);
				}
				setView();
				adapter.setList(favDataList);
			} else {
				UIUtil.showToast(getActivity(), statusMsg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void setView() {
		if (favDataList.size() <= 0) {
			lv_tasks.setVisibility(View.GONE);
			iv_no_data.setVisibility(View.VISIBLE);
		} else {
			lv_tasks.setVisibility(View.VISIBLE);
			iv_no_data.setVisibility(View.GONE);
		}
	}
}
