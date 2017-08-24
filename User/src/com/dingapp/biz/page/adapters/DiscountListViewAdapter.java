package com.dingapp.biz.page.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.dingapp.andriod.z20.R;
import com.dingapp.biz.db.orm.CouponBean;

public class DiscountListViewAdapter extends BaseAdapter {
	private Context context;
	private List<CouponBean> mList = new ArrayList<CouponBean>();
	private int selectedPosition;

	public DiscountListViewAdapter(Context context) {
		super();
		this.context = context;
	}

	public void setData(List<CouponBean> list) {
		if (list != null) {
			mList.clear();
			mList.addAll(list);
		}
		notifyDataSetChanged();
	}

	public void setSelectedPosition(int index) {
		selectedPosition = index;
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public CouponBean getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View concertView, ViewGroup container) {
		ViewHolder holder;
		if (concertView == null) {
			holder = new ViewHolder();
			concertView = View.inflate(context,R.layout.discount_dialog_listview_item, null);
			holder.tv_discount_money = (TextView) concertView
					.findViewById(R.id.tv_discount_money);
			holder.cb_discount_selected = (CheckBox) concertView
					.findViewById(R.id.cb_discount_selected);
			holder.tv_discount_left_day = (TextView) concertView
					.findViewById(R.id.tv_discount_left_day);
			concertView.setTag(holder);
		} else {
			holder = (ViewHolder) concertView.getTag();
		}
		if (position == selectedPosition) {
			holder.cb_discount_selected.setChecked(true);
		} else {
			holder.cb_discount_selected.setChecked(false);
		}
		holder.tv_discount_left_day.setText(mList.get(position).getLeft_day()
				+ "后过期");
		holder.tv_discount_money.setText((int) mList.get(position).getMoney()
				+ "元优惠券");
		return concertView;
	}

	private class ViewHolder {
		private TextView tv_discount_money;
		private CheckBox cb_discount_selected;
		private TextView tv_discount_left_day;

	}
}
