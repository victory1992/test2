package com.dingapp.biz.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONException;
import org.json.JSONObject;

import view.refresh.PullToRefreshBase;
import view.refresh.PullToRefreshScrollView;
import view.refresh.PullToRefreshBase.Mode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.orm.FullScreenAdvImgBean;
import com.dingapp.biz.db.orm.GoodsBean;
import com.dingapp.biz.db.orm.GoodsFocuPicsBean;
import com.dingapp.biz.db.orm.HomeBean;
import com.dingapp.biz.db.orm.TypeInfoBean;
import com.dingapp.biz.net.ChechWifyState;
import com.dingapp.biz.net.RequestDataUtil;
import com.dingapp.biz.net.SingleRequestQueue;
import com.dingapp.biz.page.adapters.FirstPagerRecommendListViewAdapter;
import com.dingapp.biz.page.adapters.HomeListAdapter;
import com.dingapp.biz.page.adapters.HomeTypeInfoAdapter;
import com.dingapp.biz.pay.LogUtils;
import com.dingapp.biz.util.LogoutUtils;
import com.dingapp.biz.util.StopRefresh;
import com.dingapp.biz.util.UpdateAppUtil;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.db.dao.MemberDao;
import com.dingapp.core.db.orm.Member;
import com.dingapp.core.util.AndroidUtil;
import com.dingapp.core.util.Constants;
import com.dingapp.core.util.TestJsonUtil;
import com.dingapp.core.util.UIUtil;
import com.dingapp.imageloader.core.ImageLoader;
import com.google.gson.Gson;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

/**
 * 汉固达首页
 * 
 * @author king
 * 
 */
public class HomePageFragment extends BaseFragment implements OnClickListener {
	private ListView lv_home;
	private ListView lv_goods;
	private GridView gv_type;
	private ViewPager vp_home;
	private ViewGroup vg_dots;
	private TextView tv_nowify;
	private ImageView iv_right_news;
	private TextView tv_search;
	private AtomicInteger what = new AtomicInteger(0);
	private ImageView[] imageViews;
	private ImageView imageView;
	private boolean isContinue = true;
	private List<View> images;
	private RelativeLayout rl_header_layout;
	private HomeViewPager adapter;
	private FirstPagerRecommendListViewAdapter goodsAdpater;
	private ChechWifyState wifystteBroadcast;
	private RelativeLayout rl_header_vg;
	// 缓存首页数据
	private Editor edit;
	private HomeListAdapter listAdapter;
	private HomeTypeInfoAdapter typeAdpater;
	private ImageView img_right_callphone;
	private PullToRefreshScrollView pullscrool_home;
	private Listener<String> msgListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserMsgData(response);
		}
	};
	private EMMessageListener messageListener;
	private int systemMsgCnt = 0;
	private Listener<String> refreshListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			LogUtils.i(this.getClass().getSimpleName(), response);
			if (edit != null) {
				edit.putString("idx_data", response);
				edit.commit();
			}
			parserData(response);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		int layoutId = R.layout.home_page;
		return inflater.inflate(layoutId, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Member member = new MemberDao().get();
		if (member == null || TextUtils.isEmpty(member.getSessionId())) {
			member = new Member();
			member.setSessionId("");
		}
		AppConstants.member = member;
		initView(getView());
		wifystteBroadcast = new ChechWifyState(tv_nowify);
		registerNetworkReceiver();
		UpdateAppUtil updateUtil = new UpdateAppUtil(getActivity(),
				SingleRequestQueue.getreRequestQueue(getActivity()
						.getApplicationContext()));
		updateUtil.requestData(getActivity(), false);
		initListener();
		listAdapter = new HomeListAdapter(getActivity());
		typeAdpater = new HomeTypeInfoAdapter(getActivity());
		goodsAdpater = new FirstPagerRecommendListViewAdapter(getActivity(),
				HomePageFragment.this, "home");
		lv_goods.setAdapter(goodsAdpater);
		lv_home.setAdapter(listAdapter);
		gv_type.setAdapter(typeAdpater);
		lv_home.setFocusableInTouchMode(false);
		lv_home.setFocusable(false);
		lv_goods.setFocusable(false);
		edit = getActivity().getSharedPreferences("home_idx_data",
				Context.MODE_PRIVATE).edit();
		requestData();
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

	private void initView(View view) {
		int width = Constants.getScreenWidth();
		rl_header_layout = (RelativeLayout) getView().findViewById(
				R.id.rl_header_layout);
		LayoutParams reLp = rl_header_layout.getLayoutParams();
		reLp.height = width * 2 / 3;
		rl_header_layout.setLayoutParams(reLp);
		vp_home = (ViewPager) view.findViewById(R.id.vp_home);
		vg_dots = (ViewGroup) view.findViewById(R.id.viewGroup);
		tv_nowify = (TextView) view.findViewById(R.id.tv_home_nowify);
		rl_header_vg = (RelativeLayout) view
				.findViewById(R.id.rl_header_layout);
		lv_home = (ListView) view.findViewById(R.id.lv_home);
		gv_type = (GridView) view.findViewById(R.id.gv_typeinfo);
		img_right_callphone = (ImageView) getView().findViewById(
				R.id.img_home_right_callphone);
		lv_goods = (ListView) getView().findViewById(R.id.lv_home_goods);
		gv_type.setFocusable(false);
		lv_home.setFocusable(false);
		lv_goods.setFocusable(false);
		tv_search = (TextView) getView().findViewById(R.id.et_home_search);
		iv_right_news = (ImageView) getView().findViewById(
				R.id.img_home_right_news);
		pullscrool_home = (PullToRefreshScrollView) getView().findViewById(
				R.id.pullscrool_home);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initListener() {
		tv_search.setOnClickListener(this);
		iv_right_news.setOnClickListener(this);
		img_right_callphone.setOnClickListener(this);
		StopRefresh.initRefreshView(pullscrool_home, Mode.PULL_FROM_START);
		gv_type.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Bundle bundle = new Bundle();
				TypeInfoBean bean = (TypeInfoBean) typeAdpater
						.getItem((int) id);
				bundle.putInt("category_id", bean.getType_id());
				bundle.putString("type_title", bean.getType_name());
				openPage("goods_category", bundle, false);
			}
		});
		pullscrool_home
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase refreshView) {
						StopRefresh.stopRefreash(pullscrool_home);
						requestData();
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase refreshView) {
						StopRefresh.stopRefreash(pullscrool_home);
					}
				});
		lv_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Bundle bundle = new Bundle();
				FullScreenAdvImgBean bean = (FullScreenAdvImgBean) listAdapter
						.getItem((int) id);
				bundle.putInt("prd_id", bean.getGoods_id());
				openPage("goods_detail_pager", bundle, true);
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v == img_right_callphone) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("联系客服: ");
			builder.setMessage("确定要拨打电话:" + "4006885286" + "吗？");
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							Intent intent = new Intent(Intent.ACTION_DIAL, Uri
									.parse("tel:4006885286"));
							startActivity(intent);

						}
					});
			builder.create().show();
			return;
		}
		if (v == tv_search) {
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

	// 初始化广告焦点图
	public void initViewPager(final List<GoodsFocuPicsBean> list) {
		if (list == null || list.size() <= 0) {
			return;
		}
		vg_dots.removeAllViews();
		images = new ArrayList<View>();
		for (int i = 0; i < list.size(); i++) {
			GoodsFocuPicsBean bean = list.get(i);
			if (bean == null) {
				return;
			}
			vg_dots.setVisibility(View.VISIBLE);
			rl_header_vg.setVisibility(View.VISIBLE);
			final ImageView iv = new ImageView(getActivity());
			String url = bean.getFocus_img().getDetail_url();
			ImageLoader.getInstance().displayImage(url, iv);
			iv.setTag(i);
			iv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Integer pos = (Integer) v.getTag();
					GoodsFocuPicsBean bean = list.get(pos);
					Bundle bundle = new Bundle();
					bundle.putInt("prd_id", bean.getGoods_id());
					openPage("goods_detail_pager", bundle, true);
				}
			});
			images.add(iv);
		}
		// 小圆点
		imageViews = new ImageView[images.size()];
		for (int j = 0; j < images.size(); j++) {
			imageView = new ImageView(getActivity());
			imageView.setLayoutParams(new LayoutParams(25, 25));
			imageView.setPadding(5, 5, 5, 5);
			imageViews[j] = imageView;
			if (j == 0) {
				imageViews[j].setImageResource(R.drawable.viewpager_point2);
			} else {
				imageViews[j].setImageResource(R.drawable.viewpager_point1);
			}
			vg_dots.addView(imageViews[j]);
		}

		adapter = new HomeViewPager(images);
		vp_home.setAdapter(adapter);

		vp_home.setOnPageChangeListener(new GuidePageChangeListener(imageViews));

		vp_home.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
					isContinue = false;
					break;
				case MotionEvent.ACTION_UP:
					isContinue = true;
					break;
				default:
					isContinue = true;
					break;
				}
				return false;
			}
		});

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					if (isContinue) {
						viewHandler.sendEmptyMessage(what.get());
						whatOption(imageViews);
					}
				}
			}

		}).start();
	}

	private void whatOption(ImageView[] imgViews) {
		what.incrementAndGet();
		if (what.get() > imgViews.length - 1) {
			what.getAndAdd(-5);
		}
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {

		}
	}

	private final Handler viewHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			vp_home.setCurrentItem(msg.what);
			super.handleMessage(msg);
		}

	};

	private final class GuidePageChangeListener implements OnPageChangeListener {
		private ImageView[] imgViews;

		public GuidePageChangeListener(ImageView[] imgViews) {
			this.imgViews = imgViews;
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			what.getAndSet(arg0);
			for (int i = 0; i < imgViews.length; i++) {
				imgViews[arg0].setImageResource(R.drawable.viewpager_point2);
				if (arg0 != i) {
					imgViews[i].setImageResource(R.drawable.viewpager_point1);
				}
			}
		}
	}

	class HomeViewPager extends PagerAdapter {
		private List<View> views = null;

		public HomeViewPager(List<View> views) {
			this.views = views;
		};

		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			container.addView(views.get(position));
			return views.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(views.get(position));
		}
	}

	private void registerNetworkReceiver() {
		IntentFilter filter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		getActivity().registerReceiver(wifystteBroadcast, filter);
	}

	private void unRegisterNetworkReceiver() {
		getActivity().unregisterReceiver(wifystteBroadcast);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unRegisterNetworkReceiver();
	}

	private void requestData() {
		if (AppConstants.TEST_MODE) {
			String json = TestJsonUtil.getTestJson("goods_idx");
			parserData(json);
			return;
		}
		if (!AndroidUtil.isNetworkAvailable(getActivity())) {
			String json = getActivity().getSharedPreferences("home_idx_data",
					Context.MODE_PRIVATE).getString("idx_data", "");
			if (!TextUtils.isEmpty(json)) {
				parserData(json);
			}
			// Ui显示
			tv_nowify.setVisibility(View.VISIBLE);
			return;
		}
		HashMap<String, String> postMap = new HashMap<String, String>();
		RequestDataUtil.getRequestInstance().requestData(refreshListener,
				postMap, AppConstants.goods_idx, getActivity(), null, "normal");
	}

	private void parserData(String json) {
		try {
			JSONObject jsbJson = new JSONObject(json);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			Gson gson = new Gson();
			if (statusCode.equals("200")) {
				String dataJson = jsbJson.getString("data");
				HomeBean bean = gson.fromJson(dataJson, HomeBean.class);
				initData(bean);
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

	private void initData(HomeBean bean) {
		if (bean == null) {
			return;
		}
		List<GoodsFocuPicsBean> list = bean.getFocu_pics();
		// 广告图
		if (bean.getExtend_adv_info() != null
				&& bean.getExtend_adv_info().size() > 0) {
			listAdapter.setList(bean.getExtend_adv_info());
		}
		List<GoodsBean> listBean = bean.getGoods();
		if (listBean != null && listBean.size() > 0) {
			goodsAdpater.setData(listBean);
		}
		if (bean.getType_info() != null && bean.getType_info().size() > 0) {
			typeAdpater.setData(bean.getType_info());
		}
		initViewPager(list);
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
