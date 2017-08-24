package com.dingapp.biz.page.firstpage;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import view.refresh.PullToRefreshBase;
import view.refresh.PullToRefreshBase.Mode;
import view.refresh.PullToRefreshScrollView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.orm.MsgListBean;
import com.dingapp.biz.net.RequestDataUtil;
import com.dingapp.biz.page.customview.MyListView;
import com.dingapp.biz.page.swpiedelete.SwipeLayout;
import com.dingapp.biz.page.swpiedelete.SwipeLayout.OnSwipeStateChangeListener;
import com.dingapp.biz.page.swpiedelete.SwipeLayoutManager;
import com.dingapp.biz.util.StopRefresh;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.UIUtil;

public class MemberNewsFragment extends BaseFragment implements OnClickListener {
	private PullToRefreshScrollView pull_listview;
	private ImageView img_back;
	private int index = 0;
	private int delPosition = -1;

	public enum MODE {
		UP, DOWN
	}

	private ArrayList<MsgListBean> mList;

	private Listener<String> listener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			if (pull_listview != null) {
				pull_listview.onRefreshComplete();
			}
			parseData(response, MODE.DOWN);
		}
	};
	private Listener<String> morelistener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			if (pull_listview != null) {
				pull_listview.onRefreshComplete();
			}
			parseData(response, MODE.UP);
		}
	};
	private MyListView my_lv;
	private Listener<String> delListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parseDelData(response);
		}
	};
	private MyAdapter myAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.member_news, null);
	}

	protected void parseDelData(String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			String statusCode = jsonObject.getString("statusCode");
			String statusMsg = jsonObject.getString("statusMsg");
			if (statusCode.equals("200")) {
				UIUtil.showToast(getActivity(), "刪除消息成功");
				SwipeLayoutManager.getInstance().closeCurrentLayout();
				ArrayList<MsgListBean> list = myAdapter.getList();
				if (list != null && list.size() > delPosition) {
					list.remove(delPosition);
					myAdapter.notifyDataSetChanged();
				}
			} else {
				UIUtil.showToast(getActivity(), "刪除消息失敗");
				SwipeLayoutManager.getInstance().closeCurrentLayout();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected void parseData(String response, MODE mode) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			String statusCode = jsonObject.getString("statusCode");
			String statusMsg = jsonObject.getString("statusMsg");
			if (statusCode.equals("200")) {
				if (jsonObject.has("data")) {
					mList = new ArrayList<MsgListBean>();
					JSONArray jsonArray_data = jsonObject.getJSONArray("data");
					if (jsonArray_data != null && jsonArray_data.length() > 0) {

						for (int i = 0; i < jsonArray_data.length(); i++) {
							MsgListBean newsBean = new MsgListBean();
							JSONObject json_msg = (JSONObject) jsonArray_data
									.get(i);
							if (json_msg.has("title")) {
								String title = json_msg.getString("title");
								newsBean.setTitle(title);
							}
							if (json_msg.has("msg_id")) {
								String msg_id = json_msg.getString("msg_id");
								newsBean.setMsg_id(msg_id);
							}
							if (json_msg.has("content")) {
								String content = json_msg.getString("content");
								newsBean.setContent(content);
							}
							if (json_msg.has("create_time")) {
								String create_time = json_msg
										.getString("create_time");
								newsBean.setCreate_time(create_time);
							}
							mList.add(newsBean);
						}
						if (mode == MODE.DOWN) {
							if (mList.size() > 0) {
								index = 0;
								myAdapter.setData(mList);
							}
						} else if (mode == MODE.UP) {
							if (mList.size() > 1) {
								index++;
								myAdapter.setMoreData(mList);
							}
						}
					}
				}
			} else {

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initListener();
		requestData(listener, 0);
	}

	private void requestData(Listener<String> mListener, int page_idx) {
		String url = AppConstants.msg_list;
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("page_idx", page_idx + "");
		postMap.put("type", "normal");
		RequestDataUtil.getRequestInstance().requestData(mListener, postMap,
				url, getActivity(), null, "true");
	}

	private void initListener() {
		img_back.setOnClickListener(this);
		my_lv.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
					// 如果垂直滑动，则需要关闭已经打开的layout
					SwipeLayoutManager.getInstance().closeCurrentLayout();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
	}

	private void initView() {
		pull_listview = (PullToRefreshScrollView) getView().findViewById(
				R.id.pull_listview);
		my_lv = (MyListView) getView().findViewById(R.id.my_lv);
		img_back = (ImageView) getView().findViewById(R.id.img_back);
		myAdapter = new MyAdapter();
		my_lv.setAdapter(myAdapter);
		my_lv.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
					// 如果垂直滑动，则需要关闭已经打开的layout
					SwipeLayoutManager.getInstance().closeCurrentLayout();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
		// my_lv.setOnScrollListener(new OnScrollListener() {
		// @Override
		// public void onScrollStateChanged(AbsListView view, int scrollState) {
		// if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
		// // 如果垂直滑动，则需要关闭已经打开的layout
		// SwipeLayoutManager.getInstance().closeCurrentLayout();
		// }
		// }
		//
		// @Override
		// public void onScroll(AbsListView view, int firstVisibleItem,
		// int visibleItemCount, int totalItemCount) {
		// }
		// });
		pull_listview.setMode(Mode.BOTH);
		// pull_listview.getLoadingLayoutProxy(false, true)
		// .setPullLabel("上拉加载...");
		// pull_listview.getLoadingLayoutProxy(false, true).setRefreshingLabel(
		// "正在加载...");
		// pull_listview.getLoadingLayoutProxy(false, true).setReleaseLabel(
		// "松开加载更多...");
		//
		// // 设置PullRefreshListView下拉加载时的加载提示
		pull_listview.getLoadingLayoutProxy(true, false)
				.setPullLabel("下拉刷新...");
		pull_listview.getLoadingLayoutProxy(true, false).setRefreshingLabel(
				"正在加载...");
		pull_listview.getLoadingLayoutProxy(true, false).setReleaseLabel(
				"松开加载更多...");
		pull_listview
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase refreshView) {
						StopRefresh.stopRefreash(pull_listview);
						requestData(listener, 0);
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase refreshView) {
						StopRefresh.stopRefreash(pull_listview);
						requestData(morelistener, index + 1);
					}
				});
	}

	@Override
	public void onClick(View v) {
		if (v == img_back) {
			SwipeLayoutManager.getInstance().closeCurrentLayout();
			Bundle bundle = new Bundle();
			bundle.putString("refresh", "refresh");
			popBack(bundle);
			return;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			SwipeLayoutManager.getInstance().closeCurrentLayout();
			Bundle bundle = new Bundle();
			bundle.putString("refresh", "refresh");
			popBack(bundle);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	class MyAdapter extends BaseAdapter implements OnSwipeStateChangeListener {
		private ArrayList<MsgListBean> ormList = new ArrayList<MsgListBean>();

		public MyAdapter() {
			// this.ormList = list;
		}

		@Override
		public int getCount() {
			return ormList.size();
		}

		public ArrayList<MsgListBean> getList() {
			return ormList;
		};

		public void setData(ArrayList<MsgListBean> newData) {
			if (newData != null) {
				ormList.clear();
				ormList.addAll(newData);
			}
			notifyDataSetChanged();
		}

		public void setMoreData(ArrayList<MsgListBean> moreData) {
			if (moreData != null && moreData.size() > 0) {
				ormList.addAll(moreData);
			}
			notifyDataSetChanged();
		}

		@Override
		public Object getItem(int position) {
			return ormList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MsgListBean newsBean = ormList.get(position);
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(getActivity(),
						R.layout.adapter_list, null);
				holder.swipeLayout = (SwipeLayout) convertView
						.findViewById(R.id.swipeLayout);
				holder.tv_pushnewslistitem_day = (TextView) convertView
						.findViewById(R.id.tv_pushnewslistitem_day);
				holder.tv_pushnewslistitem_yearandmonth = (TextView) convertView
						.findViewById(R.id.tv_pushnewslistitem_yearandmonth);
				holder.tv_pushnewslistitem_title = (TextView) convertView
						.findViewById(R.id.tv_pushnewslistitem_title);
				holder.tv_pushnewslistitem_detail = (TextView) convertView
						.findViewById(R.id.tv_pushnewslistitem_detail);
				holder.tv_pushnewslistitem_detail = (TextView) convertView
						.findViewById(R.id.tv_pushnewslistitem_detail);
				holder.tv_delete = (TextView) convertView
						.findViewById(R.id.tv_delete);
				holder.tv_delete = (TextView) convertView
						.findViewById(R.id.tv_delete);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// holder.tv_name.setText(mList.get(position).getTitle());
			holder.tv_pushnewslistitem_title.setText(newsBean.getTitle());
			holder.tv_pushnewslistitem_detail.setText(newsBean.getContent());
			String create_time = newsBean.getCreate_time();
			// 2016-01-19 14:40:27
			String year = create_time.substring(0, 4);
			String month = create_time.substring(5, 7);
			String day = create_time.substring(8, 10);
			holder.tv_pushnewslistitem_day.setText(day);
			holder.tv_pushnewslistitem_yearandmonth.setText(year + "年" + month);
			holder.swipeLayout.setTag(position);
			holder.swipeLayout.setOnSwipeStateChangeListener(this);

			return convertView;
		}

		@Override
		public void onOpen(Object tag) {

		}

		@Override
		public void onClose(Object tag) {

		}

		@Override
		public void delete(Object tag) {
			SwipeLayoutManager.getInstance().closeCurrentLayout();
			SwipeLayoutManager.getInstance().clearCurrentLayout();
			MsgListBean newsBean = ormList.get((Integer) tag);
			String msg_id = newsBean.getMsg_id();
			delPosition = (Integer) tag;
			requestDel(msg_id);
		}

		@Override
		public void contentClick(Object tag) {

		}

	}

	static class ViewHolder {
		TextView tv_pushnewslistitem_day, tv_delete,
				tv_pushnewslistitem_yearandmonth, tv_pushnewslistitem_title,
				tv_pushnewslistitem_detail;
		SwipeLayout swipeLayout;
	}

	public void requestDel(String msg_id) {
		String url = AppConstants.del_msg;
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("msg_id", msg_id + "");
		postMap.put("type", "normal");
		RequestDataUtil.getRequestInstance().requestData(delListener, postMap,
				url, getActivity(), null, "true");
	}
}
