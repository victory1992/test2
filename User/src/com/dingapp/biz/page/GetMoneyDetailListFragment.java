package com.dingapp.biz.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import view.refresh.PullToRefreshBase;
import view.refresh.PullToRefreshBase.Mode;
import view.refresh.PullToRefreshScrollView;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.orm.IncomePrjStatListBean;
import com.dingapp.biz.db.orm.PrjStatBean;
import com.dingapp.biz.net.RequestDataUtil;
import com.dingapp.biz.page.customview.MagicTextView;
import com.dingapp.biz.page.customview.MyExpandableListView;
import com.dingapp.biz.util.StopRefresh;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.Constants;
import com.dingapp.core.util.UIUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 收益明细页面
 * 
 * @author king
 * 
 */
public class GetMoneyDetailListFragment extends BaseFragment implements
		OnClickListener {
	private ImageView iv_back;
	private TextView tv_title;
	private MagicTextView tv_money;
	private TextView tv_first;
	private TextView tv_second;
	private TextView tv_third;
	private PopupWindow popWindow;
	private View v_parent;
	private String total_money = "0";
	private String month_money = "0";
	private String seven_money = "0";
	private View dialogView;
	// 是否是点击累计收益每一项进入的
	private String isItem;
	// 显示哪个时间段
	private String show_time = "total";
	private String from_tag = "normal";
	private PullToRefreshScrollView pullScrollView;
	private MyExpandableListView listView;
	private ViewHolderGroup holderGroup = null;
	// 筛选类型
	private int category_filter_type = 0;
	private int time_filter_type = 3;
	private String start_date;
	private String end_date;

	public static enum MODE {
		UP, DOWN
	}

	private int index = 0;
	private List<IncomePrjStatListBean> dataList = new ArrayList<IncomePrjStatListBean>();
	private MoneyDetailExpandableAdapter adapter;
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
		return inflater.inflate(R.layout.getmoney_detail, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments() != null) {
			if (getArguments().containsKey("total_money")) {
				total_money = getArguments().getString("total_money");
			}
			if (getArguments().containsKey("month_money")) {
				month_money = getArguments().getString("month_money");
			}
			if (getArguments().containsKey("seven_money")) {
				seven_money = getArguments().getString("seven_money");
			}
			if (getArguments().containsKey("show_time")) {
				show_time = getArguments().getString("show_time");
			}
			if (getArguments().containsKey("from_tag")) {
				from_tag = getArguments().getString("from_tag");
			}
			if (getArguments().containsKey("item")) {
				isItem = getArguments().getString("item");
				String time = getArguments().getString("time");
				start_date = time + " 00:00:00";
				end_date = time + " 23:59:59";
			}
		}
		initView(getView());
		initListener();
		adapter = new MoneyDetailExpandableAdapter();
		listView.setAdapter(adapter);
		requestData(refreshListener, 0);
		listView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				parent.expandGroup(groupPosition);
				return true;
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (popWindow != null) {
				popWindow.dismiss();
			}
			popStack(null);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initView(View view) {
		iv_back = (ImageView) view.findViewById(R.id.iv_moneydetail_back);
		tv_title = (TextView) view.findViewById(R.id.tv_moneydetail_title);
		tv_money = (MagicTextView) view
				.findViewById(R.id.tv_getmoney_detail_money);
		pullScrollView = (PullToRefreshScrollView) view
				.findViewById(R.id.moneydetail_pulltorefreshscrollview);
		StopRefresh.initRefreshView(pullScrollView, Mode.BOTH);
		listView = (MyExpandableListView) view
				.findViewById(R.id.lv_getmoney_detail);
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		dialogView = inflater.inflate(R.layout.choose_dialog, null);
		tv_first = (TextView) dialogView.findViewById(R.id.tv_first_choose);
		tv_second = (TextView) dialogView.findViewById(R.id.tv_second_choose);
		tv_third = (TextView) dialogView.findViewById(R.id.tv_third_choose);
		v_parent = dialogView.findViewById(R.id.v_parent);
		tv_first.setText("累计收益");
		tv_second.setText("近一周累计收益");
		tv_third.setText("近一月累计收益");
		tv_first.setOnClickListener(this);
		tv_second.setOnClickListener(this);
		tv_third.setOnClickListener(this);
		v_parent.setOnClickListener(this);
		tv_money.setText(Double.parseDouble(total_money) + "");
		initTextColor(tv_first);
		if (show_time.equals("normal")) {
			initTextColor(tv_first);
			tv_money.setText(Double.parseDouble(total_money) + "");
		} else if (show_time.equals("seven")) {
			tv_money.setText(Double.parseDouble(seven_money) + "");
			initTextColor(tv_second);
		} else if (show_time.equals("month")) {
			tv_money.setText(Double.parseDouble(month_money) + "");
			initTextColor(tv_third);
		}
		// 点击累计收益的每项
		if (isItem != null && isItem.equals("item")) {
			tv_title.setCompoundDrawables(null, null, null, null);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initListener() {
		iv_back.setOnClickListener(this);
		// 点击累计收益的每项
		if (isItem != null && isItem.equals("item")) {
			tv_title.setOnClickListener(null);
		} else {
			tv_title.setOnClickListener(this);
		}

		pullScrollView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase refreshView) {
						StopRefresh.stopRefreash(pullScrollView);
						requestData(refreshListener, 0);
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase refreshView) {
						StopRefresh.stopRefreash(pullScrollView);
						requestData(moreListener, index + 1);
					}
				});
	}

	@Override
	public void onClick(View v) {
		if (v == iv_back) {
			if (popWindow != null) {
				popWindow.dismiss();
			}
			popStack(null);
			return;
		}
		if (v == tv_title) {
			if (popWindow != null && popWindow.isShowing()) {
				popWindow.dismiss();
				return;
			}
			popDialog();
			return;
		}
		if (v == v_parent) {
			if (popWindow != null) {
				popWindow.dismiss();
			}
		}
		if (v == tv_first) {
			if (popWindow != null) {
				popWindow.dismiss();
			}
			tv_money.setText(Double.parseDouble(total_money) + "");
			show_time = "total";
			initTextColor(tv_first);
			requestData(refreshListener, 0);
		}
		if (v == tv_second) {
			if (popWindow != null) {
				popWindow.dismiss();
			}
			tv_money.setText(Double.parseDouble(seven_money) + "");
			show_time = "seven";
			initTextColor(tv_second);
			requestData(refreshListener, 0);
		}
		if (v == tv_third) {
			if (popWindow != null) {
				popWindow.dismiss();
			}
			tv_money.setText(Double.parseDouble(month_money) + "");
			show_time = "month";
			initTextColor(tv_third);
			requestData(refreshListener, 0);
		}
	}

	private void initTextColor(TextView tv) {
		tv_first.setTextColor(0xff333333);
		tv_second.setTextColor(0xff333333);
		tv_third.setTextColor(0xff333333);
		tv.setTextColor(0xfff66e00);
	}

	// 显示选择的弹出框
	private void popDialog() {
		// type是1，代表选择全部分类，2代表排序方式

		popWindow = new PopupWindow();
		popWindow.setContentView(dialogView);
		popWindow.setWidth(Constants.getScreenWidth());
		popWindow.setHeight(Constants.getScreenHeight());
		popWindow.showAsDropDown(tv_title);
	}

	class MoneyDetailExpandableAdapter extends BaseExpandableListAdapter {
		private List<IncomePrjStatListBean> list_group = new ArrayList<IncomePrjStatListBean>();

		public void setList(List<IncomePrjStatListBean> list) {
			if (list != null) {
				list_group.clear();
				list_group.addAll(list);
			}
			notifyDataSetChanged();
		}

		/*
		 * 获取当前适配器中加载的组的个数
		 */
		@Override
		public int getGroupCount() {
			return list_group.size();
		}

		/*
		 * 根据groupPosition 组的下标获取每个组中加载的数据的条目
		 */
		@Override
		public int getChildrenCount(int groupPosition) {
			// return list_group.get(groupPosition).getList().size();
			IncomePrjStatListBean ormAddr = list_group.get(groupPosition);
			return ormAddr.getPrj_stat_list() == null ? 0 : ormAddr
					.getPrj_stat_list().size();
		}

		/*
		 * 根据groupPosition 组的下标获取当前组的对象
		 */
		@Override
		public Object getGroup(int groupPosition) {
			return list_group.get(groupPosition);
		}

		/*
		 * 根据groupPosition 组的下标以及childPosition 子的下标获取 指定组中的指定下标的对象
		 */
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// return
			// list_group.get(groupPosition).getList().get(childPosition);
			IncomePrjStatListBean bean = list_group.get(groupPosition);
			return bean.getPrj_stat_list().get(childPosition);
		}

		/*
		 * 根据groupPosition 组下标获取行id
		 */
		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		/*
		 * 根据groupPosition组下标中的childPosition 子下标获取指定组中的指定下标的行id
		 */
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		/*
		 * child的id与对象的id是否相同 与底层相关
		 */
		@Override
		public boolean hasStableIds() {
			return true;
		}

		/*
		 * boolean isExpanded 表示当前的组是否可展开
		 */
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			IncomePrjStatListBean addr = (IncomePrjStatListBean) adapter
					.getGroup(groupPosition);
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(getActivity());
				convertView = inflater.inflate(R.layout.item_moneryrecord_tag,
						null);
				holderGroup = new ViewHolderGroup();
				holderGroup.tv_city = (TextView) convertView
						.findViewById(R.id.tv_item_money_record_tag);
				convertView.setTag(holderGroup);
			} else {
				holderGroup = (ViewHolderGroup) convertView.getTag();
			}
			holderGroup.tv_city.setText(addr.getDay_desc());
			return convertView;
		}

		/*
		 * boolean isLastChild 表示当前绘制的item是否是当前组中的最后一个item
		 */
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			PrjStatBean itemBean = (PrjStatBean) adapter.getChild(
					groupPosition, childPosition);
			ViewHolder holder = null;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.item_getmoney_detail,
						null);
				holder = new ViewHolder();
				holder.tv_time = (TextView) convertView
						.findViewById(R.id.tv_item_moneydetail_time);
				holder.tv_money = (TextView) convertView
						.findViewById(R.id.tv_item_moneydetail_money);
				holder.tv_title = (TextView) convertView
						.findViewById(R.id.tv_item_moneydetail_title);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (itemBean != null) {
				holder.tv_time.setText(itemBean.getCreate_time());
				holder.tv_title.setText(itemBean.getName());
				holder.tv_money.setText("+" + itemBean.getMoney() + "元");
			}
			return convertView;
		}

		/*
		 * 表示组中的child是否能够被点击 返回true表示可以相应child的事件
		 */
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}

	static class ViewHolder {
		TextView tv_time;
		TextView tv_money;
		TextView tv_title;
	}

	static class ViewHolderGroup {
		TextView tv_city;
	}

	// 请求列表数据
	private void requestData(Listener<String> listener, int index) {
		HashMap<String, String> postMap = new HashMap<String, String>();
		if (isItem != null && isItem.equals("item")) {
			time_filter_type = 2;
		} else {
			if (show_time.equals("seven")) {
				time_filter_type = 0;
			} else if (show_time.equals("month")) {
				time_filter_type = 1;
			} else {
				time_filter_type = 3;
			}
		}
		postMap.put("time_filter_type", time_filter_type + "");

		if (from_tag.equals("task")) {
			category_filter_type = 1;
		} else if (from_tag.equals("act")) {
			category_filter_type = 2;
		} else {
			category_filter_type = 0;
		}
		postMap.put("category_filter_type", category_filter_type + "");
		if (start_date != null && !TextUtils.isEmpty(start_date)) {
			postMap.put("start_date", start_date);
		}
		if (end_date != null && !TextUtils.isEmpty(end_date)) {
			postMap.put("end_date", end_date);
		}
		postMap.put("page_idx", index + "");
		RequestDataUtil.getRequestInstance().requestData(listener, postMap,
				AppConstants.income_prj_stat_list, getActivity(), null, "true");
	}

	// 解析列表数据
	private void parserData(String response, MODE mode) {
		try {
			JSONObject jsbJson = new JSONObject(response);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			Gson gson = new Gson();
			if (statusCode.equals("200")) {
				List<IncomePrjStatListBean> taskList = gson.fromJson(
						jsbJson.getString("data"),
						new TypeToken<List<IncomePrjStatListBean>>() {
						}.getType());
				if (mode == MODE.DOWN) {
					index = 0;
					dataList.clear();
				} else if (mode == MODE.UP) {
					if (taskList != null && taskList.size() > 0) {
						index++;
					}
				}
				if (taskList != null) {
					if (dataList.size() != 0) {
						for (int i = 0; i < taskList.size(); i++) {

							String key = dataList.get(dataList.size() - 1)
									.getDay_desc();
							String newKey = taskList.get(i).getDay_desc();
							if (TextUtils.equals(key, newKey)) {
								dataList.get(dataList.size() - 1)
										.getPrj_stat_list()
										.addAll(taskList.get(i)
												.getPrj_stat_list());
							} else {
								dataList.add(taskList.get(i));
							}
						}
					} else {
						dataList.addAll(taskList);
					}
				}
				adapter.setList(dataList);
				for (int i = 0; i < adapter.getGroupCount(); i++) {
					listView.expandGroup(i);
				}
			} else {
				UIUtil.showToast(getActivity(), statusMsg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
