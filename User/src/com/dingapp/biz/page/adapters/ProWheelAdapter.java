package com.dingapp.biz.page.adapters;

import java.util.List;

import com.dingapp.biz.db.dao.DBDao.Province;
import com.dingapp.biz.page.timepackers.WheelAdapter;

public class ProWheelAdapter implements WheelAdapter {
	private List<Province> list;

	public ProWheelAdapter(List<Province> list) {
		this.list = list;
	}

	@Override
	public int getItemsCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public String getItem(int index) {
		if (list.size() <= 0 || list.get(index) == null) {
			return "";
		}
		return list.get(index).region_name;
	}

	@Override
	public int getMaximumLength() {
		return list.size();
	}
	
	public String getID(int index){
		if(list!=null&&list.get(index)!=null){
			return list.get(index).region_id;
		}
		return null;
	}
}
