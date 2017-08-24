package com.dingapp.biz.page.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dingapp.andriod.z20.R;
import com.dingapp.biz.db.orm.GoodsBean;
import com.dingapp.biz.db.orm.GoodsChildBean;
import com.dingapp.biz.page.customview.MyGridView;
import com.dingapp.biz.util.ImageViewHWutils;
import com.dingapp.biz.util.Utils;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.Constants;
import com.dingapp.core.util.DispLengthUtil;
import com.dingapp.imageloader.core.ImageLoader;

public class FirstPagerRecommendListViewAdapter extends BaseAdapter {
	private Context context;
	private List<GoodsBean> mList = new ArrayList<GoodsBean>();
	private BaseFragment mFragment;
	// 判断从首页进来还是从积分进来
	private String type = "home";

	public FirstPagerRecommendListViewAdapter(Context context,
			BaseFragment mFragment, String type) {
		super();
		this.context = context;
		this.mFragment = mFragment;
		this.type = type;
	}

	public void setData(List<GoodsBean> ormList) {
		if (ormList != null) {
			mList.clear();
			mList.addAll(ormList);
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public GoodsBean getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context,
					R.layout.first_goods_listview_item, null);
			holder.tv_recommend_title = (TextView) convertView
					.findViewById(R.id.tv_recommend_title);
			holder.gv_recommend_category = (MyGridView) convertView
					.findViewById(R.id.gv_recommend_category);
			holder.rl_first_goods = (RelativeLayout) convertView
					.findViewById(R.id.rl_first_goods);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();

		}
		holder.tv_recommend_title.setText(mList.get(position).getType_name());
		List<GoodsChildBean> productList = mList.get(position).getGoods_info();
		holder.gv_recommend_category.setFocusable(false);
		final RecommendItemGridViewAdapter recommendItemGridViewAdapter = new RecommendItemGridViewAdapter(
				context, productList);
		holder.gv_recommend_category.setAdapter(recommendItemGridViewAdapter);
		holder.gv_recommend_category
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// 进入详情页、携带prd_id
						GoodsChildBean item = recommendItemGridViewAdapter
								.getItem((int) id);
						Bundle bundle = new Bundle();
						bundle.putInt("prd_id", item.getGoods_id());
						if (type.equals("home")) {
							mFragment.openPage("goods_detail_pager", bundle,
									true);
						} else {
							mFragment.openPage("jifen_goods_detail", bundle,
									true);
						}

					}
				});
		holder.itemPosition = position;
		holder.rl_first_goods.setTag(holder);
		holder.rl_first_goods.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int itPosition = -1;
				Object tag = v.getTag();
				if (tag instanceof ViewHolder) {
					itPosition = ((ViewHolder) tag).itemPosition;
				}
				if (itPosition == -1) {
					return;
				}
				Bundle bundle = new Bundle();
				GoodsBean bean = (GoodsBean) getItem(itPosition);
				int category_id = bean.getType_id();
				bundle.putInt("category_id", category_id);
				bundle.putString("type_title", bean.getType_name());
				if (type.equals("home")) {
					mFragment.openPage("goods_category", bundle, false);
				} else {
					mFragment.openPage("jifen_category_list", bundle, false);
				}

			}
		});
		return convertView;
	}

	private class ViewHolder {
		private RelativeLayout rl_first_goods;
		private TextView tv_recommend_title;
		private MyGridView gv_recommend_category;
		private int itemPosition;
	}

	private class RecommendItemGridViewAdapter extends BaseAdapter {
		private Context context;
		private List<GoodsChildBean> productList;

		public RecommendItemGridViewAdapter(Context context,
				List<GoodsChildBean> productList2) {
			super();
			this.productList = productList2;
			this.context = context;
		}

		@Override
		public int getCount() {
			return productList == null ? 0 : productList.size();
		}

		@Override
		public GoodsChildBean getItem(int position) {
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
						R.layout.goods_gridview_item_recommend, null);

				viewHolder.tv_des_recommend = (TextView) convertView
						.findViewById(R.id.tv_des_recommend);
				viewHolder.tv_factoring_price = (TextView) convertView
						.findViewById(R.id.tv_factoring_price);
				viewHolder.img_pic_recommend = (ImageView) convertView
						.findViewById(R.id.img_pic_recommend);
				viewHolder.iv_vip_tag = (ImageView) convertView
						.findViewById(R.id.img_pic_vip_tag);
				viewHolder.tv_nomal_price = (TextView) convertView
						.findViewById(R.id.tv_nomal_price);
				viewHolder.tv_factoring_price_score = (TextView) convertView
						.findViewById(R.id.tv_factoring_price_score);
				viewHolder.tv_nomal_price
						.setPaintFlags(TextPaint.STRIKE_THRU_TEXT_FLAG);
				LayoutParams lp = viewHolder.img_pic_recommend
						.getLayoutParams();
				int width = (int) (Constants.getScreenWidth() - DispLengthUtil
						.dipToPx(4)) / 2;
				lp.width = width;
				lp.height = width;
				viewHolder.img_pic_recommend.setLayoutParams(lp);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (MyViewHolder) convertView.getTag();

			}
			GoodsChildBean product = productList.get(position);
			viewHolder.iv_vip_tag.setVisibility(View.GONE);
			viewHolder.tv_nomal_price.setVisibility(View.GONE);
			viewHolder.tv_factoring_price_score.setVisibility(View.GONE);
			if (product != null) {
				viewHolder.tv_des_recommend.setText(product.getGoods_name());
				if (type.equals("home")) {
					viewHolder.tv_factoring_price.setText("￥"
							+ Utils.keepDouble2(product.getGoods_price()));
				} else {
					viewHolder.tv_factoring_price.setText(""
							+ product.getGoods_score());
					viewHolder.tv_factoring_price_score
							.setVisibility(View.VISIBLE);
				}

				if (product.getGoods_pic() != null
						&& product.getGoods_pic().getDetail_url() != null
						&& !TextUtils.isEmpty(product.getGoods_pic()
								.getDetail_url())) {
					ImageLoader.getInstance()
							.displayImage(
									product.getGoods_pic().getDetail_url(),
									viewHolder.img_pic_recommend);
				}
				if (product.getIs_vip() != null
						&& product.getIs_vip().equals("true")) {
					viewHolder.iv_vip_tag.setVisibility(View.VISIBLE);
					viewHolder.tv_nomal_price.setVisibility(View.VISIBLE);
					viewHolder.tv_nomal_price.setText("￥"
							+ product.getGoods_price());
					viewHolder.tv_factoring_price.setText("￥"
							+ product.getVip_price());
				}
			}
			return convertView;
		}

		private class MyViewHolder {
			private ImageView img_pic_recommend;
			private TextView tv_des_recommend;
			private TextView tv_factoring_price;
			private TextView tv_nomal_price;
			private ImageView iv_vip_tag;
			private TextView tv_factoring_price_score;
		}

	}
}
