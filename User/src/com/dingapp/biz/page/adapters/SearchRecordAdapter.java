package com.dingapp.biz.page.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dingapp.andriod.z20.R;

public class SearchRecordAdapter extends BaseAdapter{
	private Context context;
	private List<String> list;
	public SearchRecordAdapter(Context context,List<String> list){
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list == null?0:list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_search_record, null);
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_item_search_record);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_title.setText(list.get(arg0));
		return convertView;
	}
	static class ViewHolder{
		private TextView tv_title;
	}
}
