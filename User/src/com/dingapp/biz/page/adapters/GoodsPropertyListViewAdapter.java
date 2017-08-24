package com.dingapp.biz.page.adapters;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dingapp.andriod.z20.R;
import com.dingapp.biz.db.orm.GoodsAttrsBean;
import com.dingapp.biz.db.orm.GoodsChildAttrsBean;
import com.dingapp.biz.page.customview.MyGridView;

/**
 * @author Administrator
 * 
 */
public class GoodsPropertyListViewAdapter extends BaseAdapter {
	private List<GoodsAttrsBean> mList;
	private Context context;
	private OnPropertyListener listener;
	/**
	 * 用来存储商品属性选中的id以及子id和价格等信息
	 */
	private HashMap<String, GoodsChildAttrsBean> map = new HashMap<String, GoodsChildAttrsBean>();

	public HashMap<String, GoodsChildAttrsBean> getMap() {
		return map;
	}

	public GoodsPropertyListViewAdapter(Context context,
			List<GoodsAttrsBean> mList,
			HashMap<String, GoodsChildAttrsBean> morenMap) {
		super();
		this.context = context;
		this.mList = mList;
		map = morenMap;
	}

	public void setData(List<GoodsAttrsBean> ormList) {
		if (ormList != null) {
			mList.clear();
			mList.addAll(ormList);
		}
		notifyDataSetInvalidated();
	}

	public void setNewData(List<GoodsAttrsBean> ormList) {
		if (ormList != null) {
			mList.addAll(0, ormList);
			notifyDataSetChanged();
		}
	}

	public void setMoreData(List<GoodsAttrsBean> ormList) {
		if (ormList != null) {
			mList.addAll(ormList);
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public GoodsAttrsBean getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	/**
	 * 属性变化回调监听
	 * 
	 * @param listener
	 */
	public void setOnPropertyListener(OnPropertyListener listener) {
		this.listener = listener;
	}

	public interface OnPropertyListener {
		void notifyPriceChange(int position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		GoodsAttrsBean dynamicPropsEntity = mList.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context,
					R.layout.goods_property_listview_item, null);
			holder.tv_property_title = (TextView) convertView
					.findViewById(R.id.tv_property_title);
			holder.tv_property_pro = (TextView) convertView
					.findViewById(R.id.tv_property_pro);
			holder.mygridview_property = (MyGridView) convertView
					.findViewById(R.id.mygridview_property);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (dynamicPropsEntity != null) {
			holder.tv_property_title.setText(dynamicPropsEntity
					.getGoods_attr_name());
			if (map.get(dynamicPropsEntity.getGoods_attr_name()) != null) {
				holder.tv_property_pro.setText(map.get(
						dynamicPropsEntity.getGoods_attr_name())
						.getGoods_attr_value());
			} else {
				holder.tv_property_pro.setText("");
			}
			// holder.tv_property_pro.setText(dynamicPropsEntity
			// .getProp_value().get(0).getProp_value_name());
			// 商品属性名称
			final String prop_name = dynamicPropsEntity.getGoods_attr_name();
			// 选中的商品属性值
			final TextView tv = holder.tv_property_pro;
			List<GoodsChildAttrsBean> prop_value = dynamicPropsEntity
					.getGoods_attr();
			final List<GoodsChildAttrsBean> getm = prop_value;

			final MyGridAdapterGoods myGridAdapterGoods = new MyGridAdapterGoods(
					prop_value, prop_name);
			holder.mygridview_property.setFocusable(false);
			holder.mygridview_property.setAdapter(myGridAdapterGoods);
			holder.mygridview_property
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							// 将数据传递出去-----
							GoodsChildAttrsBean item = myGridAdapterGoods
									.getItem((int) id);
							tv.setText(item.getGoods_attr_value());
							map.put(prop_name, item);
							listener.notifyPriceChange(position);
							// 将选中的状态默认为红色颜色,其它颜色为非红色。
							// ViewGroup cg = (ViewGroup) view;
							// TextView childAt = (TextView) cg.getChildAt(0);
							// childAt.setEnabled(false);
							for (int i = 0; i < getm.size(); i++) {
								if (i != position) {
									getm.get(i).setIs_default("false");
								} else {
									getm.get(position).setIs_default("true");
								}
							}
							myGridAdapterGoods.notifyDataSetChanged();
						}
					});
		}

		return convertView;
	}

	public HashMap<String, GoodsChildAttrsBean> getHashMap() {
		return map;
	}

	private class ViewHolder {
		private TextView tv_property_title;
		private TextView tv_property_pro;
		private MyGridView mygridview_property;
	}

	/**
	 * @author Administrator
	 * 
	 *         内部gridview数据填充
	 * 
	 */
	public class MyGridAdapterGoods extends BaseAdapter {
		private List<GoodsChildAttrsBean> list;

		public MyGridAdapterGoods(List<GoodsChildAttrsBean> prop_value,
				String prop_name) {
			this.list = prop_value;
		}

		@Override
		public int getCount() {
			return list == null ? 0 : list.size();
		}

		@Override
		public GoodsChildAttrsBean getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MyViewHolder viewHolder;
			GoodsChildAttrsBean propValueEntity = list.get(position);
			if (convertView == null) {
				viewHolder = new MyViewHolder();
				convertView = View.inflate(context,
						R.layout.goods_property_listview_gridview_item, null);
				viewHolder.tv_goods_property = (TextView) convertView
						.findViewById(R.id.tv_goods_property);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (MyViewHolder) convertView.getTag();
			}
			viewHolder.tv_goods_property.setText(propValueEntity
					.getGoods_attr_value());
			if (propValueEntity.getIs_default()!=null&&propValueEntity.getIs_default().equals("true")) {
				viewHolder.tv_goods_property.setEnabled(false);
			} else {
				viewHolder.tv_goods_property.setEnabled(true);
			}
			return convertView;
		}

		private class MyViewHolder {
			private TextView tv_goods_property;
		}

	}
}
