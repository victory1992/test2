package com.dingapp.biz.db.orm;

public class TypeInfoBean {
	private Integer type_id;
	private String type_name;
	private ImageBigAndMinBean type_img;
	public Integer getType_id() {
		return type_id;
	}
	public void setType_id(Integer type_id) {
		this.type_id = type_id;
	}
	public String getType_name() {
		return type_name;
	}
	public void setType_name(String type_name) {
		this.type_name = type_name;
	}
	public ImageBigAndMinBean getType_img() {
		return type_img;
	}
	public void setType_img(ImageBigAndMinBean type_img) {
		this.type_img = type_img;
	}
	
}
