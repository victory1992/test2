package com.dingapp.biz.page.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dingapp.andriod.z20.R;
import com.dingapp.biz.db.orm.TypeInfoBean;

public class DesignerChooseListAdapter extends BaseAdapter {
	private Context context;
	private List<TypeInfoBean> list = new ArrayList<TypeInfoBean>();
	public DesignerChooseListAdapter(Context context){
		this.context = context;
	}
	public void setList(List<TypeInfoBean> list2){
		if(list2!=null){
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_designer_choose_dialog, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_item_designer_choose_dialog);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		TypeInfoBean bean = (TypeInfoBean) getItem(arg0);
		if(bean!=null){
			holder.tv_name.setText(bean.getType_name());
		}
		return convertView;
	}
	static class ViewHolder{
		private TextView tv_name;
	}
}
