package com.dingapp.biz.db.orm;

public class GoodsChildAttrsBean {
	private int goods_attr_value_id;
	private String goods_attr_value;
	private String is_default;
	public int getGoods_attr_value_id() {
		return goods_attr_value_id;
	}
	public void setGoods_attr_value_id(int goods_attr_value_id) {
		this.goods_attr_value_id = goods_attr_value_id;
	}
	public String getGoods_attr_value() {
		return goods_attr_value;
	}
	public void setGoods_attr_value(String goods_attr_value) {
		this.goods_attr_value = goods_attr_value;
	}
	public String getIs_default() {
		return is_default;
	}
	public void setIs_default(String is_default) {
		this.is_default = is_default;
	}
}
