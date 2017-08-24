package com.dingapp.biz.page.fourpage;import java.util.ArrayList;import java.util.HashMap;import java.util.List;import org.json.JSONArray;import org.json.JSONException;import org.json.JSONObject;import view.refresh.PullToRefreshBase;import view.refresh.PullToRefreshBase.Mode;import view.refresh.PullToRefreshListView;import android.content.Context;import android.os.Bundle;import android.support.annotation.Nullable;import android.text.TextUtils;import android.view.KeyEvent;import android.view.LayoutInflater;import android.view.View;import android.view.View.OnClickListener;import android.view.ViewGroup;import android.view.inputmethod.EditorInfo;import android.view.inputmethod.InputMethodManager;import android.widget.AdapterView;import android.widget.AdapterView.OnItemClickListener;import android.widget.EditText;import android.widget.FrameLayout;import android.widget.ImageView;import android.widget.LinearLayout;import android.widget.ListView;import android.widget.PopupWindow;import android.widget.TextView;import android.widget.TextView.OnEditorActionListener;import com.android.volley.Response.Listener;import com.dingapp.andriod.z20.R;import com.dingapp.biz.AppConstants;import com.dingapp.biz.db.orm.JiFenGoodsListBean;import com.dingapp.biz.db.orm.JifenParentGoodsBean;import com.dingapp.biz.db.orm.TypeInfoBean;import com.dingapp.biz.net.RequestDataUtil;import com.dingapp.biz.page.adapters.DesignerChooseListAdapter;import com.dingapp.biz.page.adapters.JiFenGoodsListAdapter;import com.dingapp.biz.page.adapters.SearchRecordAdapter;import com.dingapp.biz.util.LogoutUtils;import com.dingapp.biz.util.StopRefresh;import com.dingapp.core.app.BaseFragment;import com.dingapp.core.util.Constants;import com.dingapp.core.util.UIUtil;import com.google.gson.Gson;import com.google.gson.reflect.TypeToken;public class JiFenGoodsCategoryListFragment extends BaseFragment implements		OnClickListener {	/**	 * 商品列表	 */	private List<JiFenGoodsListBean> dataList = new ArrayList<JiFenGoodsListBean>();	private DesignerChooseListAdapter dialogAdapter;	private DesignerChooseListAdapter dialogAdapter2;	private LinearLayout goods_category;	private LinearLayout goods_sort;	private TextView tv_category;	private TextView tv_sort;	private ImageView img_category;	private ImageView img_sort;	private PullToRefreshListView lv_goods_list;	private boolean category_flag = false;	private boolean sort_flag = false;	private EditText et_goods_detail_input;	// 判断是分类还是排序方式	private boolean isAreaOrStyle = true;	/**	 * 排序方式	 */	private String sort;	/**	 * 商品列表adapter	 */	private JiFenGoodsListAdapter goodsListAdapter;	public static enum MODE {		UP, DOWN	}	// 商品搜索记录和清除搜索记录	private Listener<String> recordListener = new Listener<String>() {		@Override		public void onResponse(String response) {			parserSearchRecordData(response);		}	};	private Listener<String> clearListener = new Listener<String>() {		@Override		public void onResponse(String response) {			parserClearSearchRecordData(response);		}	};	private LinearLayout ll_parent_search;	private ListView lv_search_record;	private TextView tv_close, tv_clear_search;	private FrameLayout fl_empty_data;	@Override	public View onCreateView(LayoutInflater inflater,			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {		return View.inflate(getActivity(), R.layout.jifen_goods_categorylist,				null);	}	private Listener<String> downListener = new Listener<String>() {		@Override		public void onResponse(String response) {			if (lv_goods_list != null) {				lv_goods_list.onRefreshComplete();			}			parseListData(response, MODE.DOWN);		}	};	private Listener<String> upListener = new Listener<String>() {		@Override		public void onResponse(String response) {			if (lv_goods_list != null) {				lv_goods_list.onRefreshComplete();			}			parseListData(response, MODE.UP);		}	};	private Listener<String> categoryListener = new Listener<String>() {		@Override		public void onResponse(String response) {			parseCategoryData(response);		}	};	/**	 * 分页数量	 */	private int page_idx;	protected void parseListData(String response, MODE mode) {		try {			JSONObject jsbJson = new JSONObject(response);			String statusCode = jsbJson.getString("statusCode");			String statusMsg = jsbJson.getString("statusMsg");			Gson gson = new Gson();			if (statusCode.equals("200")) {				JifenParentGoodsBean fromJson = gson.fromJson(						jsbJson.getString("data"), JifenParentGoodsBean.class);				List<JiFenGoodsListBean> jList = fromJson.getGoods_info();				if (mode == MODE.DOWN) {					page_idx = 0;					dataList.clear();				} else if (mode == MODE.UP) {					if (jList != null && jList.size() > 0) {						page_idx++;					}				}				if (jList != null && jList.size() > 0) {					dataList.addAll(jList);				}				setView();				goodsListAdapter.setData(dataList);			} else if (TextUtils.equals(statusCode, "1001")) {				UIUtil.showToast(getActivity(), "身份登录信息失效");				LogoutUtils.logout(getActivity());			} else {				UIUtil.showToast(getActivity(), "未知错误" + statusMsg);			}		} catch (JSONException e) {			e.printStackTrace();		}	}	private void setView() {		if (dataList != null && dataList.size() > 0) {			fl_empty_data.setVisibility(View.GONE);		} else {			fl_empty_data.setVisibility(View.VISIBLE);		}	}	/**	 * @param response	 */	protected void parseCategoryData(String response) {		try {			JSONObject jsbJson = new JSONObject(response);			String statusCode = jsbJson.getString("statusCode");			String statusMsg = jsbJson.getString("statusMsg");			Gson gson = new Gson();			if (statusCode.equals("200")) {				JSONObject dataJson = jsbJson.getJSONObject("data");				if (dataJson.has("type_info")) {					List<TypeInfoBean> typeList = gson.fromJson(							dataJson.getString("type_info"),							new TypeToken<List<TypeInfoBean>>() {							}.getType());					initCategoryData(typeList);				}			}		} catch (Exception e) {		}	}	@Override	public void onActivityCreated(Bundle savedInstanceState) {		super.onActivityCreated(savedInstanceState);		if (getArguments() != null) {			Bundle bundle = getArguments();			if (bundle.containsKey("category_id")) {				category_id = bundle.getInt("category_id");			} else if (bundle.containsKey("home_search")) {				isHomeSearch = bundle.getString("home_search");			}		}		initView();		initListener();		goodsListAdapter = new JiFenGoodsListAdapter(getActivity());		lv_goods_list.setAdapter(goodsListAdapter);		if (isHomeSearch.equals("true")) {			// 从首页搜索跳过来，先判断是否登录，拉取搜索记录，edittext自动获取焦点，键盘弹出			requestSearchRecord();			et_goods_detail_input.setFocusable(true);			et_goods_detail_input.requestFocus();			InputMethodManager imm = (InputMethodManager) getActivity()					.getSystemService(Context.INPUT_METHOD_SERVICE);			imm.showSoftInput(et_goods_detail_input, 0);			if (!TextUtils.isEmpty(AppConstants.member.getSessionId())) {				requestSearchRecord();			}		} else {			requestInitData(downListener, 0);		}		dialogAdapter = new DesignerChooseListAdapter(getActivity());		dialogAdapter2 = new DesignerChooseListAdapter(getActivity());		requestCategoryData();	}	@Override	public void onCreate(Bundle savedInstanceState) {		super.onCreate(savedInstanceState);	}	private void requestCategoryData() {		HashMap<String, String> postMap = new HashMap<String, String>();		String url = AppConstants.category_list;		postMap.put("type", "score");		postMap.put("is_homepage", "false");		postMap.put("need_brand", "false");		RequestDataUtil.getRequestInstance().requestData(categoryListener,				postMap, url, getActivity(), null, "normal");	}	/**	 * 1、标题搜索的时候 需要传递 title 其它字段可有可无 2、sort请求的时候 需要传递sort category_id 可传可不传	 * 3、分页加载 page_indx 只有在上拉刷新 以及下拉刷新的时候 需要传递 index	 * 	 * @param sort	 * @param category_id	 * @param title	 * @param page_idx	 */	private void requestInitData(Listener<String> listener, int page_idx) {		HashMap<String, String> postMap = new HashMap<String, String>();		if (category_id != 0 && category_id != -1) {			postMap.put("type_id", category_id + "");		}		if (sort != null && !TextUtils.isEmpty(sort)) {			postMap.put("sort", sort);		}		if (title != null && !TextUtils.isEmpty(title)) {			postMap.put("goods_title", title);		}		postMap.put("page_idx", page_idx + "");		RequestDataUtil.getRequestInstance().requestData(listener, postMap,				AppConstants.score_goods_list, getActivity(), null, "normal");	}	private int category_id = -1;	private String title = "";	private String isHomeSearch = "false";	/**	 * 填充分类数据	 */	private void initCategoryData(final List<TypeInfoBean> typeList) {		if (typeList == null || typeList.size() <= 0) {			return;		}		dialogAdapter.setList(typeList);	}	private void initListener() {		goods_sort.setOnClickListener(this);		goods_category.setOnClickListener(this);		lv_search_record.setOnItemClickListener(new OnItemClickListener() {			@Override			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,					long arg3) {				if (searchAdapter != null) {					title = (String) searchAdapter.getItem((int) arg3);				}				et_goods_detail_input.setText(title);				initSearch();				ll_parent_search.setVisibility(View.GONE);				if (getActivity() != null) {					UIUtil.hideKeyboard(getActivity());				}			}		});		et_goods_detail_input				.setOnEditorActionListener(new OnEditorActionListener() {					@Override					public boolean onEditorAction(TextView arg0, int arg1,							KeyEvent arg2) {						if (arg1 == EditorInfo.IME_ACTION_SEARCH) {							initSearch();							ll_parent_search.setVisibility(View.GONE);							if (getActivity() != null) {								UIUtil.hideKeyboard(getActivity());							}						}						return false;					}				});		tv_close.setOnClickListener(this);		tv_clear_search.setOnClickListener(this);	}	/**	 * 搜索内容	 */	protected void initSearch() {		title = et_goods_detail_input.getText().toString();		if (title != null) {			page_idx = 0;			requestInitData(downListener, 0);		} else {			UIUtil.showToast(getActivity(), "请输入搜索内容！");		}	}	private void initListViewData() {		lv_goods_list.setOnItemClickListener(new OnItemClickListener() {			@Override			public void onItemClick(AdapterView<?> parent, View view,					int position, long id) {				JiFenGoodsListBean item = (JiFenGoodsListBean) parent						.getAdapter().getItem(position);				int prd_id = item.getGoods_id();				Bundle bundle = new Bundle();				bundle.putInt("prd_id", prd_id);				openPage("jifen_goods_detail", bundle, true);			}		});	}	private void initView() {		goods_category = (LinearLayout) getView().findViewById(				R.id.ll_goods_category_jifen);		goods_sort = (LinearLayout) getView().findViewById(				R.id.ll_goods_sort_jifen);		tv_category = (TextView) getView().findViewById(				R.id.tv_text_category_jifen);		tv_sort = (TextView) getView().findViewById(R.id.tv_text_sort_jifen);		img_category = (ImageView) getView().findViewById(				R.id.img_category_jifen);		img_sort = (ImageView) getView().findViewById(R.id.img_sort_jifen);		lv_goods_list = (PullToRefreshListView) getView().findViewById(				R.id.lv_goods_list_jifen);		et_goods_detail_input = (EditText) getView().findViewById(				R.id.et_goods_detail_input_jifen);		getView().findViewById(R.id.img_back_jifen).setOnClickListener(				new OnClickListener() {					@Override					public void onClick(View v) {						if (popWindow != null && popWindow.isShowing()) {							popWindow.dismiss();						}						popBack(null);					}				});		initPullRefreshView();		ll_parent_search = (LinearLayout) getView().findViewById(				R.id.ll_search_goods_jifen);		lv_search_record = (ListView) getView().findViewById(				R.id.lv_goods_search_record_jifen);		tv_close = (TextView) getView().findViewById(R.id.tv_close_jifen);		tv_clear_search = (TextView) getView().findViewById(				R.id.tv_clear_goods_record_jifen);		fl_empty_data = (FrameLayout) getView()				.findViewById(R.id.fl_empty_data);		initListViewData();	}	@SuppressWarnings({ "rawtypes", "unchecked" })	private void initPullRefreshView() {		StopRefresh.initRefreshView(lv_goods_list, Mode.BOTH);		lv_goods_list				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {					@Override					public void onPullDownToRefresh(							PullToRefreshBase refreshView) {						StopRefresh.stopRefreash(lv_goods_list);						requestInitData(downListener, 0);					}					@Override					public void onPullUpToRefresh(PullToRefreshBase refreshView) {						StopRefresh.stopRefreash(lv_goods_list);						requestInitData(upListener, page_idx + 1);					}				});	}	private PopupWindow popWindow;	private ListView lv_dialog;	private View v_parent;	// 显示选择的弹出框	private void popDialog() {		// type是1，代表选择全部分类，2代表排序方式		if (popWindow == null) {			LayoutInflater inflater = LayoutInflater.from(getActivity());			View dialogView = inflater.inflate(R.layout.designer_choose_dialog,					null);			v_parent = dialogView.findViewById(R.id.v_parent);			lv_dialog = (ListView) dialogView					.findViewById(R.id.lv_designer_choose_dialog);			v_parent.setOnClickListener(this);			popWindow = new PopupWindow();			popWindow.setContentView(dialogView);			popWindow.setWidth(Constants.getScreenWidth());			popWindow.setHeight(Constants.getScreenHeight());		}		lv_dialog.setOnItemClickListener(new OnItemClickListener() {			@Override			public void onItemClick(AdapterView<?> arg0, View arg1,					int position, long arg3) {				popWindow.dismiss();				if (isAreaOrStyle) {					TypeInfoBean bean = (TypeInfoBean) dialogAdapter							.getItem((int) arg3);					tv_category.setText(bean.getType_name());					if (bean.getType_name() != null							&& bean.getType_name().equals("全部")) {						category_id = -1;					} else {						category_id = bean.getType_id();					}				} else {					if (position == 0) {						sort = "sale";					} else {						sort = "newest";					}				}				requestInitData(downListener, 0);			}		});		if (isAreaOrStyle) {			lv_dialog.setAdapter(dialogAdapter);		} else {			lv_dialog.setAdapter(dialogAdapter2);		}		popWindow.showAsDropDown(goods_category);	}	@Override	public void onClick(View v) {		if (v == goods_category) {			showCategoryView();			return;		} else if (v == goods_sort) {			showSortView();			return;		}		// } else if (v == tv_popularity) {		// dataList.clear();		// page_idx = 0;		// requestInitData(downListener, "popularity", category_id,		// et_goods_detail_input.getText().toString(), 0);		// initSortData(tv_popularity);		// sort = "popularity";		// return;		// }		if (v == tv_close) {			ll_parent_search.setVisibility(View.GONE);			return;		}		if (v == tv_clear_search) {			ll_parent_search.setVisibility(View.GONE);			requestClearSearchRecord();			return;		}		if (v == v_parent) {			if (popWindow != null) {				popWindow.dismiss();			}			return;		}	}	private void initSortData(TextView tv) {		tv.setSelected(true);		showArrowStyle(img_sort);		sort_flag = false;		tv_sort.setEnabled(false);	}	/**	 * 箭头方向	 * 	 * @param img	 */	private void showArrowStyle(ImageView img) {		if (img == img_category) {			if (category_flag) {				img_category						.setImageResource(R.drawable.detail_small_dowm_gray);			} else {				img_category.setImageResource(R.drawable.details_small);			}		} else if (img == img_sort) {			if (sort_flag) {				img_sort.setImageResource(R.drawable.detail_small_dowm_gray);			} else {				img_sort.setImageResource(R.drawable.details_small);			}		}	}	private void showCategoryView() {		if (popWindow != null && popWindow.isShowing()) {			popWindow.dismiss();		} else {			isAreaOrStyle = true;			popDialog();		}	}	private void showSortView() {		if (popWindow != null && popWindow.isShowing()) {			popWindow.dismiss();		} else {			dialogAdapter2.setList(initSortData());			isAreaOrStyle = false;			popDialog();		}	}	private List<TypeInfoBean> initSortData() {		List<TypeInfoBean> list = new ArrayList<TypeInfoBean>();		for (int i = 0; i < 2; i++) {			TypeInfoBean bean = new TypeInfoBean();			if (i == 0) {				bean.setType_name("最受欢迎");			} else {				bean.setType_name("最新");			}			list.add(bean);		}		return list;	}	private List<String> searchRecordList = new ArrayList<String>();	private SearchRecordAdapter searchAdapter;	// 如果登录了，可以请求搜索记录	private void requestSearchRecord() {		HashMap<String, String> postMap = new HashMap<String, String>();		postMap.put("type", "score");		RequestDataUtil.getRequestInstance().requestData(recordListener,				postMap, AppConstants.goods_search_history, getActivity(),				null, "true");	}	private void parserSearchRecordData(String response) {		try {			JSONObject jsbJson = new JSONObject(response);			String statusCode = jsbJson.getString("statusCode");			String statusMsg = jsbJson.getString("statusMsg");			if (statusCode.equals("200")) {				JSONArray array = jsbJson.getJSONArray("data");				searchRecordList.clear();				for (int i = 0; i < array.length(); i++) {					JSONObject dataJosn = array.getJSONObject(i);					if (dataJosn.has("value_name")) {						searchRecordList.add(dataJosn.getString("value_name"));					}				}				if (searchRecordList.size() > 0) {					ll_parent_search.setVisibility(View.VISIBLE);				}				searchAdapter = new SearchRecordAdapter(getActivity(),						searchRecordList);				lv_search_record.setAdapter(searchAdapter);			} else if (TextUtils.equals(statusCode, "1001")) {				UIUtil.showToast(getActivity(), "身份登录信息失效");				LogoutUtils.logout(getActivity());			} else {				UIUtil.showToast(getActivity(), "未知错误" + statusMsg);			}		} catch (JSONException e) {			e.printStackTrace();		}	}	private void requestClearSearchRecord() {		HashMap<String, String> postMap = new HashMap<String, String>();		postMap.put("type", "score");		RequestDataUtil.getRequestInstance().requestData(clearListener,				postMap, AppConstants.goods_delete_search_history,				getActivity(), null, "true");	}	private void parserClearSearchRecordData(String response) {		try {			JSONObject jsbJson = new JSONObject(response);			String statusCode = jsbJson.getString("statusCode");			String statusMsg = jsbJson.getString("statusMsg");			if (statusCode.equals("200")) {				JSONObject dataJson = jsbJson.getJSONObject("data");				if (dataJson.has("suc")) {					String suc = dataJson.getString("suc");					if (suc.equals("true")) {						searchRecordList.clear();						if (searchAdapter != null) {							searchAdapter.notifyDataSetChanged();						}						ll_parent_search.setVisibility(View.GONE);					}				}			} else if (TextUtils.equals(statusCode, "1001")) {				UIUtil.showToast(getActivity(), "身份登录信息失效");				LogoutUtils.logout(getActivity());			} else {				UIUtil.showToast(getActivity(), "未知错误" + statusMsg);			}		} catch (JSONException e) {			e.printStackTrace();		}	}	@Override	public boolean onKeyDown(int keyCode, KeyEvent event) {		if (popWindow != null && popWindow.isShowing()) {			popWindow.dismiss();		}		return super.onKeyDown(keyCode, event);	}}