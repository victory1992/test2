package com.dingapp.biz.page.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dingapp.andriod.z20.R;
import com.dingapp.biz.db.orm.TypeInfoBean;
import com.dingapp.imageloader.core.ImageLoader;

public class HomeTypeInfoAdapter extends BaseAdapter {
	private Context context;
	private List<TypeInfoBean> mList = null;

	public HomeTypeInfoAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	public void setData(List<TypeInfoBean> ormList) {
		if (ormList != null) {
			mList = ormList;
		}
		notifyDataSetChanged();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup containner) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.item_home_type, null);
			holder.tv = (TextView) convertView.findViewById(R.id.tv_home_type);
			holder.iv = (ImageView) convertView.findViewById(R.id.iv_home_type);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		TypeInfoBean bean = (TypeInfoBean) getItem(position);
		if (bean != null) {
			holder.tv.setText(bean.getType_name());
			if (bean.getType_name() != null && bean.getType_name().equals("全部")) {
				holder.iv.setImageResource(R.drawable.iv_home_more);
			} else {
				ImageLoader.getInstance().displayImage(
						bean.getType_img().getDetail_url(), holder.iv);
			}
		}
		return convertView;
	}

	private class ViewHolder {
		private TextView tv;
		private ImageView iv;
	}
}
