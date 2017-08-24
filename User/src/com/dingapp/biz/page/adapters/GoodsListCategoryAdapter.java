package com.dingapp.biz.page.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dingapp.andriod.z20.R;
import com.dingapp.biz.db.orm.GoodsTypeBean;

public class GoodsListCategoryAdapter extends BaseAdapter {
	private List<GoodsTypeBean> mList = new ArrayList<GoodsTypeBean>();
	private Context context;
	private int selectedPosition;

	public GoodsListCategoryAdapter(Context context, List<GoodsTypeBean> mList) {
		super();
		this.mList = mList;
		this.context = context;
		/*
		 * if (mList != null && mList.size() > 0) {
		 * mList.get(selectedPosition).getCategory_name();
		 * mList.get(selectedPosition).getSub_categories()
		 * .get(selectedPosition).getCategory_name(); }
		 */
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}

	public void setSelectedPosition(int selectedPosition) {
		this.selectedPosition = selectedPosition;
	}

	public void setData(List<GoodsTypeBean> ormList) {
		mList.clear();
		if (ormList != null) {
			mList.addAll(ormList);
		}
		notifyDataSetChanged();
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

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Object getItem(int arg0) {
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
			arg1 = View.inflate(context,
					R.layout.goods_list_category_list_item, null);
			holder.tv_goods_category = (TextView) arg1
					.findViewById(R.id.tv_goods_category);
			holder.rl_parent_category = (RelativeLayout) arg1
					.findViewById(R.id.rl_parent_category);
			arg1.setTag(holder);
		} else {
			holder = (ViewHolder) arg1.getTag();
		}
		if (selectedPosition == arg0) {
			holder.rl_parent_category.setBackgroundColor(0xffffffff);
			holder.tv_goods_category.setTextColor(0xffEF4D1C);
		} else {
			holder.rl_parent_category.setBackgroundColor(0xfff3f4f6);
			holder.tv_goods_category.setTextColor(0xff666666);
		}
		holder.tv_goods_category.setText(mList.get(arg0).getType_name());
		return arg1;
	}

	private class ViewHolder {
		private RelativeLayout rl_parent_category;
		private TextView tv_goods_category;
	}

}
