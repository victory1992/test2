package com.dingapp.biz.db.orm;

import java.util.List;

public class GoodsTypeBean {
	private int type_id;
	private String type_name;
	private ImageBigAndMinBean type_pic;
	private List<GoodsChildTypeBean>child_type_info;
	public int getType_id() {
		return type_id;
	}
	public void setType_id(int type_id) {
		this.type_id = type_id;
	}
	public String getType_name() {
		return type_name;
	}
	public void setType_name(String type_name) {
		this.type_name = type_name;
	}
	public List<GoodsChildTypeBean> getChild_type_info() {
		return child_type_info;
	}
	public void setChild_type_info(List<GoodsChildTypeBean> child_type_info) {
		this.child_type_info = child_type_info;
	}
	public ImageBigAndMinBean getType_pic() {
		return type_pic;
	}
	public void setType_pic(ImageBigAndMinBean type_pic) {
		this.type_pic = type_pic;
	}
	
}
