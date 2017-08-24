package com.dingapp.biz.page;

import java.util.HashMap;
import java.util.Map;

import view.refresh.PullToRefreshBase;
import view.refresh.PullToRefreshBase.Mode;
import view.refresh.PullToRefreshListView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.orm.FriendApplyBean;
import com.dingapp.biz.db.orm.StanderBean;
import com.dingapp.biz.hx.HXContactUtils;
import com.dingapp.biz.net.SingleRequestQueue;
import com.dingapp.biz.net.StringPostRequest;
import com.dingapp.biz.page.adapters.WeChatApplyFriendAdapter;
import com.dingapp.biz.page.adapters.WeChatApplyFriendAdapter.CheckFriendStatusListener;
import com.dingapp.biz.page.adapters.WeChatApplyFriendAdapter.DeleteListener;
import com.dingapp.biz.page.swipyrefreshlayoutlibrary.SwipyRefreshLayout;
import com.dingapp.biz.page.swpiedelete.SwipeLayoutManager;
import com.dingapp.biz.util.StopRefresh;
import com.dingapp.biz.util.Utils;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.AndroidUtil;
import com.dingapp.core.util.UIUtil;
import com.google.gson.Gson;

public class WeChatApplyFriendsFragment extends BaseFragment implements
		OnClickListener {
	private ImageView img_back;
	private PullToRefreshListView wechat_apply_list_view;
	private RequestQueue queue;
	private int idx;
	private int delPos = -1;
	private boolean flag = false;
	private enum MODE {
		DOWN, UP
	}

	private ErrorListener errorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {

		}
	};
	private Listener<String> downListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parseData(response, MODE.DOWN);
		}
	};
	private Listener<String> upListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parseData(response, MODE.UP);
		}
	};
	private Listener<String> delListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parseDelData(response);
		}
	};
	private WeChatApplyFriendAdapter friendAdapter;
	private Listener<String> checkListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parseCheckData(response);
		}
	};
	private SwipyRefreshLayout swpipy_layout;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return View.inflate(getActivity(), R.layout.wechat_friend_apply, null);
	}

	protected void parseCheckData(String response) {
		Gson gson = new Gson();
		StanderBean fromJson = gson.fromJson(response, StanderBean.class);
		if ("200".equals(fromJson.getStatusCode())) {
			UIUtil.showToast(getActivity(), "操作成功");
			HXContactUtils.getInstance(getActivity()).rquestHXFriend();
			rquestApplyData(downListener, 0);
		} else {
			UIUtil.showToast(getActivity(), "操作失败");
		}
	}

	protected void parseDelData(String response) {
		try {
			Gson gson = new Gson();
			StanderBean standerBean = gson
					.fromJson(response, StanderBean.class);
			if (standerBean.getStatusCode().equals("200")) {
				if (standerBean.getData().getSuc() != null
						&& "true".equals(standerBean.getData().getSuc())) {
					friendAdapter.removePos(delPos);
				} else {
					UIUtil.showToast(getActivity(), "删除记录失败");
				}
			} else if (standerBean.getStatusCode().equals("1001")) {
				// TODO
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void parseData(String response, MODE mode) {
		Gson gson = new Gson();
		FriendApplyBean friendExamineBean = gson.fromJson(response,
				FriendApplyBean.class);
		if (friendExamineBean.getStatusCode().equals("200")) {
			if (mode == MODE.DOWN) {
				if(flag){
					UIUtil.showToast(getActivity(), "页面刷新完成");
				}
				flag = true;
				idx = 0;
				friendAdapter.setData(friendExamineBean.getData());
			} else if (mode == MODE.UP) {
				if (friendExamineBean.getData() == null
						|| friendExamineBean.getData().size() == 0) {
					UIUtil.showToast(getActivity(), getActivity()
							.getResources().getString(R.string.no_more_data));
				}
				idx++;
				friendAdapter.setMoreData(friendExamineBean.getData());
			}
			// if (swpipy_layout != null && swpipy_layout.isRefreshing()) {
			// swpipy_layout.setRefreshing(false);
			// }
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		queue = SingleRequestQueue.getreRequestQueue(getActivity());
		initView();
		initListener();
		getView().postDelayed(new Runnable() {

			@Override
			public void run() {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						rquestApplyData(downListener, 0);
					}
				});
			}
		}, 300);
	}

	private void rquestApplyData(Listener<String> listener, int page_idx) {
		if (!AndroidUtil.isNetworkAvailable(getActivity())) {
			UIUtil.showToast(getActivity(), AppConstants.NetNotifice);
			return;
		}
		Map<String, String> postMap = new HashMap<String, String>();
		postMap.put("session_id", AppConstants.member.getSessionId());
		postMap.put("platform", AppConstants.PLATFORM);
		postMap.put("page_idx", page_idx + "");
		String url = AppConstants.BaseUrl + "/api/v1/member/friend_apply_list";
		StringPostRequest request = new StringPostRequest(postMap, url,
				listener, errorListener);
		queue.add(request);
	}

	private void initListener() {
		img_back.setOnClickListener(this);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initView() {
		img_back = (ImageView) getView().findViewById(R.id.img_back);
		wechat_apply_list_view = (PullToRefreshListView) getView()
				.findViewById(R.id.wechat_apply_list_view);
		// swpipy_layout = (SwipyRefreshLayout) getView().findViewById(
		// R.id.swpipy_layout);
		// swpipy_layout.setOnRefreshListener(this);
		StopRefresh.initRefreshView(wechat_apply_list_view, Mode.BOTH);
		wechat_apply_list_view
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase refreshView) {
						StopRefresh.stopRefreash(wechat_apply_list_view);
						rquestApplyData(downListener, 0);
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase refreshView) {
						StopRefresh.stopRefreash(wechat_apply_list_view);
						rquestApplyData(upListener, idx + 1);
					}
				});
		friendAdapter = new WeChatApplyFriendAdapter(this, getActivity());
		wechat_apply_list_view.setAdapter(friendAdapter);
		friendAdapter.setCheckFriendStatus(new CheckFriendStatusListener() {

			@Override
			public void refuse(int apply_id) {
				requestStatus(apply_id, "refuse");
			}

			@Override
			public void accept(int apply_id) {
				requestStatus(apply_id, "pass");
			}
		});
		friendAdapter.setDeleteListener(new DeleteListener() {

			@Override
			public void delete(int pos, int applyId) {
				SwipeLayoutManager.getInstance().closeCurrentLayout();
				SwipeLayoutManager.getInstance().clearCurrentLayout();
				if (!AndroidUtil.isNetworkAvailable(getActivity())) {
					UIUtil.showToast(getActivity(), "当前网络不可用");
					return;
				}
				if (!Utils.isClickable()) {
					return;
				}
				delPos = pos;
				requestDeleteRecode(applyId);
			}

			@Override
			public void clickItem(int member_id) {
				if (!Utils.isClickable()) {
					return;
				}
				Bundle bundle = new Bundle();
				bundle.putString("member_id", member_id + "");
				openPage("personal_info", bundle, false);
			}
		});
	}

	protected void requestStatus(int apply_id, String string) {
		Map<String, String> postMap = new HashMap<String, String>();
		String url = AppConstants.BaseUrl + "/api/v1/member/examine_friend";
		postMap.put("session_id", AppConstants.member.getSessionId());
		postMap.put("apply_id", apply_id + "");
		postMap.put("apply_status", string);
		postMap.put("platform", AppConstants.PLATFORM);
		StringPostRequest request = new StringPostRequest(postMap, url,
				checkListener, errorListener);
		queue.add(request);
	}

	protected void requestDeleteRecode(int applyId) {
		Map<String, String> postMap = new HashMap<String, String>();
		postMap.put("platform", AppConstants.PLATFORM);
		postMap.put("session_id", AppConstants.member.getSessionId());
		postMap.put("apply_id", applyId + "");
		String url = AppConstants.BaseUrl
				+ "/api/v1/member/delete_friend_apply";
		StringPostRequest request = new StringPostRequest(postMap, url,
				delListener, errorListener);
		queue.add(request);
	}

	@Override
	public void onClick(View v) {
		if (v == img_back) {
			SwipeLayoutManager.getInstance().closeCurrentLayout();
			SwipeLayoutManager.getInstance().clearCurrentLayout();
			popBack(null);
			return;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			SwipeLayoutManager.getInstance().closeCurrentLayout();
			SwipeLayoutManager.getInstance().clearCurrentLayout();
			popBack(null);
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (hidden) {
			SwipeLayoutManager.getInstance().closeCurrentLayout();
			SwipeLayoutManager.getInstance().clearCurrentLayout();
		}
	}

	// @Override
	// public void onRefresh(SwipyRefreshLayoutDirection direction) {
	// if (direction == SwipyRefreshLayoutDirection.TOP) {
	// rquestApplyData(downListener, 0);
	// } else if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
	// rquestApplyData(upListener, idx + 1);
	// }
	// }
}
