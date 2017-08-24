package com.dingapp.biz.page.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.dingapp.andriod.z20.R;
import com.dingapp.biz.db.orm.FullScreenAdvImgBean;
import com.dingapp.biz.util.ImageViewHWutils;
import com.dingapp.core.util.Constants;
import com.dingapp.imageloader.core.ImageLoader;

public class HomeListAdapter extends BaseAdapter {
	private Context context;
	private List<FullScreenAdvImgBean> list = new ArrayList<FullScreenAdvImgBean>();

	public HomeListAdapter(Context context) {
		this.context = context;
	}

	public void setList(List<FullScreenAdvImgBean> list2) {
		if (list2 != null) {
			list.clear();
			list.addAll(list2);
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_home_listview, null);
			holder.iv = (ImageView) convertView
					.findViewById(R.id.iv_item_home_listview);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		FullScreenAdvImgBean bean = (FullScreenAdvImgBean) getItem(position);
		if (bean != null) {
			LayoutParams params = holder.iv.getLayoutParams();
			int widthS = Constants.getScreenWidth();
			params.width = widthS;
			params.height = widthS / 3;
			holder.iv.setLayoutParams(params);
			ImageLoader.getInstance().displayImage(
					bean.getAdv_img().getDetail_url()
							+ ImageViewHWutils.getWHImageVIew(widthS,
									widthS / 3), holder.iv);
		}
		return convertView;
	}

	class ViewHolder {
		private ImageView iv;
	}
}
