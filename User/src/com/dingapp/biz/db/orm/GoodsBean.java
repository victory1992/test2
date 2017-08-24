package com.dingapp.biz.db.orm;

import java.util.List;

public class GoodsBean {
	private String type_name;
	private int type_id;
	private List<GoodsChildBean> goods_info;
	public String getType_name() {
		return type_name;
	}
	public void setType_name(String type_name) {
		this.type_name = type_name;
	}
	public int getType_id() {
		return type_id;
	}
	public void setType_id(int type_id) {
		this.type_id = type_id;
	}
	public List<GoodsChildBean> getGoods_info() {
		return goods_info;
	}
	public void setGoods_info(List<GoodsChildBean> goods_info) {
		this.goods_info = goods_info;
	}
	
}
