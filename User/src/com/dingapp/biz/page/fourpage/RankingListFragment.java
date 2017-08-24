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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.orm.MemberRankingBean;
import com.dingapp.biz.net.RequestDataUtil;
import com.dingapp.biz.util.OpenPageToMemberDetailUtils;
import com.dingapp.biz.util.StopRefresh;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.UIUtil;
import com.dingapp.imageloader.core.ImageLoader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 排行榜
 * 
 * @author king
 * 
 */
public class RankingListFragment extends BaseFragment implements
		OnClickListener {
	private ImageView iv_first_head;
	private TextView tv_first_name;
	private TextView tv_first_price;
	private LinearLayout ll_first;

	private ImageView iv_second_head;
	private TextView tv_second_name;
	private TextView tv_second_price;
	private LinearLayout ll_second;

	private ImageView iv_third_head;
	private TextView tv_third_name;
	private TextView tv_third_price;
	private LinearLayout ll_third;

	private ImageView iv_back;
	// 筛选条件
	private ListView pullListView;
	private PullToRefreshScrollView pullScroolView;

	public static enum MODE {
		UP, DOWN
	}

	private int index = 0;
	private List<MemberRankingBean> dataList = new ArrayList<MemberRankingBean>();
	private MyRankingListAdapter adapter;
	private Listener<String> refreshListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserData(response, MODE.DOWN);
		}
	};
	private Listener<String> moreListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserData(response, MODE.UP);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.ranking_list, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView(getView());
		pullListView.setFocusable(false);
		initListener();
		adapter = new MyRankingListAdapter();
		pullListView.setAdapter(adapter);
		initData(refreshListener, 0);
	}

	private void initView(View view) {
		iv_first_head = (ImageView) view.findViewById(R.id.iv_ranklist_first);
		tv_first_name = (TextView) view
				.findViewById(R.id.tv_ranklist_first_name);
		tv_first_price = (TextView) view
				.findViewById(R.id.tv_ranklist_first_price);

		iv_second_head = (ImageView) view.findViewById(R.id.iv_ranklist_second);
		tv_second_name = (TextView) view
				.findViewById(R.id.tv_ranklist_second_name);
		tv_second_price = (TextView) view
				.findViewById(R.id.tv_ranklist_second_price);

		iv_third_head = (ImageView) view.findViewById(R.id.iv_ranklist_third);
		tv_third_name = (TextView) view
				.findViewById(R.id.tv_ranklist_third_name);
		tv_third_price = (TextView) view
				.findViewById(R.id.tv_ranklist_third_price);
		ll_first = (LinearLayout) view.findViewById(R.id.ll_ranklist_first);
		ll_second = (LinearLayout) view.findViewById(R.id.ll_ranklist_second);
		ll_third = (LinearLayout) view.findViewById(R.id.ll_ranklist_third);

		iv_back = (ImageView) view.findViewById(R.id.iv_rankinglist_back);
		pullListView = (ListView) view
				.findViewById(R.id.ranking_pulltorefreshlistview);
		pullScroolView = (PullToRefreshScrollView) view
				.findViewById(R.id.ranklist_pulltorefreshscrollview);
		StopRefresh.initRefreshView(pullScroolView, Mode.PULL_FROM_END);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initListener() {
		iv_back.setOnClickListener(this);
		pullScroolView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase refreshView) {
						StopRefresh.stopRefreash(pullScroolView);
						initData(refreshListener, 0);
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase refreshView) {
						StopRefresh.stopRefreash(pullScroolView);
						initData(moreListener, index + 1);
					}
				});
	}

	@Override
	public void onClick(View v) {
		if (v == iv_back) {
			popBack(null);
			return;
		}
	}

	class MyRankingListAdapter extends BaseAdapter {
		private int colors[] = { 0xfff56f00, 0xffff9700, 0xffffcd1b,
				0xff75d137, 0xff54d1ac, 0xff5ea1fa, 0xff9781e3, 0xffe7b990 };
		private List<MemberRankingBean> list = new ArrayList<MemberRankingBean>();

		public void setList(List<MemberRankingBean> list2) {
			if (list2 != null) {
				list.clear();
				list.addAll(list2);
				notifyDataSetChanged();
			}
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.item_ranking, null);
				holder = new ViewHolder();
				holder.iv_member_head = (ImageView) convertView
						.findViewById(R.id.iv_ranking_touxiang);
				holder.tv_name = (TextView) convertView
						.findViewById(R.id.tv_ranking_member_name);
				holder.tv_money = (TextView) convertView
						.findViewById(R.id.tv_ranking_member_money);
				holder.tv_num = (TextView) convertView
						.findViewById(R.id.tv_ranking_circle);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			MemberRankingBean bean = (MemberRankingBean) getItem(position);
			if (bean != null) {
				holder.tv_num.setText(bean.getPos() + "");
				if (bean.getPos() < 7) {
					holder.tv_num.setBackgroundColor(colors[position]);
				} else {
					holder.tv_num.setBackgroundColor(colors[7]);
				}
				holder.tv_money.setText(bean.getMoney() + "");
				holder.tv_name.setText(bean.getNick_name());
				ImageLoader.getInstance().displayImage(
						bean.getHeader_img_url(), holder.iv_member_head);
			}
			return convertView;
		}

		class ViewHolder {
			private ImageView iv_member_head;
			private TextView tv_num;
			private TextView tv_name;
			private TextView tv_money;
		}
	}

	private void initData(Listener<String> listener, int index) {
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("page_idx", index + "");
		postMap.put("sort_type", "0");
		RequestDataUtil.getRequestInstance().requestData(listener, postMap,
				AppConstants.member_rank_list, getActivity(), null, "true");
	}

	private void parserData(String response, MODE mode) {
		try {
			JSONObject jsbJson = new JSONObject(response);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			if (statusCode.equals("200")) {
				if (jsbJson.has("data")) {
					JSONObject dataJson = jsbJson.getJSONObject("data");
					if (dataJson.has("rank_list")) {
						String jsonString = dataJson.getString("rank_list");
						Gson gson = new Gson();

						List<MemberRankingBean> list = gson.fromJson(
								jsonString,
								new TypeToken<List<MemberRankingBean>>() {
								}.getType());
						if (mode == MODE.DOWN) {
							index = 0;
							dataList.clear();
						} else if (mode == MODE.UP) {
							if (list != null && list.size() > 0) {
								index++;
							}
						}
						if (list != null) {
							dataList.addAll(list);
						}
						if (index == 0 || index == 1) {
							refreshRankTopView();
						}
						if (dataList.size() > 0) {
							pullScroolView.setVisibility(View.VISIBLE);
						} else {
							pullScroolView.setVisibility(View.GONE);
						}
						adapter.setList(dataList);
					}
				}
			} else {
				UIUtil.showToast(getActivity(), statusMsg);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void refreshRankTopView() {
		if (dataList.size() <= 0) {
			return;
		}
		pullScroolView.setVisibility(View.VISIBLE);
		if (dataList.size() >= 1) {
			ll_first.setVisibility(View.VISIBLE);
			final MemberRankingBean bean = dataList.get(0);
			ImageLoader.getInstance().displayImage(bean.getHeader_img_url(),
					iv_first_head);
			tv_first_name.setText(bean.getNick_name());
			tv_first_price.setText(bean.getMoney() + "元");
		}
		if (dataList.size() >= 2) {
			ll_second.setVisibility(View.VISIBLE);
			final MemberRankingBean bean = dataList.get(1);
			ImageLoader.getInstance().displayImage(bean.getHeader_img_url(),
					iv_second_head);
			tv_second_name.setText(bean.getNick_name());
			tv_second_price.setText(bean.getMoney() + "元");
		}
		if (dataList.size() >= 3) {
			ll_third.setVisibility(View.VISIBLE);
			final MemberRankingBean bean = dataList.get(2);
			ImageLoader.getInstance().displayImage(bean.getHeader_img_url(),
					iv_third_head);
			tv_third_name.setText(bean.getNick_name());
			tv_third_price.setText(bean.getMoney() + "元");
		}

	}
}
