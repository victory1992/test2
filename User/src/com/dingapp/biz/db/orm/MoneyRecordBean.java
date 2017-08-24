package com.dingapp.biz.db.orm;

import java.util.List;

public class MoneyRecordBean {
	private String month_desc;
	private List<MoneyRecordItemBean> rec_list;
	public String getMonth_desc() {
		return month_desc;
	}
	public void setMonth_desc(String month_desc) {
		this.month_desc = month_desc;
	}
	public List<MoneyRecordItemBean> getRec_list() {
		return rec_list;
	}
	public void setRec_list(List<MoneyRecordItemBean> rec_list) {
		this.rec_list = rec_list;
	}
	
}
