package com.dingapp.biz.db.orm;

import java.util.List;

public class GoodsAttrsBean {
	private List<GoodsChildAttrsBean> goods_attr;
	private String goods_attr_name;
	public List<GoodsChildAttrsBean> getGoods_attr() {
		return goods_attr;
	}
	public void setGoods_attr(List<GoodsChildAttrsBean> goods_attr) {
		this.goods_attr = goods_attr;
	}
	public String getGoods_attr_name() {
		return goods_attr_name;
	}
	public void setGoods_attr_name(String goods_attr_name) {
		this.goods_attr_name = goods_attr_name;
	}
	
}
