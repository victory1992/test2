package com.dingapp.biz.db.orm;

import java.util.List;

public class OrederGoodsItemBean {
	private List<OrderPrdAttrsBean> goods_attrs;
	private int goods_id;
	private String goods_name;
	private ImageBigAndMinBean goods_pic;
	private Double goods_price;
	private int goods_cnt;
	private int design_info_id;
	private int goods_score;
	
	public List<OrderPrdAttrsBean> getGoods_attrs() {
		return goods_attrs;
	}
	public void setGoods_attrs(List<OrderPrdAttrsBean> goods_attrs) {
		this.goods_attrs = goods_attrs;
	}
	public int getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(int goods_id) {
		this.goods_id = goods_id;
	}
	public String getGoods_name() {
		return goods_name;
	}
	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}
	public ImageBigAndMinBean getGoods_pic() {
		return goods_pic;
	}
	public void setGoods_pic(ImageBigAndMinBean goods_pic) {
		this.goods_pic = goods_pic;
	}
	public Double getGoods_price() {
		return goods_price;
	}
	public void setGoods_price(Double goods_price) {
		this.goods_price = goods_price;
	}
	public int getGoods_cnt() {
		return goods_cnt;
	}
	public void setGoods_cnt(int goods_cnt) {
		this.goods_cnt = goods_cnt;
	}
	public int getDesign_info_id() {
		return design_info_id;
	}
	public void setDesign_info_id(int design_info_id) {
		this.design_info_id = design_info_id;
	}
	public int getGoods_score() {
		return goods_score;
	}
	public void setGoods_score(int goods_score) {
		this.goods_score = goods_score;
	}
	
}
