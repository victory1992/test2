package com.dingapp.biz.page.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dingapp.andriod.z20.R;
import com.dingapp.biz.db.orm.GoodsChildTypeBean;

public class GoodsListCategoryRightAdapter extends BaseAdapter {
	private List<GoodsChildTypeBean> mList = new ArrayList<GoodsChildTypeBean>();
	private Context context;
	private int selectedPosition;

	public GoodsListCategoryRightAdapter(Context context,
			List<GoodsChildTypeBean> list) {
		this.context = context;
		if (list != null) {
			this.mList.addAll(list);
		}
	}

	public void setData(List<GoodsChildTypeBean> ormList) {
		mList.clear();
		if (ormList != null) {
			mList.clear();
			mList.addAll(ormList);
		}
		notifyDataSetChanged();
	}

	public void setNewData(List<GoodsChildTypeBean> ormList) {
		if (ormList != null) {
			mList.addAll(0, ormList);
			notifyDataSetChanged();
		}
	}

	public void setMoreData(List<GoodsChildTypeBean> ormList) {
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
	public GoodsChildTypeBean getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ViewHolder holder = null;
		if (arg1 == null) {
			holder = new ViewHolder();
			arg1 = View.inflate(context, R.layout.goods_list_category_list_item, null);
			holder.tv_goods_category = (TextView) arg1.findViewById(R.id.
					tv_goods_category);
			arg1.setTag(holder);
		} else {
			holder = (ViewHolder) arg1.getTag();
		}
		if (selectedPosition == arg0) {
			holder.tv_goods_category.setTextColor(Color.parseColor("#EF4D1C"));
		} else {
			holder.tv_goods_category.setTextColor(Color.parseColor("#666666"));
		}
		holder.tv_goods_category.setText(mList.get(arg0).getChild_type_name());
		return arg1;
	}

	private class ViewHolder {
		private TextView tv_goods_category;
	}

	public void setSelectedPosition(int position) {
		this.selectedPosition = position;
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}
}
