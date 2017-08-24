package com.dingapp.biz.page.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dingapp.andriod.z20.R;
import com.dingapp.biz.db.orm.GoodsFavBean;
import com.dingapp.imageloader.core.ImageLoader;

public final class FavGoodsAdapter extends BaseAdapter {
	private List<GoodsFavBean> list = new ArrayList<GoodsFavBean>();
	private Context context;

	public FavGoodsAdapter(Context context) {
		this.context = context;
	}

	public void setList(List<GoodsFavBean> list2) {
		if (list2 != null) {
			list.clear();
			list.addAll(list2);
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_desigener_works, null);
			holder.iv_pic = (ImageView) convertView
					.findViewById(R.id.iv_item_designerworks);
			holder.tv_title = (TextView) convertView
					.findViewById(R.id.tv_item_designerworks_title);
			holder.tv_guige = (TextView) convertView
					.findViewById(R.id.tv_item_designerworks_guige);
			holder.tv_price = (TextView) convertView
					.findViewById(R.id.tv_item_designerworks_price);
			holder.tv_numsale = (TextView) convertView
					.findViewById(R.id.tv_item_designerworks_numsale);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		GoodsFavBean bean = (GoodsFavBean) getItem(arg0);
		if (bean != null) {
			if (bean.getGoods_img() != null) {
				ImageLoader.getInstance().displayImage(
						bean.getGoods_img().getDetail_url(), holder.iv_pic);
			}
			holder.tv_title.setText(bean.getGoods_title());
			holder.tv_guige.setVisibility(View.GONE);
			String time = bean.getCollect_time();
			String time2 = time.substring(0,time.indexOf(" "));
			holder.tv_numsale.setText("收藏时间:" + time2);
			holder.tv_price.setText("￥" + bean.getGoods_price());
		}
		return convertView;
	}

	static class ViewHolder {
		private ImageView iv_pic;
		private TextView tv_title;
		private TextView tv_guige;
		private TextView tv_price;
		private TextView tv_numsale;
	}

}
