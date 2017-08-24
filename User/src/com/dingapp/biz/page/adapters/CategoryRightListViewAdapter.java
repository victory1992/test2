package com.dingapp.biz.page.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dingapp.andriod.z20.R;
import com.dingapp.biz.db.orm.ChildChildTypeInfoBean;
import com.dingapp.biz.db.orm.GoodsChildTypeBean;
import com.dingapp.biz.page.customview.MyGridView;
import com.dingapp.biz.util.ImageViewHWutils;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.Constants;
import com.dingapp.core.util.DispLengthUtil;
import com.dingapp.imageloader.core.ImageLoader;

public class CategoryRightListViewAdapter extends BaseAdapter {
	private Context context;
	private List<GoodsChildTypeBean> child_type_info = new ArrayList<GoodsChildTypeBean>();;
	private BaseFragment fragment;
	private ImageLoader instance;
	private String picUrl;
	private int focusId = -1;

	public CategoryRightListViewAdapter(Context context, BaseFragment fragment) {
		super();
		this.fragment = fragment;
		this.context = context;
		instance = ImageLoader.getInstance();

	}

	public void setData(List<GoodsChildTypeBean> mList, String picUrl,
			int type_id) {
		if (mList != null) {
			child_type_info = mList;
		} else {
			child_type_info.clear();
		}
		this.picUrl = picUrl;
		this.focusId = type_id;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return child_type_info == null ? 0 : child_type_info.size();
	}

	@Override
	public GoodsChildTypeBean getItem(int arg0) {
		return child_type_info.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup container) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context,
					R.layout.category_listview_item, null);
			holder.tv_category_title = (TextView) convertView
					.findViewById(R.id.tv_category_title);
			holder.mygridview_categroy = (MyGridView) convertView
					.findViewById(R.id.mygridview_categroy);
			holder.iv_focus = (ImageView) convertView
					.findViewById(R.id.iv_catgory_second_focus);
			holder.ll_focus = (LinearLayout) convertView
					.findViewById(R.id.ll_category_second_focus);
			holder.ll_more = (LinearLayout) convertView
					.findViewById(R.id.ll_category_second_more);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.ll_focus.setVisibility(View.GONE);
		if (position == 0) {
			holder.ll_focus.setVisibility(View.VISIBLE);
			if (picUrl != null && !TextUtils.isEmpty(picUrl)) {
				LayoutParams params = holder.iv_focus.getLayoutParams();
				int widthS = Constants.getScreenWidth();
				int newWidth = widthS - (int) DispLengthUtil.dipToPx(85);
				params.width = newWidth;
				params.height = newWidth*242/610;
				holder.iv_focus.setLayoutParams(params);
				instance.displayImage(
						picUrl
								+ ImageViewHWutils.getWHImageVIew(params.width,
										params.height), holder.iv_focus);
			}
		}
		GoodsChildTypeBean bean = getItem(position);
		holder.tv_category_title.setText(bean.getChild_type_name());
		final MyGridViewAdapter myGridViewAdapter = new MyGridViewAdapter(
				context, bean.getChild_child_type_info());
		holder.mygridview_categroy.setAdapter(myGridViewAdapter);
		holder.mygridview_categroy.setTag(holder);
		holder.mygridview_categroy
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// gridview的每个条目设置点击事件，跳转到分类列表
						Bundle bundle = new Bundle();
						ChildChildTypeInfoBean bean = myGridViewAdapter
								.getItem(position);
						bundle.putInt("child_child_category_id",
								bean.getChild_child_type_id());
						fragment.openPage("goods_category", bundle, false);
					}
				});
		holder.itemPos = position;
		initListener(holder);
		return convertView;
	}

	private void initListener(final ViewHolder holder) {
		holder.ll_more.setTag(holder);
		holder.ll_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int itemPos = -1;
				Object tag = arg0.getTag();
				if (tag instanceof ViewHolder) {
					itemPos = ((ViewHolder) tag).itemPos;
				}
				if (itemPos == -1) {
					return;
				}
				Bundle bundle = new Bundle();
				GoodsChildTypeBean bean = getItem(itemPos);
				bundle.putInt("child_category_id", bean.getChild_type_id());
				fragment.openPage("goods_category", bundle, false);
			}
		});
		holder.ll_focus.setTag(holder);
		holder.ll_focus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int itemPos = -1;
				Object tag = v.getTag();
				if (tag instanceof ViewHolder) {
					itemPos = ((ViewHolder) tag).itemPos;
				}
				if (itemPos == -1) {
					return;
				}
				Bundle bundle = new Bundle();
				bundle.putInt("category_id", focusId);
				fragment.openPage("goods_category", bundle, false);
			}
		});
	}

	private class ViewHolder {
		private MyGridView mygridview_categroy;
		private TextView tv_category_title;
		private int itemPos;
		private ImageView iv_focus;
		private LinearLayout ll_focus;
		private LinearLayout ll_more;
	}

	private class MyGridViewAdapter extends BaseAdapter {
		private Context context;
		private List<ChildChildTypeInfoBean> productList = new ArrayList<ChildChildTypeInfoBean>();

		public MyGridViewAdapter(Context context,
				List<ChildChildTypeInfoBean> productList) {
			super();
			this.context = context;
			this.productList = productList;

		}

		@Override
		public int getCount() {
			return productList == null ? 0 : productList.size();
		}

		@Override
		public ChildChildTypeInfoBean getItem(int position) {
			return productList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MyViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new MyViewHolder();
				convertView = View.inflate(context,
						R.layout.category_listview_item_gridview_item, null);
				viewHolder.img_gridview_item = (ImageView) convertView
						.findViewById(R.id.img_gridview_item);
				viewHolder.tv_gridview_title = (TextView) convertView
						.findViewById(R.id.tv_gridview_title);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (MyViewHolder) convertView.getTag();
			}
			viewHolder.img_gridview_item.setImageResource(R.drawable.icon);
			ChildChildTypeInfoBean bean = getItem(position);
			if (bean != null) {
				viewHolder.tv_gridview_title.setText(bean
						.getChild_child_type_name());
				if (bean.getType_pic() != null
						&& !TextUtils.isEmpty(bean.getType_pic()
								.getDetail_url())) {
					instance.displayImage(bean.getType_pic().getDetail_url(),
							viewHolder.img_gridview_item);
				}
			}
			return convertView;
		}

		private class MyViewHolder {
			private ImageView img_gridview_item;
			private TextView tv_gridview_title;
		}
	}
}
