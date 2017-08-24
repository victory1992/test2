package com.dingapp.biz.page.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dingapp.andriod.z20.R;
import com.dingapp.biz.db.orm.AddressBean;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.UIUtil;

public class AddressAdapter extends BaseAdapter {
	private List<AddressBean> mList = new ArrayList<AddressBean>();
	private Context context;
	private BaseFragment fg;

	public AddressAdapter(Context context, BaseFragment fg) {
		this.context = context;
		this.fg = fg;
	}

	public void setData(List<AddressBean> data) {
		if (data != null) {
			mList.clear();
			mList.addAll(data);
		}
		notifyDataSetInvalidated();
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
	public View getView(int position, View convertView, ViewGroup container) {
		final AddressBean addressBean = mList.get(position);
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View
					.inflate(context,R.layout.member_center_address_item,
							null);
			holder.iv_address_edit = (ImageView) convertView
					.findViewById(R.id.iv_address_edit);
			holder.tv_user_name = (TextView) convertView
					.findViewById(R.id.tv_user_name);
			holder.tv_user_mobile = (TextView) convertView
					.findViewById(R.id.tv_user_mobile);
			holder.tv_mo_ren = (TextView) convertView.findViewById(R.id.tv_mo_ren);
			holder.tv_school_address_item = (TextView) convertView
					.findViewById(R.id.tv_school_address_item);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_school_address_item.setText("详细地址："+addressBean.getProvince_name()
				+ addressBean.getCity_name() + addressBean.getCounty_name()
				+ addressBean.getAddress());
		holder.tv_user_name.setText("姓名："+addressBean.getReceiver_name());
		holder.tv_user_mobile.setText("手机号码："+addressBean.getReceiver_mobile());
		holder.tv_mo_ren.setVisibility(View.VISIBLE);
		if (addressBean.getDefault_tag() != null
				&& addressBean.getDefault_tag().equals("true")) {
			holder.tv_mo_ren.setVisibility(View.VISIBLE);
		} else {
			holder.tv_mo_ren.setVisibility(View.GONE);
		}
		holder.iv_address_edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				UIUtil.showToast(context, "编辑地址");
				// 将地址信息传递过去
				Bundle bundle = new Bundle();
				bundle.putString("address_id", addressBean.getAddress_id() + "");
				bundle.putString("province_name",
						addressBean.getProvince_name());
				bundle.putString("city_name", addressBean.getCity_name());
				bundle.putString("county_name", addressBean.getCounty_name());
				bundle.putString("address", addressBean.getAddress());
				bundle.putString("receiver_name",
						addressBean.getReceiver_name());
				bundle.putString("receiver_mobile",
						addressBean.getReceiver_mobile());
				bundle.putString("default_tag", addressBean.getDefault_tag());
				fg.openPage("member_center_personal_data_add_address", bundle,
						false);
			}
		});
		return convertView;
	}

	private class ViewHolder {
		private TextView tv_user_name;
		private TextView tv_user_mobile;
		private ImageView iv_address_edit;
		private TextView tv_mo_ren;
		private TextView tv_school_address_item;
	}
}
