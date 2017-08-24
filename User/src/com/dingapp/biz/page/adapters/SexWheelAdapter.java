package com.dingapp.biz.page.adapters;


import java.util.ArrayList;

import com.dingapp.biz.page.timepackers.WheelAdapter;


public class SexWheelAdapter implements WheelAdapter {
	private ArrayList<String> list;
	public SexWheelAdapter(ArrayList<String> list){
		this.list = list;
	}
	@Override
	public int getItemsCount() {
		
		return list == null?0:list.size();
	}

	@Override
	public String getItem(int index) {
		if(list.size()<=0||list.get(index)==null){
			return "";
		}
		return list.get(index);
	}

	@Override
	public int getMaximumLength() {
		return list.size();
	}

}
