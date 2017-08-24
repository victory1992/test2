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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.orm.ItemTeamMemberBean;
import com.dingapp.biz.net.RequestDataUtil;
import com.dingapp.biz.util.StopRefresh;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.AndroidUtil;
import com.dingapp.core.util.UIUtil;
import com.dingapp.imageloader.core.ImageLoader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 我的团队
 * 
 * @author king
 * 
 */
public class MyTeamFragment extends BaseFragment {
	private ImageView iv_back;
	private PullToRefreshListView pullListView;

	public static enum MODE {
		UP, DOWN
	}

	private String sort_type = "join_time";// 排序方式"money,join_time";
	private int index = 0;
	private List<ItemTeamMemberBean> dataList = new ArrayList<ItemTeamMemberBean>();
	private MyTeamAdapter adapter;
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
		return inflater.inflate(R.layout.myteam, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView(getView());
		initListener();
		adapter = new MyTeamAdapter();
		pullListView.setAdapter(adapter);
		initData(refreshListener, 0);
	}
	private void initView(View view) {
		iv_back = (ImageView) view.findViewById(R.id.iv_myteam_back);
		pullListView = (PullToRefreshListView) view
				.findViewById(R.id.myteam_pulltorefreshlistview);
		StopRefresh.initRefreshView(pullListView, Mode.BOTH);
		pageStateChange();
	}
	private FrameLayout fl_loading;
	private ImageView iv_loading;
	//页面加载完，是否有网络，无数据三种状态
	private void pageStateChange(){
		fl_loading = (FrameLayout) getView().findViewById(R.id.fl_loading);
		iv_loading = (ImageView) getView().findViewById(R.id.iv_loading);
		if(!AndroidUtil.isNetworkAvailable(getActivity())){
			iv_loading.setImageResource(R.drawable.iv_task_nowify);
		}
	}
	@SuppressWarnings("unchecked")
	private void initListener() {
		iv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popBack(null);
			}
		});
		pullListView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase refreshView) {
						StopRefresh.stopRefreash(pullListView);
						initData(refreshListener, 0);
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase refreshView) {
						StopRefresh.stopRefreash(pullListView);
						initData(moreListener, index + 1);
					}
				});

//		pullListView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				ItemTeamMemberBean itemBean = (ItemTeamMemberBean) adapter.getItem((int) id);
//				Bundle bundle = new Bundle();
//				if(itemBean==null){
//					return;
//				}
//				bundle.putString("dst_id", itemBean.getDst_id()+"");
//				bundle.putString("header_img_url", itemBean.getHeader_img_url());
//				bundle.putString("nick_name", itemBean.getNick_name());
//				bundle.putString("join_time", itemBean.getJoin_time());
//				bundle.putString("dst_money", itemBean.getDst_money()+"");
//				bundle.putString("member_id", itemBean.getMember_id()+"");
//				openPage("team_members_list", bundle, false);
//			}
//		});
	}

	public class MyTeamAdapter extends BaseAdapter {
		private List<ItemTeamMemberBean> list = new ArrayList<ItemTeamMemberBean>();

		public void setList(List<ItemTeamMemberBean> list2) {
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
						R.layout.item_myteam_list, null);
				holder = new ViewHolder();
				holder.tv_member_name = (TextView) convertView
						.findViewById(R.id.tv_item_myteam_naem);
				holder.tv_time = (TextView) convertView
						.findViewById(R.id.tv_item_myteam_time);
				holder.iv_header = (ImageView) convertView
						.findViewById(R.id.iv_item_myteam_header);
				holder.tv_bonus = (TextView) convertView
						.findViewById(R.id.tv_item_myteam_bonus);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ItemTeamMemberBean itemBean = (ItemTeamMemberBean) getItem(position);
			if(itemBean != null){
				holder.tv_time.setText("加入时间: "+itemBean.getJoin_time());
				holder.tv_member_name.setText(itemBean.getNick_name());
				ImageLoader.getInstance().displayImage(itemBean.getHeader_img_url(), holder.iv_header);
				holder.tv_bonus.setText("+"+itemBean.getDst_money()+"元");
			}
			return convertView;
		}

		class ViewHolder {
			private TextView tv_member_name;
			private TextView tv_time;
			private ImageView iv_header;
			private TextView tv_bonus;
		}
	}

	private void initData(Listener<String> listener, int index) {
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("page_idx", index + "");
		postMap.put("sort_type", sort_type);
//		postMap.put("depth", "1");
		RequestDataUtil.getRequestInstance().requestData(listener, postMap,
				AppConstants.member_dst_team, getActivity(), null, "true");
	}

	private void parserData(String response, MODE mode) {
		try {
			JSONObject jsbJson = new JSONObject(response);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			Gson gson = new Gson();
			if (statusCode.equals("200")) {
				List<ItemTeamMemberBean> list = gson.fromJson(
						jsbJson.getString("data"),
						new TypeToken<List<ItemTeamMemberBean>>() {
						}.getType());
				if (mode == MODE.DOWN) {
					index = 0;
					dataList.clear();
				} else if (mode == MODE.UP) {
					if (list != null && list.size() > 0) {
						index++;
					}else{
						UIUtil.showToast(getActivity(), getActivity().getResources().getString(R.string.no_more_data));
					}
				}
				if (list != null) {
					dataList.addAll(list);
				}
				if(dataList==null||dataList.size()<=0){
					iv_loading.setImageResource(R.drawable.iv_nodata_bg);
				}else{
					fl_loading.setVisibility(View.GONE);
				}
				adapter.setList(dataList);
			} else {
				UIUtil.showToast(getActivity(), statusMsg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
