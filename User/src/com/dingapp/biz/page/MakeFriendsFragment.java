package com.dingapp.biz.page;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.orm.GoodsCategoryBean;
import com.dingapp.biz.db.orm.GoodsChildTypeBean;
import com.dingapp.biz.db.orm.GoodsTypeBean;
import com.dingapp.biz.net.RequestDataUtil;
import com.dingapp.biz.page.adapters.CategoryListLeftAdapter;
import com.dingapp.biz.page.adapters.CategoryRightListViewAdapter;
import com.dingapp.biz.util.LogoutUtils;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.LoggerUtil;
import com.dingapp.core.util.TestJsonUtil;
import com.dingapp.core.util.UIUtil;
import com.google.gson.Gson;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

/**
 * 分类模块
 * 
 * @author king
 * 
 */
public class MakeFriendsFragment extends BaseFragment implements
		OnClickListener {
	/**
	 * 搜索文本框
	 */
	private TextView tv_second_input;
	/**
	 * 分类中左边条目的item
	 */
	private ListView lv_left_item;
	/**
	 * 分类中右边条目的item
	 */
	private ListView lv_detail_item;
	/**
	 * 记录选中的位置
	 */
	private int selectedPosition;
	/**
	 * 
	 */
	private View layout;
	/**
	 * 右边listview条目数据
	 */
	private CategoryRightListViewAdapter categoryRightListViewAdapter;
	private CategoryListLeftAdapter leftAdapter;
	private TextView tv_no_data;
	private ImageView iv_right_news;
	private Listener<String> msgListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserMsgData(response);
		}
	};
	private int systemMsgCnt = 0;
	private EMMessageListener messageListener;
	private Listener<String> categoryListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parseCategoryData(response);
		}
	};
	private List<GoodsTypeBean> typeList;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.making_friends, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initListener();
		requestCategoryData();
		// 是否有新消息，1，系统消息，2，聊天的消息
		initMessages();
	}

	private void initMessages() {
		if (TextUtils.isEmpty(AppConstants.member.getSessionId())) {
			return;
		}
		messageListener = new EMMessageListener() {

			@Override
			public void onMessageReceived(List<EMMessage> arg0) {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						iv_right_news.setImageResource(R.drawable.iv_news_has);
					}
				});
			}

			@Override
			public void onMessageReadAckReceived(List<EMMessage> arg0) {

			}

			@Override
			public void onMessageDeliveryAckReceived(List<EMMessage> arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMessageChanged(EMMessage arg0, Object arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onCmdMessageReceived(List<EMMessage> arg0) {
				// TODO Auto-generated method stub

			}
		};
		EMClient.getInstance().chatManager()
				.addMessageListener(messageListener);
		int chatMsg = getCount();
		iv_right_news.setImageResource(R.drawable.iv_home_news);
		if (chatMsg > 0) {
			iv_right_news.setImageResource(R.drawable.iv_news_has);
		}
		requestMsgData();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			initMessages();
		}
	}

	public int getCount() {
		int unreadMsgCount = 0;
		EMClient.getInstance().chatManager().loadAllConversations();
		Map<String, EMConversation> conversations = EMClient.getInstance()
				.chatManager().getAllConversations();
		Set<Entry<String, EMConversation>> entrySet = conversations.entrySet();
		Iterator<Entry<String, EMConversation>> iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Map.Entry<java.lang.String, com.hyphenate.chat.EMConversation> entry = (Map.Entry<java.lang.String, com.hyphenate.chat.EMConversation>) iterator
					.next();
			EMConversation emConversation = entry.getValue();
			unreadMsgCount += emConversation.getUnreadMsgCount();
		}
		return unreadMsgCount;
	}

	private void initListener() {
		tv_second_input.setOnClickListener(this);
		iv_right_news.setOnClickListener(this);
		// 左边条目点击后右边条目跟着变动
		lv_left_item.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				LoggerUtil.d("item click", "curr：" + position + ", prev:"
						+ leftAdapter.getSelectedPosition());
				if (leftAdapter.getSelectedPosition() == position) {
					return;
				}
				leftAdapter.setSelectedPosition(position);
				leftAdapter.notifyDataSetChanged();
				// 右边的新数据设置

				List<GoodsChildTypeBean> sub_categories = typeList
						.get(position).getChild_type_info();
				String imgUrl = "";
				int focusId = -1;
				if (typeList.get(position).getType_pic() != null) {
					imgUrl = typeList.get(position).getType_pic()
							.getDetail_url();
					focusId = typeList.get(position).getType_id();
				}
				if (sub_categories != null && sub_categories.size() > 0) {
					lv_detail_item.setVisibility(View.VISIBLE);
					tv_no_data.setVisibility(View.GONE);
					categoryRightListViewAdapter.setData(sub_categories,
							imgUrl, focusId);
					categoryRightListViewAdapter.notifyDataSetChanged();
				} else {
					lv_detail_item.setVisibility(View.GONE);
					tv_no_data.setVisibility(View.VISIBLE);
				}
				lv_detail_item.setSelection(0);
			}
		});
	}

	/**
	 * 第一次初始化数据
	 */
	private void initListViewData() {
		// 左边数据
		if (typeList != null && typeList.size() > 0) {
			leftAdapter = new CategoryListLeftAdapter(getActivity(), typeList);
			lv_left_item.setAdapter(leftAdapter);
			// 第一次初始化条目，右边条目默认选择为0；
			List<GoodsChildTypeBean> child_type_info = typeList.get(0)
					.getChild_type_info();
			String imgUrl = "";
			int focusId = -1;
			if (typeList.get(0).getType_pic() != null) {
				imgUrl = typeList.get(0).getType_pic().getDetail_url();
				focusId = typeList.get(0).getType_id();
			}
			categoryRightListViewAdapter = new CategoryRightListViewAdapter(
					getActivity(), MakeFriendsFragment.this);
			lv_detail_item.setAdapter(categoryRightListViewAdapter);
			categoryRightListViewAdapter.setData(child_type_info, imgUrl,
					focusId);
			initListener();
		}
	}

	private void requestCategoryData() {
		if (AppConstants.TEST_MODE) {
			String json = TestJsonUtil.getTestJson("goods_category");
			parseCategoryData(json);
			return;
		}
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("is_homepage", "true");
		postMap.put("type", "normal");
		postMap.put("need_brand", "false");
		String url = AppConstants.category_list;
		RequestDataUtil.getRequestInstance().requestData(categoryListener,
				postMap, url, getActivity(), null, "normal");
	}

	private void initView() {
		tv_second_input = (TextView) getView().findViewById(
				R.id.et_secondhome_search);
		lv_left_item = (ListView) getView().findViewById(R.id.lv_categroy_list);
		lv_detail_item = (ListView) getView().findViewById(
				R.id.lv_categroy_detail_list);
		tv_no_data = (TextView) getView().findViewById(R.id.tv_no_data);
		iv_right_news = (ImageView) getView().findViewById(
				R.id.img_second_right_news);
	}

	@Override
	public void onClick(View v) {
		if (v == tv_second_input) {
			Bundle bundle = new Bundle();
			bundle.putString("home_search", "true");
			openPage("goods_category", bundle, false);
			return;
		}
		if (v == iv_right_news) {
			if (AppConstants.member.getSessionId() == null
					|| TextUtils.isEmpty(AppConstants.member.getSessionId())) {
				openPage("login", null, false);
				return;
			}
			Bundle bundle = new Bundle();
			bundle.putInt(AppConstants.KEY, systemMsgCnt);
			openPage("wechat_home", bundle, false);
			return;
		}
	}

	/**
	 * @param response
	 */
	protected void parseCategoryData(String response) {
		try {
			JSONObject jsbJson = new JSONObject(response);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			Gson gson = new Gson();
			if (statusCode.equals("200")) {
				GoodsCategoryBean cateBean = gson.fromJson(
						jsbJson.getString("data"), GoodsCategoryBean.class);
				typeList = cateBean.getType_info();
				initListViewData();
			} else {
				UIUtil.showToast(getActivity(), statusMsg);
			}
		} catch (Exception e) {
		}
	}

	private void requestMsgData() {
		HashMap<String, String> postMap = new HashMap<String, String>();
		RequestDataUtil.getRequestInstance().requestData(msgListener, postMap,
				AppConstants.has_new_msg, getActivity(), null, "true");
	}

	private void parserMsgData(String json) {
		try {
			JSONObject jsbJson = new JSONObject(json);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			if (statusCode.equals("200")) {
				JSONObject dataJson = jsbJson.getJSONObject("data");
				if (dataJson.has("suc")
						&& dataJson.getString("suc").equals("true")) {
					iv_right_news.setImageResource(R.drawable.iv_news_has);
					systemMsgCnt = 1;
				} else {
					systemMsgCnt = 0;
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
