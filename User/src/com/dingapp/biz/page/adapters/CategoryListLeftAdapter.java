package com.dingapp.biz.page.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dingapp.andriod.z20.R;
import com.dingapp.biz.db.orm.GoodsTypeBean;

public class CategoryListLeftAdapter extends BaseAdapter {
	private int selectedPosition;
	private List<GoodsTypeBean> mList;
	private Context context;

	public CategoryListLeftAdapter(Context context, List<GoodsTypeBean> typeList) {
		super();
		this.context = context;
		this.mList = typeList;
		this.selectedPosition = 0;
	}

	public void setData(List<GoodsTypeBean> ormList) {
		if (ormList != null) {
			mList.clear();
			mList.addAll(ormList);
		}
		notifyDataSetInvalidated();
	}

	public void setNewData(List<GoodsTypeBean> ormList) {
		if (ormList != null) {
			mList.addAll(0, ormList);
			notifyDataSetChanged();
		}
	}

	public void setMoreData(List<GoodsTypeBean> ormList) {
		if (ormList != null) {
			mList.addAll(ormList);
			notifyDataSetChanged();
		}
	}

	public void setSelectedPosition(int selectedPosition) {
		this.selectedPosition = selectedPosition;
	}

	public int getSelectedPosition() {
		return this.selectedPosition;
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public GoodsTypeBean getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = View.inflate(context, R.layout.category_listview_left_item,
					null);
			holder.tv = (TextView) view.findViewById(R.id.tv_category_list);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		GoodsTypeBean bean = getItem(position);
		holder.tv.setText(bean.getType_name());
		if (selectedPosition == position) {
			view.setEnabled(true);
			holder.tv.setTextColor(0xfff66e00);
		} else {
			view.setEnabled(false);
			holder.tv.setTextColor(0xff808080);
		}
		return view;

	}

	class ViewHolder {
		private TextView tv;
	}
}
