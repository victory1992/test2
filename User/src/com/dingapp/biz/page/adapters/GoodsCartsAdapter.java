package com.dingapp.biz.page.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dingapp.andriod.z20.R;
import com.dingapp.biz.db.orm.CartListBean;
import com.dingapp.biz.db.orm.OrderPrdAttrsBean;
import com.dingapp.biz.util.ImageViewHWutils;
import com.dingapp.imageloader.core.ImageLoader;

public class GoodsCartsAdapter extends BaseAdapter {
	private List<CartListBean> list = new ArrayList<CartListBean>();
	private Context context;
	private AdapterListenr click;

	public GoodsCartsAdapter(Context context, AdapterListenr click) {
		this.context = context;
		this.click = click;
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
					R.layout.item_thirdpager, null);
			holder.iv_pic = (ImageView) convertView
					.findViewById(R.id.iv_item_carts);
			holder.tv_title = (TextView) convertView
					.findViewById(R.id.tv_item_carts_title);
			holder.tv_guige = (TextView) convertView
					.findViewById(R.id.tv_item_carts_guige);
			holder.tv_price = (TextView) convertView
					.findViewById(R.id.tv_item_carts_price);
			holder.iv_choose = (ImageView) convertView
					.findViewById(R.id.iv_item_carts_choose);
			holder.iv_reduce = (ImageView) convertView
					.findViewById(R.id.iv_reduce);
			holder.iv_add = (ImageView) convertView.findViewById(R.id.iv_add);
			holder.tv_num = (TextView) convertView
					.findViewById(R.id.tv_num_carts);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		CartListBean bean = (CartListBean) getItem(arg0);
		if (bean != null) {
			if (bean.getGoods_img() != null) {
				ImageLoader.getInstance().displayImage(
						bean.getGoods_img().getDetail_url()
								+ ImageViewHWutils.getWHImageVIew(250, 250),
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
			for (int i = 0; i < attrList.size(); i++) {
				String name = attrList.get(i).getAttr_name();
				String value = attrList.get(i).getAttr_value();
				sb.append(name + ":" + value);
			}
			holder.tv_guige.setText(sb.toString());
			holder.tv_num.setText(bean.getCnt() + "");
			if (bean.getIs_select() != null
					&& bean.getIs_select().equals("true")) {
				holder.iv_choose.setImageResource(R.drawable.pay_checked);
			} else {
				holder.iv_choose.setImageResource(R.drawable.pay_unchecked);
			}
		}
		holder.itemPositinon = arg0;
		initAdapterListener(holder);
		return convertView;
	}

	private void initAdapterListener(ViewHolder holder) {
		holder.iv_choose.setTag(holder);
		holder.iv_choose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int itemPosition = -1;
				Object tag = v.getTag();
				if (tag instanceof ViewHolder) {
					itemPosition = ((ViewHolder) tag).itemPositinon;
				}
				if (itemPosition == -1) {
					return;
				}
				CartListBean bean = list.get(itemPosition);
				if (bean.getIs_select() != null
						&& bean.getIs_select().equals("true")) {
					bean.setIs_select("false");
				} else {
					bean.setIs_select("true");
				}
				notifyDataSetChanged();
				click.onClickListener(bean, 3);
			}
		});
		holder.iv_add.setTag(holder);
		holder.iv_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int itemPosition = -1;
				Object tag = v.getTag();
				if (tag instanceof ViewHolder) {
					itemPosition = ((ViewHolder) tag).itemPositinon;
				}
				if (itemPosition == -1) {
					return;
				}
				CartListBean bean = list.get(itemPosition);
				click.onClickListener(bean, 1);
			}
		});
		holder.iv_reduce.setTag(holder);
		holder.iv_reduce.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int itemPosition = -1;
				Object tag = v.getTag();
				if (tag instanceof ViewHolder) {
					itemPosition = ((ViewHolder) tag).itemPositinon;
				}
				if (itemPosition == -1) {
					return;
				}
				CartListBean bean = list.get(itemPosition);
				click.onClickListener(bean, 2);
			}
		});
	}

	static class ViewHolder {
		private ImageView iv_pic;
		private TextView tv_title;
		private TextView tv_guige;
		private TextView tv_price;
		private ImageView iv_choose;
		private ImageView iv_add;
		private ImageView iv_reduce;
		private TextView tv_num;
		private int itemPositinon;
	}

	public interface AdapterListenr {
		// tag=1表示加，tag=2,表示减
		public abstract void onClickListener(CartListBean bean, int tag);
	}
}
