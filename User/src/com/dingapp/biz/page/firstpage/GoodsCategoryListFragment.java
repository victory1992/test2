package com.dingapp.biz.page.firstpage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import view.refresh.PullToRefreshBase;
import view.refresh.PullToRefreshBase.Mode;
import view.refresh.PullToRefreshListView;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.android.volley.Response.Listener;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.orm.BrandInfoBean;
import com.dingapp.biz.db.orm.ChildChildTypeInfoBean;
import com.dingapp.biz.db.orm.GoodsCategoryBean;
import com.dingapp.biz.db.orm.GoodsChildTypeBean;
import com.dingapp.biz.db.orm.GoodsListBean;
import com.dingapp.biz.db.orm.GoodsListBean.DataEntity;
import com.dingapp.biz.db.orm.GoodsListBean.DataEntity.PrdsEntity;
import com.dingapp.biz.db.orm.GoodsTypeBean;
import com.dingapp.biz.net.RequestDataUtil;
import com.dingapp.biz.page.adapters.BrandAdapter;
import com.dingapp.biz.page.adapters.GoodsListAdapter;
import com.dingapp.biz.page.adapters.GoodsListCategoryAdapter;
import com.dingapp.biz.page.adapters.SearchRecordAdapter;
import com.dingapp.biz.util.LogoutUtils;
import com.dingapp.biz.util.StopRefresh;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.TestJsonUtil;
import com.dingapp.core.util.UIUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class GoodsCategoryListFragment extends BaseFragment implements
		OnClickListener {
	/**
	 * 商品列表
	 */

	private List<PrdsEntity> dataList = new ArrayList<PrdsEntity>();
	/**
	 * 商品列表数据
	 */
	private List<PrdsEntity> prds = new ArrayList<GoodsListBean.DataEntity.PrdsEntity>();
	private LinearLayout goods_category;
	private LinearLayout goods_sort;
	private LinearLayout goods_brand;
	private TextView tv_brand;
	private ImageView img_brand;
	private TextView tv_category;
	private TextView tv_sort;
	private ImageView img_category;
	private ImageView img_sort;
	private PullToRefreshListView lv_goods_list;
	private ListView lv_left_category;
	private ExpandableListView lv_right_category;
	private LinearLayout ll_empty_category;
	private LinearLayout ll_sort;
	private LinearLayout ll_category;
	private LinearLayout ll_empty_brand;
	private LinearLayout ll_brand;
	private ListView lv_brand;
	private BrandAdapter adapter_brand;
	private MyExpandableAdapter adapter_right;
	private ViewHolderGroup holderGroup = null;
	// 判断点击了：category,sort,brand
	private String type_flag = "";
	// 是否是打开弹出框
	private boolean isShowWindow = true;
	private EditText et_goods_detail_input;
	private LinearLayout ll_empty_sort;
	/**
	 * 排序方式
	 */
	private String sort = "";
	private int category_id = -1;
	private int child_category_id = -1;
	private int child_child_category_id = -1;
	private int brand_id = -1;
	private String title = "";
	private String isHomeSearch = "false";
	/**
	 * 商品列表adapter
	 */
	private GoodsListAdapter goodsListAdapter;

	public static enum MODE {
		UP, DOWN
	}

	// 商品搜索记录和清除搜索记录
	private Listener<String> recordListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserSearchRecordData(response);
		}
	};
	private Listener<String> clearListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserClearSearchRecordData(response);
		}
	};
	private LinearLayout ll_parent_search;
	private ListView lv_search_record;
	private LinearLayout ll_search_tvs;
	private TextView tv_close, tv_clear_search;
	private FrameLayout fl_empty_data;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return View.inflate(getActivity(), R.layout.goods_list_pager, null);
	}

	private Listener<String> downListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			if (lv_goods_list != null) {
				lv_goods_list.onRefreshComplete();
			}
			parseListData(response, MODE.DOWN);
		}
	};

	private Listener<String> upListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			if (lv_goods_list != null) {
				lv_goods_list.onRefreshComplete();
			}
			parseListData(response, MODE.UP);
		}

	};
	private Listener<String> categoryListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parseCategoryData(response);
		}
	};
	private TextView tv_price_high;
	private TextView tv_price_low;
	private TextView tv_popularity;
	private TextView tv_sales_volume;
	/**
	 * 分页数量
	 */
	private int page_idx;

	protected void parseListData(String response, MODE mode) {
		try {
			Gson gson = new Gson();
			GoodsListBean fromJson = gson.fromJson(response,
					GoodsListBean.class);
			if (fromJson != null) {
				DataEntity data = fromJson.getData();
				if (data != null) {
					prds = data.getGoods_info();
				}
			}
			if (mode == MODE.DOWN) {
				page_idx = 0;
				dataList.clear();
			} else if (mode == MODE.UP) {
				if (prds != null && prds.size() > 0) {
					page_idx++;
				}
			}
			if (prds != null && prds.size() > 0) {
				dataList.addAll(prds);
			}
			setView();
			goodsListAdapter.setData(dataList);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
	}

	private void setView() {
		if (dataList != null && dataList.size() > 0) {
			fl_empty_data.setVisibility(View.GONE);
		} else {
			fl_empty_data.setVisibility(View.VISIBLE);
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
				initCategoryData(cateBean.getType_info());
				adapter_brand.setList(cateBean.getBrands_info());
			} else {
				UIUtil.showToast(getActivity(), statusMsg);
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments() != null) {
			Bundle bundle = getArguments();
			if (bundle.containsKey("category_id")) {
				category_id = bundle.getInt("category_id");
			} else if (bundle.containsKey("home_search")) {
				isHomeSearch = bundle.getString("home_search");
			}
			if (bundle.containsKey("child_category_id")) {
				child_category_id = bundle.getInt("child_category_id");
			}
			if (bundle.containsKey("child_child_category_id")) {
				child_child_category_id = bundle
						.getInt("child_child_category_id");
			}
		}
		initView();
		initListener();
		goodsListAdapter = new GoodsListAdapter(getActivity());
		lv_goods_list.setAdapter(goodsListAdapter);
		if (isHomeSearch.equals("true")) {
			// 从首页搜索跳过来，先判断是否登录，拉取搜索记录，edittext自动获取焦点，键盘弹出
			requestSearchRecord();
			et_goods_detail_input.setFocusable(true);
			et_goods_detail_input.requestFocus();
			InputMethodManager imm = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(et_goods_detail_input, 0);
			if (!TextUtils.isEmpty(AppConstants.member.getSessionId())) {
				requestSearchRecord();
			}
		} else {
			requestInitData(downListener, 0);
		}
		requestCategoryData();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private void requestCategoryData() {
		if (AppConstants.TEST_MODE) {
			String json = TestJsonUtil.getTestJson("goods_category");
			parseCategoryData(json);
			return;
		}
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("type", "normal");
		postMap.put("is_homepage", "false");
		postMap.put("need_brand", "true");
		String url = AppConstants.category_list;
		RequestDataUtil.getRequestInstance().requestData(categoryListener,
				postMap, url, getActivity(), null, "normal");
	}

	/**
	 * 1、标题搜索的时候 需要传递 title 其它字段可有可无 2、sort请求的时候 需要传递sort category_id 可传可不传
	 * 3、分页加载 page_indx 只有在上拉刷新 以及下拉刷新的时候 需要传递 index
	 * 
	 * @param sort
	 * @param category_id
	 * @param title
	 * @param page_idx
	 */
	private void requestInitData(Listener<String> listener, int page_idx) {
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("favorited", "false");
		if (category_id != -1 && category_id != 0) {
			postMap.put("type_id", category_id + "");
		}
		if (child_category_id != -1 && child_category_id != 0) {
			postMap.put("child_type_id", child_category_id + "");
		}
		if (child_child_category_id != -1 && child_child_category_id != 0) {
			postMap.put("child_child_type_id", child_child_category_id + "");
		}

		if (brand_id != -1 && brand_id != 0) {
			postMap.put("brand_id", brand_id + "");
		}
		if (sort != null && !TextUtils.isEmpty(sort)) {
			postMap.put("sort", sort);
		}
		if (title != null && !TextUtils.isEmpty(title)) {
			postMap.put("goods_title", title);
		}
		postMap.put("page_idx", page_idx + "");
		RequestDataUtil.getRequestInstance().requestData(listener, postMap,
				AppConstants.prd_list, getActivity(), null, "normal");
	}

	/**
	 * 填充分类数据
	 */
	private void initCategoryData(List<GoodsTypeBean> typeList) {
		// 没数据直接返回
		if (typeList == null || typeList.size() < 0) {
			return;
		}
		// 左边条目的数据
		final GoodsListCategoryAdapter goodsListCategoryAdapter = new GoodsListCategoryAdapter(
				getActivity(), typeList);
		adapter_right = new MyExpandableAdapter();
		GoodsTypeBean typeBean = (GoodsTypeBean) goodsListCategoryAdapter
				.getItem(0);
		goodsListCategoryAdapter.setSelectedPosition(0);
		goodsListCategoryAdapter.notifyDataSetChanged();
		// 左边条目改变，右边跟着变化。
		adapter_right.setList(typeBean.getChild_type_info());
		lv_right_category.setAdapter(adapter_right);
		lv_right_category.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				if (parent.isGroupExpanded(groupPosition)) {
					parent.collapseGroup(groupPosition);
				} else {
					parent.expandGroup(groupPosition);
				}
				return true;
			}
		});
		lv_right_category.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				for (int i = 0, count = lv_right_category
						.getExpandableListAdapter().getGroupCount(); i < count; i++) {
					if (groupPosition != i) {// 关闭其他分组
						lv_right_category.collapseGroup(i);
					}
				}
			}
		});
		lv_left_category.setAdapter(goodsListCategoryAdapter);
		lv_left_category.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				GoodsTypeBean typeBean = (GoodsTypeBean) goodsListCategoryAdapter
						.getItem((int) id);
				category_id = typeBean.getType_id();
				goodsListCategoryAdapter.setSelectedPosition(position);
				goodsListCategoryAdapter.notifyDataSetChanged();
				adapter_right = new MyExpandableAdapter();
				lv_right_category.setAdapter(adapter_right);
				// 左边条目改变，右边跟着变化。
				adapter_right.setList(typeBean.getChild_type_info());
			}
		});
		lv_right_category.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView arg0, View arg1,
					int arg2, long arg3) {
				GoodsChildTypeBean childBean = (GoodsChildTypeBean) adapter_right
						.getGroup(arg2);
				if (childBean.getChild_type_id() == -1) {
					child_category_id = childBean.getChild_type_id();
					if (goodsListCategoryAdapter.getSelectedPosition() == 0) {
						GoodsTypeBean typeBean = (GoodsTypeBean) goodsListCategoryAdapter
								.getItem(0);
						category_id = typeBean.getType_id();
					}
					// 请求网络 再次刷新数据
					requestInitData(downListener, 0);
					showDissmissAnimation(ll_category);
					showArrowStyle(img_category);
					tv_category.setEnabled(false);
				}
				return false;
			}
		});
		lv_right_category.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView arg0, View arg1,
					int arg2, int arg3, long arg4) {
				ChildChildTypeInfoBean childChildBean = (ChildChildTypeInfoBean) adapter_right
						.getChild(arg2, arg3);
				child_child_category_id = childChildBean
						.getChild_child_type_id();
				// 请求网络 再次刷新数据
				requestInitData(downListener, 0);
				showDissmissAnimation(ll_category);
				showArrowStyle(img_category);
				tv_category.setEnabled(false);
				return false;
			}
		});

	}

	private void initListener() {
		goods_sort.setOnClickListener(this);
		goods_category.setOnClickListener(this);
		ll_sort.setOnClickListener(this);
		ll_category.setOnClickListener(this);
		ll_empty_category.setOnClickListener(this);
		ll_empty_sort.setOnClickListener(this);
		tv_sales_volume.setOnClickListener(this);
		tv_price_high.setOnClickListener(this);
		tv_price_low.setOnClickListener(this);
		tv_popularity.setOnClickListener(this);
		goods_brand.setOnClickListener(this);
		ll_empty_brand.setOnClickListener(this);
		ll_brand.setOnClickListener(this);
		lv_brand.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				BrandInfoBean bean = (BrandInfoBean) adapter_brand
						.getItem(arg2);
				if (bean != null) {
					// 请求网络 再次刷新数据
					brand_id = bean.getBrand_id();
					requestInitData(downListener, 0);
					showDissmissAnimation(ll_brand);
					showArrowStyle(img_brand);
					tv_brand.setEnabled(false);
				}

			}
		});
		lv_search_record.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (searchAdapter != null) {
					title = (String) searchAdapter.getItem((int) arg3);
				}
				et_goods_detail_input.setText(title);
				initSearch();
				ll_parent_search.setVisibility(View.GONE);
				if (getActivity() != null) {
					UIUtil.hideKeyboard(getActivity());
				}
			}
		});
		et_goods_detail_input
				.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView arg0, int arg1,
							KeyEvent arg2) {
						if (arg1 == EditorInfo.IME_ACTION_SEARCH) {
							initSearch();
							ll_parent_search.setVisibility(View.GONE);
							if (getActivity() != null) {
								UIUtil.hideKeyboard(getActivity());
							}
						}
						return false;
					}
				});
		tv_close.setOnClickListener(this);
		tv_clear_search.setOnClickListener(this);
	}

	/**
	 * 搜索内容
	 */
	protected void initSearch() {
		title = et_goods_detail_input.getText().toString();
		if (title != null) {
			page_idx = 0;
			requestInitData(downListener, 0);
		} else {
			UIUtil.showToast(getActivity(), "请输入搜索内容！");
		}

	}

	private void initListViewData() {
		lv_goods_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				PrdsEntity item = (PrdsEntity) parent.getAdapter().getItem(
						position);
				int prd_id = item.getGoods_id();
				Bundle bundle = new Bundle();
				bundle.putInt("prd_id", prd_id);
				openPage("goods_detail_pager", bundle, true);
			}
		});

	}

	private void initView() {
		goods_category = (LinearLayout) getView().findViewById(
				R.id.ll_goods_category);
		goods_sort = (LinearLayout) getView().findViewById(R.id.ll_goods_sort);
		tv_category = (TextView) getView().findViewById(R.id.tv_text_category);
		tv_sort = (TextView) getView().findViewById(R.id.tv_text_sort);
		img_category = (ImageView) getView().findViewById(R.id.img_category);
		img_sort = (ImageView) getView().findViewById(R.id.img_sort);
		lv_goods_list = (PullToRefreshListView) getView().findViewById(
				R.id.lv_goods_list);
		lv_left_category = (ListView) getView().findViewById(
				R.id.lv_left_category);
		ll_sort = (LinearLayout) getView().findViewById(R.id.ll_sort);
		ll_category = (LinearLayout) getView().findViewById(R.id.ll_category);
		ll_empty_category = (LinearLayout) getView().findViewById(
				R.id.ll_empty_category);
		et_goods_detail_input = (EditText) getView().findViewById(
				R.id.et_goods_detail_input);
		ll_empty_sort = (LinearLayout) getView().findViewById(
				R.id.ll_empty_sort);
		tv_price_high = (TextView) getView().findViewById(R.id.tv_price_high);
		tv_price_low = (TextView) getView().findViewById(R.id.tv_price_low);
		tv_popularity = (TextView) getView().findViewById(R.id.tv_popularity);
		tv_sales_volume = (TextView) getView().findViewById(
				R.id.tv_sales_volume);
		getView().findViewById(R.id.img_back).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						popBack(null);
					}
				});
		initPullRefreshView();
		ll_parent_search = (LinearLayout) getView().findViewById(
				R.id.ll_search_goods);
		lv_search_record = (ListView) getView().findViewById(
				R.id.lv_goods_search_record);
		ll_search_tvs = (LinearLayout) getView().findViewById(
				R.id.ll_search_record_tvs);
		tv_close = (TextView) getView().findViewById(R.id.tv_close);
		tv_clear_search = (TextView) getView().findViewById(
				R.id.tv_clear_goods_record);
		img_brand = (ImageView) getView().findViewById(R.id.img_brand);
		tv_brand = (TextView) getView().findViewById(R.id.tv_text_brand);
		goods_brand = (LinearLayout) getView()
				.findViewById(R.id.ll_goods_brand);
		ll_brand = (LinearLayout) getView().findViewById(R.id.ll_brand);
		ll_empty_brand = (LinearLayout) getView().findViewById(
				R.id.ll_empty_brand);
		lv_brand = (ListView) getView().findViewById(R.id.lv_brand);
		adapter_brand = new BrandAdapter(getActivity());
		lv_brand.setAdapter(adapter_brand);
		lv_right_category = (ExpandableListView) getView().findViewById(
				R.id.lv_right_detail);
		fl_empty_data = (FrameLayout) getView()
				.findViewById(R.id.fl_empty_data);
		initListViewData();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initPullRefreshView() {
		StopRefresh.initRefreshView(lv_goods_list, Mode.BOTH);
		lv_goods_list
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase refreshView) {
						StopRefresh.stopRefreash(lv_goods_list);
						requestInitData(downListener, 0);
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase refreshView) {
						StopRefresh.stopRefreash(lv_goods_list);
						requestInitData(upListener, page_idx + 1);
					}

				});

	}

	@Override
	public void onClick(View v) {
		if (v == goods_category) {
			type_flag = "category";
			showCategoryView();
			return;
		} else if (v == goods_sort) {
			type_flag = "sort";
			showSortView();
			return;
		} else if (v == goods_brand) {
			type_flag = "brand";
			showBrandView();
			return;
		} else if (v == ll_empty_category) {
			showDissmissAnimation(ll_category);
			showArrowStyle(img_category);
			tv_category.setEnabled(false);
			return;
		} else if (v == ll_empty_brand) {
			showDissmissAnimation(ll_brand);
			showArrowStyle(img_brand);
			tv_brand.setEnabled(false);
			return;
		} else if (v == ll_empty_sort) {
			showDissmissAnimation(ll_sort);
			showArrowStyle(img_sort);
			tv_sort.setEnabled(false);
			return;
		} else if (v == tv_sales_volume) {
			// 每次点击分类或者排序还有搜索内容的时候，将page_id置为0
			page_idx = 0;
			dataList.clear();
			sort = "sale";
			requestInitData(downListener, 0);
			initSortData(tv_sales_volume);
			return;
		} else if (v == tv_price_high) {
			sort = "price_desc";
			page_idx = 0;
			dataList.clear();
			requestInitData(downListener, 0);
			initSortData(tv_price_high);
			return;
		} else if (v == tv_price_low) {
			sort = "price_asc";
			dataList.clear();
			page_idx = 0;
			requestInitData(downListener, 0);
			initSortData(tv_price_low);
			return;
		} else if (v == tv_popularity) {
			dataList.clear();
			sort = "view";
			page_idx = 0;
			requestInitData(downListener, 0);
			initSortData(tv_popularity);
			return;
		}
		if (v == tv_close) {
			ll_parent_search.setVisibility(View.GONE);
			return;
		}
		if (v == tv_clear_search) {
			ll_parent_search.setVisibility(View.GONE);
			requestClearSearchRecord();
			return;
		}
	}

	private void initSortData(TextView tv) {
		tv_price_high.setSelected(false);
		tv_price_low.setSelected(false);
		tv_sales_volume.setSelected(false);
		tv_popularity.setSelected(false);
		tv.setSelected(true);
		showDissmissAnimation(ll_sort);
		showArrowStyle(img_sort);
		tv_sort.setEnabled(false);
	}

	/**
	 * 箭头方向
	 * 
	 * @param img
	 */
	private void showArrowStyle(ImageView img) {

		if (img == img_category) {
			if (!isShowWindow) {
				img_category
						.setImageResource(R.drawable.detail_small_dowm_gray);
			} else {
				img_category.setImageResource(R.drawable.details_small);
			}
		} else if (img == img_sort) {
			if (!isShowWindow) {
				img_sort.setImageResource(R.drawable.detail_small_dowm_gray);
			} else {
				img_sort.setImageResource(R.drawable.details_small);
			}
		} else if (img == img_brand) {
			if (!isShowWindow) {
				img_brand.setImageResource(R.drawable.detail_small_dowm_gray);
			} else {
				img_brand.setImageResource(R.drawable.details_small);
			}
		}
	}

	private void showCategoryView() {
		if (!type_flag.equals("category")) {
			return;
		}
		showArrowStyle(img_category);
		if (isShowWindow) {
			tv_category.setEnabled(true);
			showAppearAnimation(ll_category, goods_category);
			ll_category.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					return true;
				}
			});
		} else {
			tv_category.setEnabled(false);
			showDissmissAnimation(ll_category);
		}
	}

	private void showSortView() {
		if (!type_flag.equals("sort")) {
			return;
		}
		showArrowStyle(img_sort);
		if (isShowWindow) {
			tv_sort.setEnabled(true);
			showAppearAnimation(ll_sort, goods_sort);
			ll_sort.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					return true;
				}
			});
		} else {
			tv_sort.setEnabled(false);
			showDissmissAnimation(ll_sort);
		}
	}

	private void showBrandView() {
		if (!type_flag.equals("brand")) {
			return;
		}
		showArrowStyle(img_brand);
		if (isShowWindow) {
			tv_brand.setEnabled(true);
			showAppearAnimation(ll_brand, goods_brand);
			ll_brand.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					return true;
				}
			});
		} else {
			tv_brand.setEnabled(false);
			showDissmissAnimation(ll_brand);
		}
	}

	/**
	 * 消失的动画
	 * 
	 * @param v
	 */
	private void showDissmissAnimation(final View v) {
		AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
		alphaAnimation.setDuration(500);
		v.startAnimation(alphaAnimation);
		alphaAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				goods_category.setClickable(false);
				goods_sort.setClickable(false);
				goods_brand.setClickable(false);
				ll_empty_category.setClickable(false);
				ll_empty_sort.setClickable(false);
				ll_empty_brand.setClickable(false);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				v.setVisibility(View.GONE);
				goods_category.setClickable(true);
				goods_sort.setClickable(true);
				goods_brand.setClickable(true);
				ll_empty_brand.setClickable(true);
				ll_empty_category.setClickable(true);
				ll_empty_sort.setClickable(true);
				isShowWindow = true;

			}
		});
	}

	/**
	 * @param v
	 *            出现的动画
	 */
	private void showAppearAnimation(final View v, final LinearLayout ll_choose) {
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
		alphaAnimation.setDuration(500);
		v.startAnimation(alphaAnimation);
		alphaAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				v.setVisibility(View.VISIBLE);
				goods_category.setClickable(false);
				goods_sort.setClickable(false);
				goods_brand.setClickable(false);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				v.setVisibility(View.VISIBLE);
				ll_choose.setClickable(true);
				isShowWindow = false;
			}
		});
	}

	private List<String> searchRecordList = new ArrayList<String>();
	private SearchRecordAdapter searchAdapter;

	// 如果登录了，可以请求搜索记录
	private void requestSearchRecord() {
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("type", "normal");
		RequestDataUtil.getRequestInstance().requestData(recordListener,
				postMap, AppConstants.goods_search_history, getActivity(),
				null, "true");
	}

	private void parserSearchRecordData(String response) {
		try {
			JSONObject jsbJson = new JSONObject(response);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			if (statusCode.equals("200")) {
				JSONArray array = jsbJson.getJSONArray("data");
				searchRecordList.clear();
				for (int i = 0; i < array.length(); i++) {
					JSONObject dataJosn = array.getJSONObject(i);
					if (dataJosn.has("value_name")) {
						searchRecordList.add(dataJosn.getString("value_name"));
					}
				}
				if (searchRecordList.size() > 0) {
					ll_parent_search.setVisibility(View.VISIBLE);
				}
				searchAdapter = new SearchRecordAdapter(getActivity(),
						searchRecordList);
				lv_search_record.setAdapter(searchAdapter);
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

	private void requestClearSearchRecord() {
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("type", "normal");
		RequestDataUtil.getRequestInstance().requestData(clearListener,
				postMap, AppConstants.goods_delete_search_history,
				getActivity(), null, "true");
	}

	private void parserClearSearchRecordData(String response) {
		try {
			JSONObject jsbJson = new JSONObject(response);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			if (statusCode.equals("200")) {
				JSONObject dataJson = jsbJson.getJSONObject("data");
				if (dataJson.has("suc")) {
					String suc = dataJson.getString("suc");
					if (suc.equals("true")) {
						searchRecordList.clear();
						if (searchAdapter != null) {
							searchAdapter.notifyDataSetChanged();
						}
						ll_parent_search.setVisibility(View.GONE);
					}
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

	class MyExpandableAdapter extends BaseExpandableListAdapter {
		private List<GoodsChildTypeBean> list_group = new ArrayList<GoodsChildTypeBean>();

		public void setList(List<GoodsChildTypeBean> list) {
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
			GoodsChildTypeBean ormAddr = list_group.get(groupPosition);
			return ormAddr.getChild_child_type_info() == null ? 0 : ormAddr
					.getChild_child_type_info().size();
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
			GoodsChildTypeBean bean = list_group.get(groupPosition);
			return bean.getChild_child_type_info().get(childPosition);
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
			GoodsChildTypeBean addr = (GoodsChildTypeBean) adapter_right
					.getGroup(groupPosition);
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(getActivity());
				convertView = inflater.inflate(
						R.layout.item_expandlistview_parent, null);
				holderGroup = new ViewHolderGroup();
				holderGroup.tv_city = (TextView) convertView
						.findViewById(R.id.tv_item_expand_parent);
				holderGroup.iv_arrow = (ImageView) convertView
						.findViewById(R.id.iv_item_expand_parent);
				convertView.setTag(holderGroup);
			} else {
				holderGroup = (ViewHolderGroup) convertView.getTag();
			}
			holderGroup.tv_city.setText(addr.getChild_type_name());
			if (isExpanded) {
				holderGroup.iv_arrow.setImageResource(R.drawable.details_small);
			} else {
				holderGroup.iv_arrow
						.setImageResource(R.drawable.detail_small_dowm_gray);
			}
			return convertView;
		}

		/*
		 * boolean isLastChild 表示当前绘制的item是否是当前组中的最后一个item
		 */
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ChildChildTypeInfoBean itemBean = (ChildChildTypeInfoBean) adapter_right
					.getChild(groupPosition, childPosition);
			ViewHolder holder = null;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(
						R.layout.goods_list_category_list_item, null);
				holder = new ViewHolder();
				holder.tv_goods_category = (TextView) convertView
						.findViewById(R.id.tv_goods_category);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (itemBean != null) {
				holder.tv_goods_category.setText(itemBean
						.getChild_child_type_name());
				holder.tv_goods_category.setTextColor(0xfff66e00);
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
		private TextView tv_goods_category;
	}

	static class ViewHolderGroup {
		TextView tv_city;
		ImageView iv_arrow;
	}

}
