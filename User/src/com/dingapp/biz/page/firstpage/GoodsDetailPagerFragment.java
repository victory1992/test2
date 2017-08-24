package com.dingapp.biz.page.firstpage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import view.refresh.PullToRefreshBase;
import view.refresh.PullToRefreshBase.Mode;
import view.refresh.PullToRefreshScrollView;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.Listener;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.orm.CartListBean;
import com.dingapp.biz.db.orm.CommentBean;
import com.dingapp.biz.db.orm.GoodsAttrsBean;
import com.dingapp.biz.db.orm.GoodsChildAttrsBean;
import com.dingapp.biz.db.orm.GoodsDetailBean;
import com.dingapp.biz.db.orm.ImageBigAndMinBean;
import com.dingapp.biz.db.orm.ModifyBean;
import com.dingapp.biz.db.orm.OrderPrdAttrsBean;
import com.dingapp.biz.net.RequestDataUtil;
import com.dingapp.biz.page.CenterMyOrderListFragment.MODE;
import com.dingapp.biz.page.adapters.CommentAdapter;
import com.dingapp.biz.page.adapters.GoodsPropertyListViewAdapter;
import com.dingapp.biz.page.adapters.GoodsPropertyListViewAdapter.OnPropertyListener;
import com.dingapp.biz.page.customview.MyListView;
import com.dingapp.biz.page.thirdpage.SureOrderFragment.SURE_PAYTYPE;
import com.dingapp.biz.pay.weixin.WxConstants;
import com.dingapp.biz.util.ImageUtils;
import com.dingapp.biz.util.LogoutUtils;
import com.dingapp.biz.util.StopRefresh;
import com.dingapp.biz.util.WebViewUtils;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.AndroidUtil;
import com.dingapp.core.util.Constants;
import com.dingapp.core.util.LoggerUtil;
import com.dingapp.core.util.TestJsonUtil;
import com.dingapp.core.util.UIUtil;
import com.dingapp.imageloader.core.ImageLoader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class GoodsDetailPagerFragment extends BaseFragment implements
		OnClickListener {
	/**
	 * loading框
	 */
	private RelativeLayout rl_progress_goodsdetail;
	/**
	 * 商品详情scrollView
	 */
	private PullToRefreshScrollView sv_goods_detail;
	/**
	 * 商品展示图viewpager
	 */
	private ViewPager vp_goods_detail_image;
	/**
	 * 小圆点group
	 */
	private LinearLayout ll_viewGroup;
	/**
	 * 商品描述
	 */
	private TextView goods_detail_des;
	/**
	 * 商品收藏按钮
	 */
	private ImageView tv_collect_goods;
	/**
	 * 商品价格
	 */
	private TextView tv_goods_price_total;
	/**
	 * 回退按钮
	 */
	private ImageView img_detail_back;
	/**
	 * 分享按钮
	 */
	private ImageView img_detail_share;
	/**
	 * 加入购物车按钮
	 */
	private TextView tv_add_cart;
	/**
	 * 立即购买按钮
	 */
	private TextView tv_buy_now;
	/**
	 * 商品属性listview
	 */
	private MyListView lv_goods_property;
	/**
	 * 赠送积分
	 */
	private TextView tv_goods_give_jifen;
	/**
	 * 分销
	 */
	private TextView tv_share;
	/**
	 * 普通商品价格
	 */
	private TextView tv_nomal_price;
	private TextView tv_nomal_price_txt;
	private ImageView iv_vip_tag;
	/**
	 * 收藏标记
	 */
	private boolean flag;
	private ImageView img_share_wx;
	private ImageView img_shar_friend;
	private TextView cancle_share;
	private Dialog dialog;
	private RelativeLayout rl_head_layout;
	private ImageView iv_share;
	/**
	 * 微信分享
	 */
	private IWXAPI wxApi;
	/**
	 * 评价和详请切换
	 */
	private ListView lv_comments;
	private TextView tv_change_wv;
	private TextView tv_change_lv;
	// 购物车数量
	private TextView tv_cart_count;
	private RelativeLayout rl_cart_counts;
	/**
	 * 商品数量
	 */
	private ImageView tv_add_num;
	private ImageView tv_reduce_num;
	private TextView tv_num;
	/**
	 * 服务器返回数据
	 */
	private String has_favorited;
	private int comment_cnt;
	private List<CommentBean> comments = new ArrayList<CommentBean>();
	private List<ImageBigAndMinBean> focus_pics;
	private String prd_details;
	private int prd_id;
	private double prd_price;
	private double vip_price = 0.0;
	private int send_score = 0;
	private String is_vip = "false";
	private String prd_title;
	private double goods_weight;
	private int goods_choose_num = 1;
	private int goods_avisible_num = 1;
	private List<GoodsAttrsBean> dynamic_props;
	/**
	 * 分享url，需要载url之后追加分享会员的会员ID。例如:http://xx.xx.xx?prd_id=xx&user_id=1
	 */
	private String share_url;
	private List<ImageView> imageViews;
	private int previousSelectedePosition;
	private ImageLoader imageLoader;
	private View layout;
	private HashMap<String, GoodsChildAttrsBean> properMap;
	// 评论有上拉加载更多
	private CommentAdapter commentAdapter;
	private int index = 0;
	private Listener<String> refreshListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserCommentData(response, MODE.DOWN);
		}
	};
	private Listener<String> moreListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserCommentData(response, MODE.UP);
		}
	};
	private Listener<String> listener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parseData(response);
		}
	};
	private boolean cart_falg = false;
	private Listener<String> cartListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			if (cart_falg) {
				return;
			}
			parseCartData(response);
		}
	};
	/**
	 * 加入购物车是否成功标记
	 */
	private String suc;
	private String suc_collect_cancle;
	private String suc_comfirm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imageLoader = ImageLoader.getInstance();
	}

	protected void parseCartData(String response) {
		cart_falg = true;
		Gson gson = new Gson();
		ModifyBean fromJson = gson.fromJson(response, ModifyBean.class);
		ModifyBean.DataEntity data = fromJson.getData();
		if (data != null) {
			suc = data.getSuc();
		}
		if ("true".equals(suc)) {
			View view = View.inflate(getActivity(), R.layout.my_toast, null);
			Toast toast = new Toast(getActivity());
			toast.setView(view);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.show();
			tv_cart_count.setText((Integer.parseInt(tv_cart_count.getText()
					.toString()) + 1) + "");
		} else {
			UIUtil.showToast(getActivity(), "加入购物车失败！");
		}
		cart_falg = false;
	}

	/**
	 * @param response
	 *            解析数据
	 */
	protected void parseData(String response) {
		try {
			JSONObject jsbJson = new JSONObject(response);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			rl_progress_goodsdetail.setVisibility(View.GONE);
			Gson gson = new Gson();
			if (statusCode.equals("200")) {
				GoodsDetailBean bean = gson.fromJson(jsbJson.getString("data"),
						GoodsDetailBean.class);
				if (bean != null) {
					has_favorited = bean.getIf_collect();
					comment_cnt = bean.getComment_cnt();
					dynamic_props = bean.getGoods_attrs();
					focus_pics = bean.getGoods_pics();
					prd_details = bean.getGoods_detail();
					prd_id = bean.getGoods_id();
					prd_price = bean.getGoods_price();
					goods_avisible_num = bean.getGoods_number();
					prd_title = bean.getGoods_name();
					goods_weight = bean.getGoods_weight();
					is_vip = bean.getIs_vip();
					vip_price = bean.getVip_price();
					send_score = bean.getSend_score();
					if (focus_pics != null && focus_pics.size() > 0
							&& focus_pics.get(0).getDetail_url() != null) {
						selected_pic = focus_pics.get(0);
					}
				}
				tv_goods_give_jifen.setText(bean.getSend_score() + "");
				if (bean.getIs_vip() != null && bean.getIs_vip().equals("true")) {
					iv_vip_tag.setVisibility(View.VISIBLE);
					tv_nomal_price.setVisibility(View.VISIBLE);
					tv_nomal_price.setText("￥" + prd_price);
					tv_nomal_price_txt.setVisibility(View.VISIBLE);
					tv_goods_price_total.setText("￥" + bean.getVip_price());
				} else {
					tv_goods_price_total.setText("￥" + prd_price);
				}
				tv_cart_count.setText(bean.getCart_cnt() + "");
				// 先将 vp全部清空
				vp_goods_detail_image.removeAllViews();
				ll_viewGroup.removeAllViews();
				share_url = bean.getShare_url();
				initViewPager();
				initCollectGoodsStatu();
				initGoodsProperty();
				initGoodsPropertyData();
				initWebViewData();
			} else if (TextUtils.equals(statusCode, "1001")) {
				UIUtil.showToast(getActivity(), "身份登录信息失效");
				LogoutUtils.logout(getActivity());
			} else {
				UIUtil.showToast(getActivity(), statusMsg);
				if (statusCode.equals("8888")) {
					popBack(null);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void requestData() {
		if (AppConstants.TEST_MODE) {
			String json = TestJsonUtil.getTestJson("goods_detail");
			parseData(json);
			return;
		}
		HashMap<String, String> postMap = new HashMap<String, String>();
		// session_id
		if (prd_id != 0) {
			postMap.put("goods_id", prd_id + "");
		}
		String url = AppConstants.prd_details;
		rl_progress_goodsdetail.setVisibility(View.VISIBLE);
		RequestDataUtil.getRequestInstance().requestData(listener, postMap,
				url, getActivity(), null, "normal");
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (layout == null) {
			layout = View.inflate(getActivity(), R.layout.goods_detail_pager,
					null);
		}
		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		wxApi = WXAPIFactory.createWXAPI(getActivity(), WxConstants.WX_APP_ID,
				true);
		wxApi.registerApp(WxConstants.WX_APP_ID);
		initView();
		Bundle bundle = getArguments();
		lv_goods_property.setFocusable(false);
		if (bundle != null && bundle.containsKey("prd_id")) {
			prd_id = bundle.getInt("prd_id");
		}
		initListener();
		commentAdapter = new CommentAdapter(getActivity(),
				GoodsDetailPagerFragment.this);
		lv_comments.setAdapter(commentAdapter);
		requestData();
		requestCommentList(refreshListener, 0);

	}

	/**
	 * 初始化轮播图 和 小圆点
	 */
	@SuppressWarnings("deprecation")
	private void initViewPager() {
		ImageView imageView;
		ImageView pointView;
		LinearLayout.LayoutParams layoutParams;
		imageViews = new ArrayList<ImageView>();
		if (focus_pics == null) {
			for (int i = 0; i < 5; i++) {
				if (getActivity() == null) {
					return;
				}
				imageView = new ImageView(getActivity());
				imageView.setBackgroundResource(R.drawable.icon);
				imageView.setScaleType(ScaleType.FIT_XY);
				imageViews.add(imageView);

				pointView = new ImageView(getActivity());
				pointView.setBackgroundResource(R.drawable.selector_pointview);
				layoutParams = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				if (i != 0) {
					layoutParams.leftMargin = 15;
					pointView.setEnabled(false);
				} else {
					pointView.setEnabled(true);
				}
				ll_viewGroup.addView(pointView, layoutParams);
			}
		} else {
			for (int i = 0; i < focus_pics.size(); i++) {
				if (getActivity() == null) {
					return;
				}
				imageView = new ImageView(getActivity());
				imageLoader.displayImage(focus_pics.get(i).getDetail_url(),
						imageView);
				// imageView.setScaleType(ScaleType.CENTER_CROP);
				imageViews.add(imageView);

				pointView = new ImageView(getActivity());
				pointView.setBackgroundResource(R.drawable.selector_pointview);
				layoutParams = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				if (i != 0) {
					layoutParams.leftMargin = 15;
					pointView.setEnabled(false);
				} else {
					pointView.setEnabled(true);
				}
				ll_viewGroup.addView(pointView, layoutParams);
			}
		}
		MyAdapter myAdapter = new MyAdapter();
		vp_goods_detail_image.setAdapter(myAdapter);
		previousSelectedePosition = 0;
		vp_goods_detail_image
				.setOnPageChangeListener(new OnPageChangeListener() {

					@Override
					public void onPageSelected(int arg0) {
						ll_viewGroup.getChildAt(previousSelectedePosition)
								.setEnabled(false);
						ll_viewGroup.getChildAt(arg0).setEnabled(true);
						previousSelectedePosition = arg0;
					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {

					}

					@Override
					public void onPageScrollStateChanged(int arg0) {

					}
				});

	}

	public class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView img = imageViews.get(position);
			container.addView(img);
			return img;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	/**
	 * 商品描述 以及价格
	 */
	private void initGoodsProperty() {
		if (prd_title != null) {
			goods_detail_des.setText(prd_title);
		}
	}

	/**
	 * 初始化收藏按钮
	 */
	private void initCollectGoodsStatu() {

		if (has_favorited != null && has_favorited.equals("true")) {
			tv_collect_goods.setImageResource(R.drawable.collect_yes);
		} else {
			tv_collect_goods.setImageResource(R.drawable.collect_no);
		}

	}

	/**
	 * 填充商品属性数据
	 */
	private void initGoodsPropertyData() {
		if (dynamic_props != null) {
			HashMap<String, GoodsChildAttrsBean> morenMap = new HashMap<String, GoodsChildAttrsBean>();
			for (int i = 0; i < dynamic_props.size(); i++) {
				GoodsAttrsBean dynamicPropsEntity = dynamic_props.get(i);
				if (dynamicPropsEntity.getGoods_attr() != null) {
					for (int j = 0; j < dynamicPropsEntity.getGoods_attr()
							.size(); j++) {
						GoodsChildAttrsBean childBean = dynamicPropsEntity
								.getGoods_attr().get(j);
						if (childBean.getIs_default() != null
								&& childBean.getIs_default().equals("true")) {
							morenMap.put(
									dynamicPropsEntity.getGoods_attr_name(),
									childBean);
						}
					}
				}
			}
			final GoodsPropertyListViewAdapter goodsPropertyListViewAdapter = new GoodsPropertyListViewAdapter(
					getActivity(), dynamic_props, morenMap);
			lv_goods_property.setAdapter(goodsPropertyListViewAdapter);
			// 获取map集合
			properMap = goodsPropertyListViewAdapter.getMap();
			/**
			 * 监听属性变化,当属性变化之后可以开始计算价格。
			 */
			goodsPropertyListViewAdapter
					.setOnPropertyListener(new OnPropertyListener() {

						@Override
						public void notifyPriceChange(int position) {
							properMap = goodsPropertyListViewAdapter
									.getHashMap();
						}
					});
		}
	}

	private WebView webview;
	private TextView tv_no_prd;
	private ImageBigAndMinBean selected_pic;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initListener() {
		tv_add_cart.setOnClickListener(this);
		tv_buy_now.setOnClickListener(this);
		img_detail_back.setOnClickListener(this);
		img_detail_share.setOnClickListener(this);
		tv_collect_goods.setOnClickListener(this);
		tv_change_lv.setOnClickListener(this);
		tv_change_wv.setOnClickListener(this);
		tv_add_num.setOnClickListener(this);
		tv_reduce_num.setOnClickListener(this);
		rl_cart_counts.setOnClickListener(this);
		tv_share.setOnClickListener(this);
		rl_progress_goodsdetail.setOnClickListener(this);
		sv_goods_detail
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase refreshView) {
						StopRefresh.stopRefreash(sv_goods_detail);
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase refreshView) {
						StopRefresh.stopRefreash(sv_goods_detail);
						requestCommentList(moreListener, index + 1);
					}
				});
	}

	private void initView() {
		rl_head_layout = (RelativeLayout) getView().findViewById(
				R.id.rl_head_layout);
		int width = Constants.getScreenWidth();
		android.view.ViewGroup.LayoutParams reLp = rl_head_layout
				.getLayoutParams();
		reLp.height = width;
		rl_head_layout.setLayoutParams(reLp);
		sv_goods_detail = (PullToRefreshScrollView) getView().findViewById(
				R.id.sv_goods_detail);
		vp_goods_detail_image = (ViewPager) getView().findViewById(
				R.id.vp_goods_detail_image);
		ll_viewGroup = (LinearLayout) getView().findViewById(R.id.ll_viewGroup);
		goods_detail_des = (TextView) getView().findViewById(
				R.id.goods_detail_des);
		tv_collect_goods = (ImageView) getView().findViewById(
				R.id.iv_collect_goods);
		tv_goods_price_total = (TextView) getView().findViewById(
				R.id.tv_goods_price_total);
		img_detail_back = (ImageView) getView().findViewById(
				R.id.img_detail_back);
		img_detail_share = (ImageView) getView().findViewById(
				R.id.img_detail_share);
		tv_add_cart = (TextView) getView().findViewById(R.id.tv_add_cart);
		tv_buy_now = (TextView) getView().findViewById(R.id.tv_buy_now);
		lv_goods_property = (MyListView) getView().findViewById(
				R.id.lv_goods_property);
		webview = (WebView) getView().findViewById(R.id.webview);
		tv_no_prd = (TextView) getView().findViewById(R.id.tv_no_prd);
		tv_change_lv = (TextView) getView().findViewById(
				R.id.tv_goodsdetail_comment);
		tv_change_wv = (TextView) getView().findViewById(
				R.id.tv_goodsdetail_web);
		lv_comments = (ListView) getView().findViewById(
				R.id.lv_goodsdetail_comment);
		rl_progress_goodsdetail = (RelativeLayout) getView().findViewById(
				R.id.rl_progress_goodsdetail);
		tv_goods_give_jifen = (TextView) getView().findViewById(
				R.id.tv_goods_give_jifen);
		iv_vip_tag = (ImageView) getView().findViewById(R.id.img_pic_vip_tag);
		tv_nomal_price = (TextView) getView().findViewById(R.id.tv_nomal_price);
		tv_nomal_price_txt = (TextView) getView().findViewById(
				R.id.tv_nomal_price_txt);
		tv_nomal_price.setPaintFlags(TextPaint.STRIKE_THRU_TEXT_FLAG);
		lv_comments.setFocusable(false);
		tv_no_prd.setVisibility(View.GONE);
		tv_share = (TextView) getView().findViewById(R.id.tv_goodsdetail_sale);
		WebViewUtils.initWebView(webview);
		tv_reduce_num = (ImageView) getView().findViewById(R.id.iv_reduce);
		tv_add_num = (ImageView) getView().findViewById(R.id.iv_add);
		tv_num = (TextView) getView().findViewById(R.id.tv_num_goods);
		tv_cart_count = (TextView) getView().findViewById(R.id.tv_cart_count);
		rl_cart_counts = (RelativeLayout) getView().findViewById(
				R.id.rl_cart_counts);
		StopRefresh.initRefreshView(sv_goods_detail, Mode.DISABLED);
		iv_share = new ImageView(getActivity());
		iv_share.setImageResource(R.drawable.share_logo);
	}

	private void initWebViewData() {
		if (prd_details == null) {
			return;
		}
		String template = AndroidUtil.getAssetsContents("article_info2.html");
		LoggerUtil.d("webview", template);
		String detail = template.replaceAll("%s", prd_details);
		LoggerUtil.d("webview", detail);
		webview.loadDataWithBaseURL("about:blank", detail, "text/html",
				"UTF-8", null);

	}

	@Override
	public void onClick(View v) {
		if (v == tv_add_cart) {
			if (AppConstants.member.getSessionId() == null
					|| TextUtils.isEmpty(AppConstants.member.getSessionId())) {
				openPage("login_page", null, false);
				return;
			}
			addCart();
			return;
		} else if (v == tv_buy_now) {
			if (AppConstants.member.getSessionId() == null
					|| TextUtils.isEmpty(AppConstants.member.getSessionId())) {
				openPage("login_page", null, false);
				return;
			}
			buyNow();
			return;
		} else if (v == img_detail_back) {
			popStack(null);
			return;
		} else if (v == img_detail_share) {
			share();
			return;
		} else if (v == tv_collect_goods) {
			if (AppConstants.member.getSessionId() == null
					|| TextUtils.isEmpty(AppConstants.member.getSessionId())) {
				openPage("login_page", null, false);
				return;
			}
			collectGoods();
			return;
		} else if (v == img_share_wx) {
			dialog.dismiss();
			share(1);
			return;
		} else if (v == img_shar_friend) {
			dialog.dismiss();
			share(2);
			return;
		} else if (v == cancle_share) {
			dialog.dismiss();
			return;
		}
		if (v == tv_change_lv) {
			refreshChangeView(tv_change_lv);
			lv_comments.setVisibility(View.VISIBLE);
			webview.setVisibility(View.GONE);
			StopRefresh.initRefreshView(sv_goods_detail, Mode.PULL_FROM_END);
			return;
		}
		if (v == tv_change_wv) {
			refreshChangeView(tv_change_wv);
			lv_comments.setVisibility(View.GONE);
			webview.setVisibility(View.VISIBLE);
			StopRefresh.initRefreshView(sv_goods_detail, Mode.DISABLED);
			return;
		}
		if (v == tv_add_num) {
			if (goods_choose_num >= goods_avisible_num - 1) {
				UIUtil.showToast(getActivity(), "商品库存不足");
				return;
			}
			goods_choose_num = goods_choose_num + 1;
			tv_num.setText(goods_choose_num + "");
			return;
		}
		if (v == tv_reduce_num) {
			if (goods_choose_num <= 1) {
				UIUtil.showToast(getActivity(), "商品不能为0");
				return;
			}
			goods_choose_num = goods_choose_num - 1;
			tv_num.setText(goods_choose_num + "");
			return;
		}
		if (v == rl_cart_counts) {
			if (AppConstants.member.getSessionId() == null
					|| TextUtils.isEmpty(AppConstants.member.getSessionId())) {
				openPage("login_page", null, false);
				return;
			}
			Bundle bundle = new Bundle();
			bundle.putBoolean("isFrom", false);
			openPage("goods_carts", null, false);
			return;
		}
		if (v == tv_share) {
			if (AppConstants.member.getSessionId() == null
					|| TextUtils.isEmpty(AppConstants.member.getSessionId())) {
				openPage("login_page", null, false);
				return;
			}
			share();
			return;
		}
	}

	private void refreshChangeView(TextView tv) {
		tv_change_lv.setBackgroundColor(0xffcccccc);
		tv_change_wv.setBackgroundColor(0xffcccccc);
		tv.setBackgroundColor(0xffffffff);
	}

	private void parseCollectCancleStatu(String response) {
		try {
			JSONObject jsbJson = new JSONObject(response);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			if (statusCode.equals("200")) {
				has_favorited = "false";
				UIUtil.showToast(getActivity(), "取消收藏成功！");
				initCollectGoodsStatu();
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

	/**
	 * 收藏商品
	 */
	private void collectGoods() {
		// 判断是否登录，未登录则先登录 之后 再跳转回来

		if ("true".equals(has_favorited)) {
			String url = AppConstants.cancel_favorite_prd;
			HashMap<String, String> postMap = new HashMap<String, String>();
			postMap.put("goods_id", prd_id + "");
			Listener<String> collect_cancle_Listener = new Listener<String>() {

				@Override
				public void onResponse(String response) {
					parseCollectCancleStatu(response);
				}

			};
			//
			String sessionId = AppConstants.member.getSessionId();
			if (sessionId != null && !sessionId.equals("")) {
				RequestDataUtil.getRequestInstance().requestData(
						collect_cancle_Listener, postMap, url, getActivity(),
						null, "true");
			} else {
				openPage("login_page", null, false);
			}
		} else if ("false".equals(has_favorited)) {
			Listener<String> collect_comfirm = new Listener<String>() {

				@Override
				public void onResponse(String response) {
					parseCollectComfirmStatu(response);
				}
			};
			HashMap<String, String> postMap = new HashMap<String, String>();
			postMap.put("goods_id", prd_id + "");
			String url = AppConstants.favorite_prd;
			boolean isLogin = RequestDataUtil.getRequestInstance().requestData(
					collect_comfirm, postMap, url, getActivity(), null, "true");
			if (isLogin) {
				openPage("login_page", null, false);
			}
		} else {
			UIUtil.showToast(getActivity(), "网络错误！");
		}
	}

	protected void parseCollectComfirmStatu(String response) {
		try {
			JSONObject jsbJson = new JSONObject(response);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			if (statusCode.equals("200")) {
				UIUtil.showToast(getActivity(), "收藏成功！");
				has_favorited = "true";
				initCollectGoodsStatu();
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

	/**
	 * 分享页面
	 */
	@SuppressWarnings("deprecation")
	private void share() {
		if (dialog == null) {
			dialog = new Dialog(getActivity(), R.style.dialog_style);
			View view = View
					.inflate(getActivity(), R.layout.share_dialog, null);
			img_share_wx = (ImageView) view.findViewById(R.id.img_share_wx);
			img_shar_friend = (ImageView) view
					.findViewById(R.id.img_share_wx_friend);
			cancle_share = (TextView) view.findViewById(R.id.cancle_share);
			img_share_wx.setOnClickListener(this);
			img_shar_friend.setOnClickListener(this);
			cancle_share.setOnClickListener(this);
			dialog.setContentView(view);
			Window window = dialog.getWindow();
			window.setBackgroundDrawable(new BitmapDrawable());
			window.setGravity(Gravity.BOTTOM);
			LayoutParams params = window.getAttributes();
			params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
			params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
		}
		dialog.show();
	}

	/**
	 * 微信分享
	 * 
	 * @param type
	 *            1为分享到好友，2为分享到朋友圈
	 */
	private void share(final int type) {
		boolean isInstalled = wxApi.isWXAppInstalled();
		if (!isInstalled) {
			UIUtil.showToast(getActivity(), "未安装微信");
			return;
		}
		try {
			new Thread() {
				@Override
				public void run() {
					super.run();
					WXWebpageObject webpage = new WXWebpageObject();
					if (share_url == null || TextUtils.isEmpty(share_url)) {
						return;
					}
					webpage.webpageUrl = share_url;
					WXMediaMessage msg = new WXMediaMessage(webpage);
					msg.title = "汉固达";
					msg.description = prd_title;
					BitmapDrawable d = (BitmapDrawable) iv_share.getDrawable();
					Bitmap bmp = d.getBitmap();
					msg.thumbData = ImageUtils.getCompressBitmap(bmp,
							CompressFormat.JPEG); // 设置图片数据;
					SendMessageToWX.Req req = new SendMessageToWX.Req();
					req.transaction = String
							.valueOf(System.currentTimeMillis());
					req.message = msg;
					if (type == 1) {
						req.scene = SendMessageToWX.Req.WXSceneSession;
					} else {
						req.scene = SendMessageToWX.Req.WXSceneTimeline;
					}
					wxApi.sendReq(req);
				}
			}.start();
		} catch (Exception e) {
		}

	}

	/**
	 * 立即购买，跳转到填写订单页面，传递参数
	 */
	private void buyNow() {
		// 判断是否登录，未登录先跳转到登录页面。
		if (dynamic_props == null) {
			UIUtil.showToast(getActivity(), "没有解析数据成功！");
			return;
		}
		ArrayList<CartListBean> listBean = new ArrayList<CartListBean>();
		CartListBean bean = new CartListBean();
		bean.setCart_id(0);
		bean.setIs_select("true");
		bean.setCnt(Integer.parseInt(tv_num.getText().toString()));
		List<OrderPrdAttrsBean> goods_attrs = new ArrayList<OrderPrdAttrsBean>();
		for (String key : properMap.keySet()) {
			OrderPrdAttrsBean attrBean = new OrderPrdAttrsBean();
			GoodsChildAttrsBean detailBean = properMap.get(key);
			attrBean.setAttr_name(key);
			attrBean.setAttr_value(detailBean.getGoods_attr_value());
			attrBean.setAttr_id(detailBean.getGoods_attr_value_id());
			goods_attrs.add(attrBean);
		}
		bean.setGoods_attrs(goods_attrs);
		bean.setGoods_id(prd_id);
		bean.setGoods_img(selected_pic);
		bean.setGoods_price(prd_price);
		bean.setGoods_title(prd_title);
		bean.setGoods_weight(goods_weight);
		bean.setVip_price(vip_price);
		bean.setIs_vip(is_vip);
		bean.setSend_score(send_score);
		listBean.add(bean);
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList("cart_list", listBean);
		bundle.putDouble("price", prd_price);
		bundle.putString("sure_paytype", SURE_PAYTYPE.NORMAL.name());
		openPage("sure_order", bundle, true);
	}

	/**
	 * 加入购物车逻辑
	 */
	private void addCart() {
		requestAddCart();
	}

	private void requestAddCart() {
		if (dynamic_props == null) {
			return;
		}
		if (properMap.size() != dynamic_props.size()) {
			UIUtil.showToast(getActivity(), "请选择商品属性");
			return;
		}
		String url = AppConstants.add_cart;
		HashMap<String, String> postMap = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		for (String key : properMap.keySet()) {
			GoodsChildAttrsBean bean = properMap.get(key);
			sb.append(bean.getGoods_attr_value_id());
			sb.append(",");
		}
		postMap.put("attrs_ids", sb.toString());
		postMap.put("goods_id", prd_id + "");
		postMap.put("goods_cnt", tv_num.getText().toString() + "");
		boolean isLogin = RequestDataUtil.getRequestInstance().requestData(
				cartListener, postMap, url, getActivity(), null, "true");
		if (isLogin) {
			openPage("login_page", null, false);
		}
	}

	// 请求评论列表
	private void requestCommentList(Listener<String> listener, int index) {
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("page_idx", index + "");
		postMap.put("goods_id", prd_id + "");
		RequestDataUtil.getRequestInstance().requestData(listener, postMap,
				AppConstants.evaluate_list, getActivity(), null, "normal");
	}

	private boolean refresh_flag = false;

	private void parserCommentData(String response, MODE mode) {
		try {
			JSONObject jsbJson = new JSONObject(response);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			Gson gson = new Gson();
			if (statusCode.equals("200")) {
				List<CommentBean> taskList = gson.fromJson(
						jsbJson.getString("data"),
						new TypeToken<List<CommentBean>>() {
						}.getType());
				if (mode == MODE.DOWN) {
					if (refresh_flag) {
						if (taskList != null && taskList.size() > 0) {
							UIUtil.showToast(getActivity(), "页面刷新完成");
						} else {
							UIUtil.showToast(getActivity(), "页面刷新完成，暂无数据");
						}
					}
					refresh_flag = true;
					index = 0;
					comments.clear();
				} else if (mode == MODE.UP) {
					if (taskList != null && taskList.size() > 0) {
						index++;
					} else {
						UIUtil.showToast(getActivity(), "暂无更多数据");
					}
				}
				if (taskList != null) {
					comments.addAll(taskList);
				}
				commentAdapter.setData(comments);
			} else {
				UIUtil.showToast(getActivity(), statusMsg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
