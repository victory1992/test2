package com.dingapp.biz.db.orm;

public class GoodsFavBean {
	private ImageBigAndMinBean goods_img;
	private int goods_id;
	private Double goods_price;
	private String goods_title;
	private String collect_time;
	private int design_info_id;

	public ImageBigAndMinBean getGoods_img() {
		return goods_img;
	}

	public void setGoods_img(ImageBigAndMinBean goods_img) {
		this.goods_img = goods_img;
	}

	public int getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(int goods_id) {
		this.goods_id = goods_id;
	}

	public Double getGoods_price() {
		return goods_price;
	}

	public void setGoods_price(Double goods_price) {
		this.goods_price = goods_price;
	}

	public String getGoods_title() {
		return goods_title;
	}

	public void setGoods_title(String goods_title) {
		this.goods_title = goods_title;
	}

	public String getCollect_time() {
		return collect_time;
	}

	public void setCollect_time(String collect_time) {
		this.collect_time = collect_time;
	}

	public int getDesign_info_id() {
		return design_info_id;
	}

	public void setDesign_info_id(int design_info_id) {
		this.design_info_id = design_info_id;
	}

}
