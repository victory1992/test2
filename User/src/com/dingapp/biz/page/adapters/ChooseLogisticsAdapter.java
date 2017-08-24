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
import com.dingapp.biz.db.orm.ExpressListBean;

public class ChooseLogisticsAdapter extends BaseAdapter {

	private Context context;
	private List<ExpressListBean> mList = new ArrayList<ExpressListBean>();
	private int selectedPosition;

	public ChooseLogisticsAdapter(Context context) {
		super();
		this.context = context;
	}

	public void setData(List<ExpressListBean> list) {
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
	public Object getItem(int arg0) {
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
			concertView = View.inflate(context, R.layout.item_choose_logistics,
					null);
			holder.tv_name = (TextView) concertView
					.findViewById(R.id.tv_item_choose_logistics);
			holder.tv_extra_msg = (TextView) concertView
					.findViewById(R.id.tv_item_choose_logistics);
			holder.cb_selected = (CheckBox) concertView
					.findViewById(R.id.cb_selected);
			concertView.setTag(holder);
		} else {
			holder = (ViewHolder) concertView.getTag();
		}
		if (position == selectedPosition) {
			holder.cb_selected.setChecked(true);
		} else {
			holder.cb_selected.setChecked(false);
		}
		ExpressListBean bean = (ExpressListBean) getItem(position);
		if (bean != null) {
			holder.tv_name.setText(bean.getExpress_name());
		}
		return concertView;
	}

	private class ViewHolder {
		private TextView tv_name;
		private CheckBox cb_selected;
		private TextView tv_extra_msg;

	}
}
