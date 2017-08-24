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
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.orm.MoneyRecordBean;
import com.dingapp.biz.db.orm.MoneyRecordItemBean;
import com.dingapp.biz.net.RequestDataUtil;
import com.dingapp.biz.page.customview.MyExpandableListView;
import com.dingapp.biz.util.StopRefresh;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.UIUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 提现记录页面
 * 
 * @author king
 * 
 */
public class MoneyRecordFragment extends BaseFragment {
	private ImageView iv_back;
	private MyExpandableListView listView;
	private PullToRefreshScrollView pullScrollView;
	private ViewHolderGroup holderGroup = null;

	public static enum MODE {
		UP, DOWN
	}

	private int index = 0;
	private List<MoneyRecordBean> dataList = new ArrayList<MoneyRecordBean>();
	private MyExpandableAdapter adapter;
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
	private boolean falg;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			popStack(null);
			return true;
		}
		return false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.money_record, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView(getView());
		initListener();
		adapter = new MyExpandableAdapter();
		listView.setAdapter(adapter);
		initData(refreshListener, 0);
		listView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				parent.expandGroup(groupPosition);
				return true;
			}
		});
	}

	private void initView(View view) {
		iv_back = (ImageView) view.findViewById(R.id.iv_moneyrecord_back);
		listView = (MyExpandableListView) view
				.findViewById(R.id.lv_money_record);
		pullScrollView = (PullToRefreshScrollView) view
				.findViewById(R.id.moneyrecord_pulltorefreshscrollview);
		StopRefresh.initRefreshView(pullScrollView, Mode.PULL_FROM_END);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initListener() {
		iv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popStack(null);
			}
		});
		pullScrollView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase refreshView) {
						StopRefresh.stopRefreash(pullScrollView);
						initData(refreshListener, 0);
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase refreshView) {
						StopRefresh.stopRefreash(pullScrollView);
						initData(moreListener, index + 1);
					}
				});
	}

	class MyExpandableAdapter extends BaseExpandableListAdapter {
		private List<MoneyRecordBean> list_group = new ArrayList<MoneyRecordBean>();

		public void setList(List<MoneyRecordBean> list) {
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
			MoneyRecordBean ormAddr = list_group.get(groupPosition);
			return ormAddr.getRec_list() == null ? 0 : ormAddr.getRec_list()
					.size();
			// return
			// ((FriendAddr)list_group.get(groupPosition)).getListMember().size();
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
			MoneyRecordBean bean = list_group.get(groupPosition);
			return bean.getRec_list().get(childPosition);
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
			MoneyRecordBean addr = (MoneyRecordBean) adapter
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
			holderGroup.tv_city.setText(addr.getMonth_desc());
			return convertView;
		}

		/*
		 * boolean isLastChild 表示当前绘制的item是否是当前组中的最后一个item
		 */
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			MoneyRecordItemBean itemBean = (MoneyRecordItemBean) adapter
					.getChild(groupPosition, childPosition);
			ViewHolder holder = null;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(
						R.layout.item_moneyrecord_detail, null);
				holder = new ViewHolder();
				holder.iv_paytype = (ImageView) convertView
						.findViewById(R.id.iv_item_moneyrecord_paytype);
				holder.tv_time = (TextView) convertView
						.findViewById(R.id.tv_item_moneyrecord_time);
				holder.tv_money = (TextView) convertView
						.findViewById(R.id.tv_item_moneyrecord_money);
				holder.tv_status = (TextView) convertView
						.findViewById(R.id.tv_item_moneyrecord_status);
				holder.tv_extra_msg = (TextView) convertView
						.findViewById(R.id.tv_refuse_reason);
				holder.v_blank = convertView.findViewById(R.id.v_blank);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (itemBean != null) {
				if (itemBean.getChannel().equals("微信钱包")) {
					holder.iv_paytype.setImageResource(R.drawable.iv_wx);
				} else {
					holder.iv_paytype.setImageResource(R.drawable.iv_zfb);
				}
				String time = itemBean.getWithdraw_time();
				String time2 = time.substring(time.indexOf(' ') - 2,
						time.indexOf(' '));
				String time3 = time.substring(time.lastIndexOf(' '));
				holder.tv_time.setText(time2 + "日 " + time3);
				holder.tv_money.setText("提现" + itemBean.getMoney());
				holder.tv_status.setText(itemBean.getStatus());
				if (itemBean.getStatus() != null
						&& itemBean.getStatus().equals("已到账")) {
					holder.tv_status.setTextColor(0xff42a7ff);
				} else {
					holder.tv_status.setTextColor(0xfff66e00);
				}
				if (itemBean.getExtra_msg() != null
						&& !TextUtils.isEmpty(itemBean.getExtra_msg())) {
					holder.tv_extra_msg.setVisibility(View.VISIBLE);
					holder.tv_extra_msg.setText("拒绝理由: "
							+ itemBean.getExtra_msg());
					holder.v_blank.setVisibility(View.VISIBLE);
				} else {
					holder.tv_extra_msg.setVisibility(View.GONE);
					holder.v_blank.setVisibility(View.GONE);
				}

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
		ImageView iv_paytype;
		TextView tv_time;
		TextView tv_money;
		TextView tv_status;
		TextView tv_extra_msg;
		View v_blank;
	}

	static class ViewHolderGroup {
		TextView tv_city;
	}

	private void initData(Listener<String> listener, int index) {
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("page_idx", index + "");
		postMap.put("type", "normal");
		RequestDataUtil.getRequestInstance().requestData(listener, postMap,
				AppConstants.withdraw_cash_rec, getActivity(), null, "true");
	}

	private void parserData(String response, MODE mode) {
		try {
			JSONObject jsbJson = new JSONObject(response);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			Gson gson = new Gson();
			if (statusCode.equals("200")) {
				List<MoneyRecordBean> list = gson.fromJson(
						jsbJson.getString("data"),
						new TypeToken<List<MoneyRecordBean>>() {
						}.getType());
				if (mode == MODE.DOWN) {
					if (falg) {
						if (list != null && list.size() > 0) {
							UIUtil.showToast(getActivity(), "页面刷新完成");
						} else {
							UIUtil.showToast(getActivity(), "页面刷新完成,暂无数据");
						}

					}
					falg = true;
					index = 0;
					dataList.clear();
				} else if (mode == MODE.UP) {
					if (list != null && list.size() > 0) {
						index++;
					}
				}
				if (list != null) {
					if (dataList.size() != 0) {
						for (int i = 0; i < list.size(); i++) {

							String key = dataList.get(dataList.size() - 1)
									.getMonth_desc();
							String newKey = list.get(i).getMonth_desc();
							if (TextUtils.equals(key, newKey)) {
								dataList.get(dataList.size() - 1).getRec_list()
										.addAll(list.get(i).getRec_list());
							} else {
								dataList.add(list.get(i));
							}
						}

					} else {
						dataList.addAll(list);
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
