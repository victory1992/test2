package com.dingapp.biz.db.orm;

public class GoodsChildBean {
	private int goods_id;
	private String goods_name;
	private ImageBigAndMinBean goods_pic;
	private Double goods_price;
	private int goods_score;
	private String is_vip;
	private Double vip_price;
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
	public String getIs_vip() {
		return is_vip;
	}
	public void setIs_vip(String is_vip) {
		this.is_vip = is_vip;
	}
	public Double getVip_price() {
		return vip_price;
	}
	public void setVip_price(Double vip_price) {
		this.vip_price = vip_price;
	}
	public int getGoods_score() {
		return goods_score;
	}
	public void setGoods_score(int goods_score) {
		this.goods_score = goods_score;
	}
}
