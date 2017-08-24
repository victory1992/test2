package com.dingapp.biz.page.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dingapp.andriod.z20.R;
import com.dingapp.biz.db.orm.GoodsListBean.DataEntity.PrdsEntity;
import com.dingapp.biz.util.ImageViewHWutils;
import com.dingapp.imageloader.core.ImageLoader;

public class GoodsListAdapter extends BaseAdapter {
	private Context context;
	private List<PrdsEntity> mList = new ArrayList<PrdsEntity>();
	private ImageLoader imageLoader;

	public GoodsListAdapter(Context context) {
		super();
		this.context = context;
		if (imageLoader == null) {
			imageLoader = ImageLoader.getInstance();
		}
	}

	public void setData(List<PrdsEntity> ormList) {
		if (ormList != null) {
			mList.clear();
			mList.addAll(ormList);
		}
		notifyDataSetInvalidated();
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public PrdsEntity getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertview, ViewGroup contain) {
		PrdsEntity prdsEntity = mList.get(position);
		ViewHolder holder;
		if (convertview == null) {
			holder = new ViewHolder();
			convertview = View.inflate(context, R.layout.goods_list_item, null);
			holder.img_icon = (ImageView) convertview
					.findViewById(R.id.img_icon);
			holder.tv_goods_des = (TextView) convertview
					.findViewById(R.id.tv_goods_des);
			holder.tv_total_money_goods = (TextView) convertview
					.findViewById(R.id.tv_total_money_goods);
			holder.tv_visitor = (TextView) convertview
					.findViewById(R.id.tv_visitor);
			convertview.setTag(holder);
		} else {
			holder = (ViewHolder) convertview.getTag();
		}
		holder.tv_visitor.setVisibility(View.GONE);
		if (prdsEntity != null) {
			holder.tv_visitor.setText("已出售" + prdsEntity.getPay_cnt() + "件");
			holder.tv_goods_des.setText(prdsEntity.getGoods_name());
			holder.tv_total_money_goods.setText("￥"
					+ prdsEntity.getGoods_price() + "");
			if (prdsEntity.getGoods_pic() != null) {
				imageLoader.displayImage(
						prdsEntity.getGoods_pic().getDetail_url()
								+ ImageViewHWutils.getWHImageVIew(220, 220),
						holder.img_icon);
			}
		}
		return convertview;
	}

	private static class ViewHolder {
		private ImageView img_icon;
		private TextView tv_goods_des;
		private TextView tv_visitor;
		private TextView tv_total_money_goods;
	}

}
