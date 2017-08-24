package com.dingapp.biz.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.orm.MemberBean;
import com.dingapp.biz.net.SingleRequestQueue;
import com.dingapp.biz.net.StringPostRequest;
import com.dingapp.biz.page.adapters.WeChatHomeAdapter;
import com.dingapp.biz.page.adapters.WeChatHomeAdapter.ContentClickListener;
import com.dingapp.biz.page.swpiedelete.SwipeLayoutManager;
import com.dingapp.biz.util.OpenPageToMemberDetailUtils;
import com.dingapp.biz.util.Utils;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.AndroidUtil;
import com.dingapp.core.util.UIUtil;
import com.google.gson.Gson;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.dbcontacts.dbcontact.ContactDao;
import com.hyphenate.easeui.domain.EaseUser;

public class WeChatHomeFragment extends BaseFragment implements OnClickListener {
	public static int DLEA_MSG = 1000;
	private ImageView img_back;
	private ImageView img_pop_add;
	private ImageView img_wechat_contact;
	private ListView wechat_contact_list_view;
	private WeChatHomeAdapter chatHomeAdapter;
	private String currentUserName;
	private RelativeLayout rl_friend_apply;
	private ErrorListener errorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {

		}
	};
	private TextView tv_msg_apply;
	private TextView tv_apply_username;
	private TextView tv_apply_content;
	private TextView tv_apply_time;
	private ImageView tv_msg_notify;
	private Listener<String> listener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parseData(response);
		}
	};
	private EMMessageListener arg0;
	private String apply_url;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return View.inflate(getActivity(), R.layout.wechat_home, null);
	}

	protected void parseData(String response) {
		Gson gson = new Gson();
		MemberBean memberBean = gson.fromJson(response, MemberBean.class);
		if (memberBean.getStatusCode().equals("200")) {
			if ("true".equals(memberBean.getData().getIs_friend())) {
				Bundle bundle = new Bundle();
				bundle.putString(EaseConstant.EXTRA_USER_ID, currentUserName);
				bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE,
						EaseConstant.CHATTYPE_SINGLE);
				openPage("wechat_chat", bundle, false);
			} else {
				UIUtil.showToast(getActivity(), "当前不是好友，请先添加好友再聊天");
				EMClient.getInstance().chatManager()
						.deleteConversation(currentUserName, true);
				String subSequence = currentUserName.substring(11,
						currentUserName.length());
				OpenPageToMemberDetailUtils.openPage(WeChatHomeFragment.this,
						subSequence, true);
			}

		} else if (memberBean.getStatusCode().equals("1001")) {

		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		int msgCnt = 0;
		if (getArguments() != null
				&& getArguments().containsKey(AppConstants.KEY)) {
			msgCnt = getArguments().getInt(AppConstants.KEY);
		}
		initView();
		initListener();
		initData();
		initMessage();
		initFiMessage();
		initAnimation(msgCnt);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			SwipeLayoutManager.getInstance().closeCurrentLayout();
			SwipeLayoutManager.getInstance().clearCurrentLayout();
			Bundle bundle = new Bundle();
			bundle.putString("msg_refresh", "true");
			popBack(bundle);
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	private void initFiMessage() {

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				EMClient.getInstance().chatManager().loadAllConversations();
				List<EMConversation> loadConversationList = loadConversationList();
				chatHomeAdapter.setData(loadConversationList);
			}
		});
	}

	protected List<EMConversation> loadConversationList() {
		// 获取所有会话，包括陌生人
		Map<String, EMConversation> conversations = EMClient.getInstance()
				.chatManager().getAllConversations();
		// 过滤掉messages size为0的conversation
		/**
		 * 如果在排序过程中有新消息收到，lastMsgTime会发生变化 影响排序过程，Collection.sort会产生异常
		 * 保证Conversation在Sort过程中最后一条消息的时间不变 避免并发问题
		 */
		List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
		synchronized (conversations) {
			for (EMConversation conversation : conversations.values()) {
				if (conversation.getAllMessages().size() != 0) {
					// if(conversation.getType() !=
					// EMConversationType.ChatRoom){
					sortList.add(new Pair<Long, EMConversation>(conversation
							.getLastMessage().getMsgTime(), conversation));
					// }
				}
			}
		}
		try {
			// Internal is TimSort algorithm, has bug
			sortConversationByLastChatTime(sortList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<EMConversation> list = new ArrayList<EMConversation>();
		for (Pair<Long, EMConversation> sortItem : sortList) {
			list.add(sortItem.second);
		}
		return list;
	}

	@Override
	protected void onDataReset(Bundle bundle) {
		super.onDataReset(bundle);
		if (bundle != null && bundle.containsKey("refresh")) {
			initAnimation(0);
		}
	}

	/**
	 * 根据最后一条消息的时间排序
	 * 
	 * @param usernames
	 */
	private void sortConversationByLastChatTime(
			List<Pair<Long, EMConversation>> conversationList) {
		Collections.sort(conversationList,
				new Comparator<Pair<Long, EMConversation>>() {
					@Override
					public int compare(final Pair<Long, EMConversation> con1,
							final Pair<Long, EMConversation> con2) {

						if (con1.first == con2.first) {
							return 0;
						} else if (con2.first > con1.first) {
							return 1;
						} else {
							return -1;
						}
					}

				});
	}

	private void initMessage() {
		arg0 = new EMMessageListener() {

			@Override
			public void onMessageReceived(List<EMMessage> arg0) {
				// TODO Auto-generated method stub
				initFiMessage();
			}

			@Override
			public void onMessageReadAckReceived(List<EMMessage> arg0) {
				// TODO Auto-generated method stub

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
		EMClient.getInstance().chatManager().addMessageListener(arg0);
	}

	private void initData() {
		chatHomeAdapter = new WeChatHomeAdapter(this, getActivity());
		wechat_contact_list_view.setAdapter(chatHomeAdapter);
		chatHomeAdapter.setContentClickListener(new ContentClickListener() {

			@Override
			public void onItemClick(EMConversation conversation) {
				// TODO Auto-generated method stub
				if (Utils.isClickable()) {
					if (conversation == null) {
						return;
					}
					currentUserName = conversation.getUserName();
					if ("admin".equals(currentUserName)) {
						Bundle bundle = new Bundle();
						bundle.putString(EaseConstant.EXTRA_USER_ID,
								currentUserName);
						bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE,
								EaseConstant.CHATTYPE_SINGLE);
						openPage("wechat_chat", bundle, false);
						return;
					}
					EaseUser easeUser = new ContactDao(getActivity())
							.query(currentUserName);
					if (easeUser != null
							&& "true".equals(easeUser.getIs_corp())) {
						Bundle bundle = new Bundle();
						bundle.putString(EaseConstant.EXTRA_USER_ID,
								currentUserName);
						bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE,
								EaseConstant.CHATTYPE_SINGLE);
						openPage("wechat_chat", bundle, false);
						return;
					}
					if (!AndroidUtil.isNetworkAvailable(getActivity())) {
						Bundle bundle = new Bundle();
						bundle.putString(EaseConstant.EXTRA_USER_ID,
								currentUserName);
						bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE,
								EaseConstant.CHATTYPE_SINGLE);
						openPage("wechat_chat", bundle, false);
					} else {
						Bundle bundle = new Bundle();
						bundle.putString(EaseConstant.EXTRA_USER_ID,
								currentUserName);
						bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE,
								EaseConstant.CHATTYPE_SINGLE);
						openPage("wechat_chat", bundle, false);
					}
				}
			}

			@Override
			public void deleteClick(EMConversation conversation) {
				SwipeLayoutManager.getInstance().closeCurrentLayout();
				SwipeLayoutManager.getInstance().clearCurrentLayout();
				if (Utils.isClickable()) {
					String userName = conversation.getUserName();
					boolean deleteConversation = EMClient.getInstance()
							.chatManager().deleteConversation(userName, true);
					if (deleteConversation) {
						initFiMessage();
					}
				}

			}
		});
	}

	private Animation animation;

	public void initAnimation(int count) {
		if (count > 0) {
			tv_msg_notify.setVisibility(View.VISIBLE);
			animation = AnimationUtils.loadAnimation(getActivity(),
					R.anim.message_notify);
			tv_msg_notify.setAnimation(animation);
			animation.startNow();
		} else {
			if (animation != null) {
				animation.cancel();
			}
			tv_msg_notify.clearAnimation();
			tv_msg_notify.setVisibility(View.GONE);
		}
	}

	private void queryMemberInfo(String member_id) {
		Map<String, String> postMap = new HashMap<String, String>();
		String url = AppConstants.BaseUrl
				+ "/api/v1/member/query_member_profile";
		postMap.put("session_id", AppConstants.member == null ? ""
				: AppConstants.member.getSessionId());
		postMap.put("member_id", member_id);
		postMap.put("platform", AppConstants.PLATFORM);
		StringPostRequest stringPostRequest = new StringPostRequest(postMap,
				url, listener, errorListener);
		SingleRequestQueue.getreRequestQueue(getActivity()).add(
				stringPostRequest);

	}

	private void initListener() {
		img_back.setOnClickListener(this);
		img_pop_add.setOnClickListener(this);
		img_wechat_contact.setOnClickListener(this);
		rl_friend_apply.setOnClickListener(this);
	}

	private void initView() {
		img_back = (ImageView) getView().findViewById(R.id.img_back);
		img_pop_add = (ImageView) getView().findViewById(R.id.img_pop_add);
		tv_msg_apply = (TextView) getView().findViewById(R.id.tv_msg_apply);
		tv_msg_apply.setVisibility(View.GONE);
		tv_apply_username = (TextView) getView().findViewById(
				R.id.tv_apply_username);
		tv_apply_content = (TextView) getView().findViewById(
				R.id.tv_apply_content);
		tv_apply_time = (TextView) getView().findViewById(R.id.tv_apply_time);

		rl_friend_apply = (RelativeLayout) getView().findViewById(
				R.id.rl_friend_apply);
		img_wechat_contact = (ImageView) getView().findViewById(
				R.id.img_wechat_contact);
		wechat_contact_list_view = (ListView) getView().findViewById(
				R.id.wechat_contact_list_view);
		tv_msg_notify = (ImageView) getView().findViewById(R.id.tv_msg_notify);
		tv_msg_notify.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		if (!Utils.isClickable()) {
			return;
		}
		if (v == img_back) {
			SwipeLayoutManager.getInstance().closeCurrentLayout();
			SwipeLayoutManager.getInstance().clearCurrentLayout();
			Bundle bundle = new Bundle();
			bundle.putString("msg_refresh", "true");
			popBack(bundle);
			return;
		} else if (v == img_pop_add) {
			Bundle bundle = new Bundle();
			bundle.putString("apply_url", apply_url + "");
			openPage("look_mobile_wxfriends", bundle, true);
			// showPop();
			return;
		} else if (v == img_wechat_contact) {
			openPage("wechat_contact", null, true);
			return;
		} else if (v == rl_friend_apply) {
			openPage("member_news", null, false);
			return;
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		if (!hidden) {
			initFiMessage();
		}
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EMClient.getInstance().chatManager().removeMessageListener(arg0);
	}
}
