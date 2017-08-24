package com.dingapp.biz.db.orm;

import java.util.List;

public class IncomePrjStatListBean {
	private String day_desc;
	private List<PrjStatBean> prj_stat_list;
	public String getDay_desc() {
		return day_desc;
	}
	public void setDay_desc(String day_desc) {
		this.day_desc = day_desc;
	}
	public List<PrjStatBean> getPrj_stat_list() {
		return prj_stat_list;
	}
	public void setPrj_stat_list(List<PrjStatBean> prj_stat_list) {
		this.prj_stat_list = prj_stat_list;
	}
	
}
