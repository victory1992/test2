package com.dingapp.biz.page.adapters;

import java.util.List;

import com.dingapp.biz.db.dao.DBDao.Area;
import com.dingapp.biz.page.timepackers.WheelAdapter;

public class AreaWheelAdapter implements WheelAdapter {
	private List<Area> list;

	public AreaWheelAdapter(List<Area> list) {
		this.list = list;
	}

	@Override
	public int getItemsCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public String getItem(int index) {
		if(list==null){
			return "";
		}
		if (list.size() <= 0 || list.get(index) == null) {
			return "";
		}
		return list.get(index).region_name;
	}

	@Override
	public int getMaximumLength() {
		return list==null?0:list.size();
	}
	public String getID(int index){
		if(list!=null&&list.get(index)!=null){
			return list.get(index).region_id;
		}
		return null;
	}
}
