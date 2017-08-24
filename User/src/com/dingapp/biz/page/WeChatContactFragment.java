package com.dingapp.biz.page;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.orm.FriendExamineBean;
import com.dingapp.biz.db.orm.FriendExamineBean.DateEntity;
import com.dingapp.biz.db.orm.StanderBean;
import com.dingapp.biz.net.SingleRequestQueue;
import com.dingapp.biz.net.StringPostRequest;
import com.dingapp.biz.util.Utils;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.AndroidUtil;
import com.dingapp.core.util.UIUtil;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.dbcontacts.dbcontact.ContactDao;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.easeui.ui.EaseContactListFragment.EaseContactListItemClickListener;
import com.hyphenate.easeui.ui.EaseContactListFragment.LeftClickListener;
import com.hyphenate.easeui.utils.EaseUserUtils;

public class WeChatContactFragment extends BaseFragment {
	private int idx;
	private ErrorListener errorListener;
	private RequestQueue queue;
	private Map<String, EaseUser> contactMap = new HashMap<String, EaseUser>();
	private Listener<String> listener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parseData(response);
		}
	};
	private EaseContactListFragment easeContactListFragment;
	private Dialog cancled_dialog;
	private Listener<String> delListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parseDelData(response);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return View.inflate(getActivity(), R.layout.wechat_contact, null);
	}

	protected void parseDelData(String response) {
		Gson gson = new Gson();
		StanderBean standerBean = gson.fromJson(response, StanderBean.class);
		if (standerBean.getStatusCode().equals("200")) {
			if ("true".equals(standerBean.getData().getSuc())) {
				UIUtil.showToast(getActivity(), "删除好友成功");
				new ContactDao(getActivity()).del(currentUser.getUsername());
				cancled_dialog.dismiss();
				contactMap.clear();
				EMClient.getInstance().chatManager()
						.deleteConversation(currentUser.getUsername(), true);
				requestData();
				return;
			}
		}
	}

	protected void parseData(String response) {
		Gson gson = new Gson();
		FriendExamineBean friendExamineBean = gson.fromJson(response,
				FriendExamineBean.class);
		if (friendExamineBean.getStatusCode().equals("200")) {
			if (friendExamineBean != null
					&& friendExamineBean.getData().size() > 0) {
				EaseUser user = null;
				for (int i = 0; i < friendExamineBean.getData().size(); i++) {
					DateEntity dateEntity = friendExamineBean.getData().get(i);
					user = new EaseUser("huluhongbao"
							+ dateEntity.getMember_id());
					if (dateEntity.getMember_pic().getOrigin_url() != null) {
						user.setAvatar(dateEntity.getMember_pic()
								.getOrigin_url());
					}
					// 昵称
					if (dateEntity.getMember_nick_name() != null) {
						user.setNickName(dateEntity.getMember_nick_name());
					} else {
						user.setNickName("huluhongbao"
								+ dateEntity.getMember_id());
					}
					// 备注
					if (dateEntity.getMember_mark_name() != null) {
						user.setNickName(dateEntity.getMember_mark_name());
					}
					// friend_id
					user.setApply_id(dateEntity.getApply_id());
					user.setIs_corp("false");
					int update = new ContactDao(getActivity()).update(user);
					if (update == 0) {
						new ContactDao(getActivity()).add(user);
					}
					contactMap.put("huluhongbao" + dateEntity.getMember_id(),
							user);
				}
			}
			Map<String, EaseUser> queryAll = new ContactDao(getActivity()).queryAll("false");
			if(queryAll==null||queryAll.keySet()==null){
				return;
			}
			Iterator<String> iterator = queryAll.keySet().iterator();
			while (iterator.hasNext()) {
				String string = (String) iterator.next();
				if(!contactMap.containsKey(string)){
					int del = new ContactDao(getActivity()).del(string);
					EMClient.getInstance().chatManager().deleteConversation(string, true);
					Log.d("delete", string);
				}
			}
			Map<String, EaseUser> map = new ContactDao(getActivity()).queryAll(null);
			EaseUserUtils.contactMap = map;
			// 更新数据
			easeContactListFragment.refreshData(contactMap);
		} else if ("1001".equals(friendExamineBean.getStatusCode())) {
			// TODO
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		queue = SingleRequestQueue.getreRequestQueue(getActivity());
		initData();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void initData() {
		easeContactListFragment = new EaseContactListFragment();
		if (!AndroidUtil.isNetworkAvailable(getActivity())) {
			Map<String, EaseUser> queryAll = new ContactDao(getActivity())
					.queryAll("false");
			easeContactListFragment.setContactsMap(queryAll);
		}
		getActivity().getSupportFragmentManager().beginTransaction()
				.replace(R.id.fl_frame, easeContactListFragment).commit();
		easeContactListFragment.setLeftClickListener(new LeftClickListener() {

			@Override
			public void leftClick() {
				if (Utils.isClickable()) {
					popBack(null);
				}
			}
		});
		easeContactListFragment
				.setContactListItemClickListener(new EaseContactListItemClickListener() {

					@Override
					public void onListItemClicked(EaseUser user) {
						if (Utils.isClickable()) {
							if (user == null) {
								return;
							}
							String userName = user.getUsername();
							Bundle bundle = new Bundle();
							bundle.putString(EaseConstant.EXTRA_USER_ID,
									userName);
							bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE,
									EaseConstant.CHATTYPE_SINGLE);
							openPage("wechat_chat", bundle, false);
						}
					}

					@Override
					public void onListItemLongClicked(EaseUser user) {
						showCancledDilog(user);
					}
				});
		if (AndroidUtil.isNetworkAvailable(getActivity())) {
			requestData();
		}
	}

	private void showCancledDilog(final EaseUser user) {
		if(getActivity()==null){
			return;
		}
		cancled_dialog = new Dialog(getActivity(), R.style.dialog_style);
		View inflate = View.inflate(getActivity(), R.layout.cancled_layout,
				null);
		((TextView) (inflate.findViewById(R.id.tv_content)))
				.setText("你确定要删除好友：" + user.getNickName() + "吗？");
		inflate.findViewById(R.id.tv_cancled).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						cancled_dialog.dismiss();
					}
				});
		inflate.findViewById(R.id.tv_confirm).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						requestDel(user);
					}
				});
		cancled_dialog.setContentView(inflate);
		Window window = cancled_dialog.getWindow();
		window.setBackgroundDrawable(new BitmapDrawable());
		window.setGravity(Gravity.CENTER);
		LayoutParams params = window.getAttributes();
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		cancled_dialog.show();
	}

	private EaseUser currentUser;

	protected void requestDel(EaseUser user) {
		if (!AndroidUtil.isNetworkAvailable(getActivity())) {
			UIUtil.showToast(getActivity(), AppConstants.NetNotifice);
			return;
		}
		currentUser = user;
		Map<String, String> postMap = new HashMap<String, String>();
		postMap.put("session_id", AppConstants.member.getSessionId());
		postMap.put("platform", AppConstants.PLATFORM);
		postMap.put("friend_id", user.getApply_id() + "");
		String url = AppConstants.BaseUrl + "/api/v1/member/delete_friend";
		StringPostRequest request = new StringPostRequest(postMap, url,
				delListener, errorListener);
		queue.add(request);

	}

	private void requestData() {
		if (!AndroidUtil.isNetworkAvailable(getActivity())) {
			UIUtil.showToast(getActivity(), AppConstants.NetNotifice);
			return;
		}
		EaseUser easeUser = new EaseUser("huluhongbao"
				+ AppConstants.member.getMemberId());
		easeUser.setAvatar(AppConstants.member.getHeaderProfile());
		easeUser.setNickName(AppConstants.member.getNickName());
		easeUser.setIs_corp("true");
		if (new ContactDao(getActivity()).update(easeUser) == -1) {
			new ContactDao(getActivity()).add(easeUser);
		}
		Map<String, String> postMap = new HashMap<String, String>();
		postMap.put("session_id", AppConstants.member.getSessionId());
		postMap.put("platform", AppConstants.PLATFORM);
		postMap.put("examine_status", "pass");
		String url = AppConstants.BaseUrl
				+ "/api/v1/member/friend_examine_list";
		StringPostRequest request = new StringPostRequest(postMap, url,
				listener, errorListener);
		queue.add(request);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			getView().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					requestData();
				}
			}, 300);
		}
	}
//	@Override
//	protected void onDataReset(Bundle bundle) {
//		super.onDataReset(bundle);
//		if(bundle.containsKey("need_update")){
//			boolean boolean1 = bundle.getBoolean("need_update");
//			if(boolean1){
//				String userName = bundle.getString("username");
//				EaseUser userInfo = EaseUserUtils.getUserInfo(userName, getActivity());
//				contactMap.put(userName, userInfo);
//				easeContactListFragment.refreshData(contactMap);
//			}
//		}
//	}
}
