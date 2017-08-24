package com.dingapp.biz.db.orm;

import java.util.List;

public class MemberLabelListBean {
	private List<MemberLabelBean> labels;
	private String next_exsit;
	public List<MemberLabelBean> getLabels() {
		return labels;
	}
	public void setLabels(List<MemberLabelBean> labels) {
		this.labels = labels;
	}
	public String getNext_exsit() {
		return next_exsit;
	}
	public void setNext_exsit(String next_exsit) {
		this.next_exsit = next_exsit;
	}
	
}
