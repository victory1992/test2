package com.dingapp.biz.page.fourpage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.orm.CommentOrderBean;
import com.dingapp.biz.db.orm.ImageBean;
import com.dingapp.biz.db.orm.OrderParamInfo;
import com.dingapp.biz.net.RequestDataUtil;
import com.dingapp.biz.page.choosephoto.MultiImageSelectorActivity;
import com.dingapp.biz.page.customview.MyGridView;
import com.dingapp.biz.util.DialogLoading;
import com.dingapp.biz.util.ImageUtils;
import com.dingapp.biz.util.LogoutUtils;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.app.StubActivity;
import com.dingapp.core.util.AndroidUtil;
import com.dingapp.core.util.UIUtil;
import com.dingapp.imageloader.core.ImageLoader;
import com.google.gson.Gson;

public class CommentOrderFragment extends BaseFragment implements
		OnClickListener {
	private ListView lv_comment_list;
	private ImageView iv_comment_back;
	private TextView tv_sure;
	private CommentAdapter commentAdapter;
	private List<CommentOrderBean> mList = new ArrayList<CommentOrderBean>();
	ArrayList<String> goodsPiclist = new ArrayList<String>();
	ArrayList<String> goodsNamelist = new ArrayList<String>();
	ArrayList<String> goodsIdlist = new ArrayList<String>();
	private String order_id;
	// 图片对于的pos
	private int parentPosition;
	private Listener<String> upLoadListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserUpload(response);
		}
	};
	private Listener<String> sureListener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserSureData(response);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.comment_order, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments() != null) {
			if (getArguments().containsKey(AppConstants.KEY)) {
				order_id = getArguments().getString(AppConstants.KEY);
			}
			if (getArguments().containsKey("goodsPiclist")) {
				goodsPiclist = getArguments()
						.getStringArrayList("goodsPiclist");
			}
			if (getArguments().containsKey("goodsNamelist")) {
				goodsNamelist = getArguments().getStringArrayList(
						"goodsNamelist");
			}
			if (getArguments().containsKey("goodsIdlist")) {
				goodsIdlist = getArguments().getStringArrayList("goodsIdlist");
			}

		}
		initView();
		initListener();
		commentAdapter = new CommentAdapter(getActivity());
		lv_comment_list.setAdapter(commentAdapter);
		initData();
	}

	private void initData() {
		for (int i = 0; i < goodsNamelist.size(); i++) {
			CommentOrderBean bean = new CommentOrderBean();
			bean.setGoods_id(Integer.parseInt(goodsIdlist.get(i)));
			bean.setGoods_title(goodsNamelist.get(i));
			bean.setGoods_pic(goodsPiclist.get(i));
			List<String> picurl = new ArrayList<String>();
			picurl.add("add_pics");
			bean.setNative_pic_url(picurl);
			bean.setNet_pic_url(new ArrayList<String>());
			bean.setContent("好评");
			bean.setScore(5);
			mList.add(bean);
		}
		commentAdapter.notifyDataSetChanged();
	}

	private void initView() {
		lv_comment_list = (ListView) getView().findViewById(R.id.lv_comment);
		iv_comment_back = (ImageView) getView().findViewById(
				R.id.iv_comment_back);
		tv_sure = (TextView) getView().findViewById(R.id.tv_comment_sure);
	}

	private void initListener() {
		iv_comment_back.setOnClickListener(this);
		tv_sure.setOnClickListener(this);
	}

	@Override
	public void onDataReset(Bundle bundle) {
		super.onDataReset(bundle);
		if (bundle != null && bundle.containsKey("pic_list")) {
			List<String> list_return = bundle.getStringArrayList("pic_list");
			refreshPics(0, "add", list_return);
		}
	}

	// type = "delete"代表删除后刷新，type = "add"代表添加后刷新
	private void refreshPics(int position, String type, List<String> picUrls) {
		List<String> nativeList = mList.get(parentPosition).getNative_pic_url();
		if (nativeList.contains("add_pics")) {
			nativeList.remove("add_pics");
		}
		if (type.equals("delete")) {
			nativeList.remove(position);
			// 删除网络的集合
			List<String> webList = mList.get(parentPosition).getNet_pic_url();
			if (webList != null && webList.size() >= position) {
				webList.remove(position);
			}
		} else {
			nativeList.addAll(picUrls);
			// 上传添加的图片
			upLoadFile(picUrls.get(0));
		}
		if (nativeList.size() < 6) {
			nativeList.add("add_pics");
		}
		if (mList.size() > parentPosition) {
			CommentOrderBean bean = mList.get(parentPosition);
			bean.setNative_pic_url(nativeList);
			// 啥时候清空呢
		}
		commentAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		if (v == iv_comment_back) {
			popBack(null);
			return;
		}
		if (v == tv_sure) {
			// Bundle bundle = new Bundle();
			// popBack(bundle);
			requestComment();
			return;
		}
	}

	class CommentAdapter extends BaseAdapter {
		private Context context;
		private MyFoucus myFoucus;
		private MyWatch myWatch;

		public CommentAdapter(Context context) {
			super();
			this.context = context;
			myFoucus = new MyFoucus();
			myWatch = new MyWatch();
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public CommentOrderBean getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(context,
						R.layout.item_comment_order, null);
				holder.et_content = (EditText) convertView
						.findViewById(R.id.et_article_content);
				holder.gv_pic = (MyGridView) convertView
						.findViewById(R.id.gv_publish_pics);
				holder.iv_goods_pic = (ImageView) convertView
						.findViewById(R.id.iv_item_goods);
				holder.rb = (RatingBar) convertView
						.findViewById(R.id.ratingbar);
				holder.tv_goods_name = (TextView) convertView
						.findViewById(R.id.tv_goods_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();

			}
			final CommentOrderBean bean = getItem(position);
			holder.et_content.setTag(position);
			holder.rb.setTag(position);
			if (bean != null) {
				holder.tv_goods_name.setText(bean.getGoods_title());
				ImageLoader.getInstance().displayImage(bean.getGoods_pic(),
						holder.iv_goods_pic);
				holder.rb.setRating(bean.getScore());
				final ChildCommentAdapter childAdapter = new ChildCommentAdapter(
						context, bean.getNative_pic_url());
				holder.gv_pic.setAdapter(childAdapter);
				holder.gv_pic.setTag(position);
				holder.gv_pic.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						parentPosition = (Integer) parent.getTag();
						String url = childAdapter.getItem((int) id);
						if (url.equals("add_pics")) {
							Bundle bundle = new Bundle();
							bundle.putInt(
									MultiImageSelectorActivity.EXTRA_SELECT_COUNT,
									1);
							openPage("choose_photo", bundle, true);
						}
					}
				});
			}
			View currentFocus = ((StubActivity) context).getCurrentFocus();
			// 这地方依旧不明白啥原因
			if (currentFocus != null) {
				currentFocus.clearFocus();
			}
			if (currentFocus != null) {
				currentFocus.requestFocus();
			}
			holder.et_content.setOnFocusChangeListener(myFoucus);
			holder.et_content.removeTextChangedListener(myWatch);
			if (!TextUtils.isEmpty(bean.getContent())) {
				holder.et_content.setText(bean.getContent());
			} else {
				holder.et_content.setText("好评");
			}
			holder.et_content.addTextChangedListener(myWatch);
			holder.itemPosition = position;
			holder.rb
					.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

						@Override
						public void onRatingChanged(RatingBar arg0, float arg1,
								boolean arg2) {
							int position = (Integer) arg0.getTag();
							CommentOrderBean bean = mList.get(position);
							bean.setScore((int) arg0.getRating());
						}
					});
			return convertView;
		}

		class MyFoucus implements OnFocusChangeListener {
			// 当获取焦点时修正myWatch中的position值,这是最重要的一步!
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					int position = (Integer) v.getTag();
					myWatch.position = position;
				}
			}
		}

		class MyWatch implements TextWatcher {
			public int position;

			@Override
			public void afterTextChanged(Editable s) {
				if (s != null && !"".equals(s.toString())) {
					CommentOrderBean bean = mList.get(position);
					bean.setContent(s.toString());
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

		}

		private class ViewHolder {
			private TextView tv_goods_name;
			private MyGridView gv_pic;
			private int itemPosition;
			private ImageView iv_goods_pic;
			private RatingBar rb;
			private EditText et_content;
		}

		private class ChildCommentAdapter extends BaseAdapter {
			private Context context;
			private List<String> productList;

			public ChildCommentAdapter(Context context,
					List<String> productList2) {
				super();
				this.productList = productList2;
				this.context = context;
			}

			@Override
			public int getCount() {
				return productList.size();
			}

			@Override
			public String getItem(int position) {
				return productList.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertview, ViewGroup parent) {
				ViewHolder holder = null;
				if (convertview == null) {
					holder = new ViewHolder();
					convertview = LayoutInflater.from(getActivity()).inflate(
							R.layout.gridview_pics, null);
					holder.iv_pic = (ImageView) convertview
							.findViewById(R.id.gv_imgs);
					holder.iv_remove = (ImageView) convertview
							.findViewById(R.id.iv_delete);
					convertview.setTag(holder);
				} else {
					holder = (ViewHolder) convertview.getTag();
				}
				holder.itemPosition = position;
				initAdapterListener(holder);
				String picUrl = productList.get(position);
				if (picUrl != null && !picUrl.equals("add_pics")) {
					try {
						Bitmap bm = ImageUtils.decodeSampledBitmapFromResource(
								picUrl, 320, 320);
						if (bm != null) {
							int digree = ImageUtils.getDigree(picUrl);
							if (digree != 0) {
								if (digree != 0) {// 旋转图片
									Matrix m = new Matrix();
									m.postRotate(digree);
									Bitmap rotateBitmap = Bitmap.createBitmap(
											bm, 0, 0, bm.getWidth(),
											bm.getHeight(), m, true);
									bm.recycle();
									bm = rotateBitmap;
								}
							}
						}
						holder.iv_pic.setImageBitmap(bm);
					} catch (Exception e) {
						ImageLoader.getInstance().displayImage(
								"file://" + picUrl, holder.iv_pic);
					}
					holder.iv_remove.setVisibility(View.VISIBLE);
				} else {
					holder.iv_pic.setImageResource(R.drawable.carame);
					holder.iv_remove.setVisibility(View.GONE);
				}
				return convertview;
			}

			class ViewHolder {
				private ImageView iv_pic;
				private ImageView iv_remove;
				private int itemPosition;
			}

			private void initAdapterListener(final ViewHolder holder) {
				holder.iv_remove.setTag(holder);
				holder.iv_remove.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						int itemPosition = -1;
						Object tag = holder.iv_remove.getTag();
						if (tag instanceof ViewHolder) {
							itemPosition = holder.itemPosition;
						}
						if (itemPosition == -1) {
							return;
						}
						refreshPics(itemPosition, "delete", null);
					}
				});
			}
		}
	}

	// 上传图片
	private void upLoadFile(String pic_url) {
		if (AndroidUtil.isNetworkAvailable(getActivity())) {
			HashMap<String, String> postMap = new HashMap<String, String>();
			postMap.put("platform", AppConstants.PLATFORM);
			postMap.put("session_id", AppConstants.member.getSessionId());
			List<ImageBean> imageList = new ArrayList<ImageBean>();
			ImageBean imgBean = new ImageBean();
			Bitmap bm = ImageUtils.decodeSampledBitmapFromResource(pic_url,
					640, 640);
			int digree = ImageUtils.getDigree(pic_url);
			if (digree != 0) {
				if (digree != 0) {// 旋转图片
					Matrix m = new Matrix();
					m.postRotate(digree);
					Bitmap rotateBitmap = Bitmap.createBitmap(bm, 0, 0,
							bm.getWidth(), bm.getHeight(), m, true);
					bm.recycle();
					bm = rotateBitmap;
				}
			}
			byte[] data = ImageUtils.Bitmap2Bytes(bm, CompressFormat.PNG);
			bm.recycle();
			imgBean.setData(data);
			imgBean.setName("resource");
			imgBean.setFileName(0 + ".png");
			imageList.add(imgBean);
			RequestDataUtil.getRequestInstance().requestData(upLoadListener,
					postMap, AppConstants.upload_file, getActivity(),
					imageList, "normal");
		} else {
			UIUtil.showToast(getActivity(), AppConstants.NetNotifice);
		}
	}

	private void parserUpload(String response) {
		try {
			JSONObject jsonObj = new JSONObject(response);
			String statusCode = jsonObj.getString("statusCode");
			String statusMsg = jsonObj.getString("statusMsg");
			if (TextUtils.equals(statusCode, "200")) {
				JSONObject dataJson = jsonObj.getJSONObject("data");
				if (dataJson.has("detail_url")) {
					String detail_url = dataJson.getString("detail_url");
					mList.get(parentPosition).getNet_pic_url().add(detail_url);
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

	// 确定评价
	private void requestComment() {
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("order_id", order_id);
		ArrayList<OrderParamInfo> infoList = new ArrayList<OrderParamInfo>();
		for (int i = 0; i < mList.size(); i++) {
			OrderParamInfo info = new OrderParamInfo();
			CommentOrderBean bean = mList.get(i);
			info.setGoods_id(bean.getGoods_id());
			info.setContent(bean.getContent());
			info.setScore(bean.getScore());
			if (bean.getNet_pic_url() != null
					&& bean.getNet_pic_url().size() > 0) {
				StringBuilder sb = new StringBuilder();
				for (int j = 0; j < bean.getNet_pic_url().size(); j++) {
					sb.append(",");
					sb.append(bean.getNet_pic_url().get(j));
				}
				info.setPics(sb.toString().substring(1));
			}
			infoList.add(info);
		}
		Gson gson = new Gson();
		String json = gson.toJson(infoList);
		postMap.put("evaluate_info", json);
		RequestDataUtil.getRequestInstance().requestData(sureListener, postMap,
				AppConstants.evaluate_add, getActivity(), null, "true");
	}

	private void parserSureData(String response) {
		try {
			JSONObject jsonObj = new JSONObject(response);
			String statusCode = jsonObj.getString("statusCode");
			String statusMsg = jsonObj.getString("statusMsg");
			if (TextUtils.equals(statusCode, "200")) {
				JSONObject dataJson = jsonObj.getJSONObject("data");
				if (dataJson.has("suc")) {
					String suc = dataJson.getString("suc");
					if (suc.equals("true")) {
						UIUtil.showToast(getActivity(), "评价成功");
						popBack(null);
					} else {
						UIUtil.showToast(getActivity(), "评价失败");
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
}
