package com.dingapp.biz.db.orm;

import java.util.List;

public class IncomeDayStatListBean {
	private String month;
	private List<DayStatBean> day_stat_list;
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public List<DayStatBean> getDay_stat_list() {
		return day_stat_list;
	}
	public void setDay_stat_list(List<DayStatBean> day_stat_list) {
		this.day_stat_list = day_stat_list;
	}
	
}
