package com.dingapp.biz.page.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.dingapp.andriod.z20.R;
import com.dingapp.biz.db.orm.AddressBean;
import com.dingapp.core.app.BaseFragment;

public class SelectAddressAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<AddressBean> mList = new ArrayList<AddressBean>();
	private int selectedPosition;
	private BaseFragment fm;

	public SelectAddressAdapter(Context context, BaseFragment fm) {
		super();
		this.context = context;
		this.fm = fm;
	}

	public void setData(ArrayList<AddressBean> ormList) {
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
	public AddressBean getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context,
					R.layout.select_address_item, null);
			holder.cb_select_address = (CheckBox) convertView
					.findViewById(R.id.cb_select_address);
			holder.tv_school_address_item = (TextView) convertView
					.findViewById(R.id.tv_school_address_item);
			holder.tv_normal = (TextView) convertView.findViewById(R.id.tv_normal);
			holder.tv_person_mobile = (TextView) convertView
					.findViewById(R.id.tv_person_mobile);
			holder.tv_person_name = (TextView) convertView
					.findViewById(R.id.tv_person_name);
			holder.iv_person_address_edit = (ImageView) convertView
					.findViewById(R.id.iv_person_address_edit);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (selectedPosition == position) {

			holder.cb_select_address.setChecked(true);
		} else {
			holder.cb_select_address.setChecked(false);

		}
		final AddressBean bean = (AddressBean) getItem(position);
		if (bean != null) {
			holder.tv_normal
					.setVisibility(bean.getDefault_tag().equals("true") ? View.VISIBLE
							: View.GONE);
			holder.tv_person_mobile.setText(bean.getReceiver_mobile());
			holder.tv_person_name.setText(bean.getReceiver_name());
			holder.tv_school_address_item.setText(bean.getProvince_name()
					+ bean.getCity_name() + bean.getCounty_name()
					+ bean.getAddress());
		}
		holder.iv_person_address_edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("address_id", bean.getAddress_id() + "");
				bundle.putString("city_name", bean.getCity_name());
				bundle.putString("receiver_mobile", bean.getReceiver_mobile());
				bundle.putString("receiver_name", bean.getReceiver_name());
				bundle.putString("default_tag", bean.getDefault_tag());
				bundle.putString("address", bean.getAddress());
				bundle.putString("province_name", bean.getProvince_name());
				bundle.putString("county_name", bean.getCounty_name());
				// 编辑地址
				fm.openPage("member_center_personal_data_add_address", bundle,
						false);
			}
		});
		return convertView;
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}

	public void setSelectedPosition(int selectedPosition) {
		this.selectedPosition = selectedPosition;
	}

	private class ViewHolder {
		private CheckBox cb_select_address;
		private TextView tv_school_address_item;
		private TextView tv_normal;
		private TextView tv_person_mobile;
		private TextView tv_person_name;
		private ImageView iv_person_address_edit;
	}

}
