package com.dingapp.biz.db.orm;

import java.util.List;

public class GoodsChildTypeBean {
	private int child_type_id;
	private String child_type_name;
	private List<ChildChildTypeInfoBean> child_child_type_info;

	public int getChild_type_id() {
		return child_type_id;
	}

	public void setChild_type_id(int child_type_id) {
		this.child_type_id = child_type_id;
	}

	public String getChild_type_name() {
		return child_type_name;
	}

	public void setChild_type_name(String child_type_name) {
		this.child_type_name = child_type_name;
	}

	public List<ChildChildTypeInfoBean> getChild_child_type_info() {
		return child_child_type_info;
	}

	public void setChild_child_type_info(
			List<ChildChildTypeInfoBean> child_child_type_info) {
		this.child_child_type_info = child_child_type_info;
	}

}
