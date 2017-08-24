package com.dingapp.biz.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dingapp.andriod.z20.R;
import com.dingapp.biz.hx.EaseChatFragment;
import com.dingapp.biz.hx.EaseChatFragment.OnClickLeftListener;
import com.dingapp.biz.util.Utils;
import com.dingapp.core.app.BaseFragment;
import com.hyphenate.easeui.EaseConstant;

public class WeChatChatingFragment extends BaseFragment {
	private int chatType;
	private String user_id;
	private EaseChatFragment easeChatFragment;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		easeChatFragment = new EaseChatFragment();
	}
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return View.inflate(getActivity(), R.layout.wechat_chat, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments() != null) {
			chatType = getArguments().getInt(EaseConstant.EXTRA_CHAT_TYPE,
					EaseConstant.CHATTYPE_SINGLE);
			user_id = getArguments().getString(EaseConstant.EXTRA_USER_ID);
		}
		getView().postDelayed(new Runnable() {

			@Override
			public void run() {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						initData();
					}
				});
			}
		}, 300);
	}

	private void initData() {
		Bundle bundle = new Bundle();
		bundle.putString(EaseConstant.EXTRA_USER_ID, user_id);
		bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, chatType);
		easeChatFragment.setArguments(bundle);
		getActivity().getSupportFragmentManager().beginTransaction()
				.add(R.id.fl_wechat_chat, easeChatFragment).commit();
		easeChatFragment.setOnClickLeftListener(new OnClickLeftListener() {

			@Override
			public void click() {
				if (!Utils.isClickable()) {
					return;
				}
				popBack(null);
			}

			@Override
			public void rightClick() {
				if (!Utils.isClickable()) {
					return;
				}
				Bundle bundle = new Bundle();
				if (user_id.equals("admin")) {
					bundle.putString("admin", "admin");
				} else {
					bundle.putString("member_id",
							user_id.substring(11, user_id.length()));
				}
				bundle.putString("origin", "chatting");
				openPage("personal_info", bundle, false);
			}
		});
	}

	@Override
	public void onHiddenChanged(final boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			getView().postDelayed(new Runnable() {

				@Override
				public void run() {
					easeChatFragment.onHiddenChanged(hidden);
				}
			}, 300);

		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		easeChatFragment.onDestroy();
	}
	@Override
	public void onResume() {
		super.onResume();
		easeChatFragment.onResume();
	}
	@Override
	public void onStop() {
		super.onStop();
		easeChatFragment.onStop();
	}
	
}
