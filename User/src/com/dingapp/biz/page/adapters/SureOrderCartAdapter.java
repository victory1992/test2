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
import com.dingapp.biz.db.orm.CartListBean;
import com.dingapp.biz.db.orm.OrderPrdAttrsBean;
import com.dingapp.biz.util.ImageViewHWutils;
import com.dingapp.imageloader.core.ImageLoader;

public class SureOrderCartAdapter extends BaseAdapter {
	private List<CartListBean> list = new ArrayList<CartListBean>();
	private Context context;

	public SureOrderCartAdapter(Context context, List<CartListBean> list2) {
		this.context = context;
		this.list = list2;
	}

	public void setList(List<CartListBean> list2) {
		if (list2 != null) {
			list.clear();
			list.addAll(list2);
		}
		notifyDataSetChanged();
	}

	public List<CartListBean> getNewList() {
		return list;
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
					R.layout.item_sureorder_cart, null);
			holder.iv_pic = (ImageView) convertView
					.findViewById(R.id.iv_item_carts);
			holder.tv_title = (TextView) convertView
					.findViewById(R.id.tv_item_carts_title);
			holder.tv_guige = (TextView) convertView
					.findViewById(R.id.tv_item_carts_guige);
			holder.tv_price = (TextView) convertView
					.findViewById(R.id.tv_item_carts_price);
			holder.tv_num = (TextView) convertView
					.findViewById(R.id.tv_goods_num);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		CartListBean bean = (CartListBean) getItem(arg0);
		if (bean != null) {
			if (bean.getGoods_img() != null) {
				ImageLoader.getInstance().displayImage(
						bean.getGoods_img().getDetail_url()
								+ ImageViewHWutils.getWHImageVIew(220, 220),
						holder.iv_pic);
			}
			holder.tv_title.setText(bean.getGoods_title());
			if (bean.getIs_vip() != null && bean.getIs_vip().equals("true")) {
				holder.tv_price.setText("￥" + bean.getVip_price());
			} else {
				holder.tv_price.setText("￥" + bean.getGoods_price());
			}

			List<OrderPrdAttrsBean> attrList = bean.getGoods_attrs();
			StringBuilder sb = new StringBuilder();
			if (attrList != null) {
				for (int i = 0; i < attrList.size(); i++) {
					String name = attrList.get(i).getAttr_name();
					String value = attrList.get(i).getAttr_value();
					sb.append(name + ":" + value);
				}
			}
			holder.tv_guige.setText(sb.toString());
			holder.tv_num.setText("x" + bean.getCnt());
		}
		return convertView;
	}

	static class ViewHolder {
		private ImageView iv_pic;
		private TextView tv_title;
		private TextView tv_guige;
		private TextView tv_price;
		private TextView tv_num;
	}
}
