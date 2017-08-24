package com.dingapp.biz.db.orm;

import java.util.List;

/**
 * 商品详情
 */
public class GoodsDetailBean {
	private int comment_cnt;
	private int cart_cnt;
	private int goods_id;
	private String goods_name;
	private String create_time;
	private List<ImageBigAndMinBean> goods_pics;
	private List<GoodsAttrsBean> goods_attrs;
	private Double goods_price;
	private int goods_sale_cnt;
	private String goods_detail;
	private int goods_number;
	private String if_collect;
	private double goods_weight;
	private String share_url;
	private String is_vip;
	private double vip_price;
	private int send_score;
	public int getComment_cnt() {
		return comment_cnt;
	}

	public void setComment_cnt(int comment_cnt) {
		this.comment_cnt = comment_cnt;
	}


	public int getCart_cnt() {
		return cart_cnt;
	}

	public void setCart_cnt(int cart_cnt) {
		this.cart_cnt = cart_cnt;
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

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public List<ImageBigAndMinBean> getGoods_pics() {
		return goods_pics;
	}

	public void setGoods_pics(List<ImageBigAndMinBean> goods_pics) {
		this.goods_pics = goods_pics;
	}

	public List<GoodsAttrsBean> getGoods_attrs() {
		return goods_attrs;
	}

	public void setGoods_attrs(List<GoodsAttrsBean> goods_attrs) {
		this.goods_attrs = goods_attrs;
	}

	public Double getGoods_price() {
		return goods_price;
	}

	public void setGoods_price(Double goods_price) {
		this.goods_price = goods_price;
	}

	public int getGoods_sale_cnt() {
		return goods_sale_cnt;
	}

	public void setGoods_sale_cnt(int goods_sale_cnt) {
		this.goods_sale_cnt = goods_sale_cnt;
	}

	public String getGoods_detail() {
		return goods_detail;
	}

	public void setGoods_detail(String goods_detail) {
		this.goods_detail = goods_detail;
	}

	public int getGoods_number() {
		return goods_number;
	}

	public void setGoods_number(int goods_number) {
		this.goods_number = goods_number;
	}

	public String getIf_collect() {
		return if_collect;
	}

	public void setIf_collect(String if_collect) {
		this.if_collect = if_collect;
	}

	public double getGoods_weight() {
		return goods_weight;
	}

	public void setGoods_weight(double goods_weight) {
		this.goods_weight = goods_weight;
	}

	public String getShare_url() {
		return share_url;
	}

	public void setShare_url(String share_url) {
		this.share_url = share_url;
	}

	public String getIs_vip() {
		return is_vip;
	}

	public void setIs_vip(String is_vip) {
		this.is_vip = is_vip;
	}

	public double getVip_price() {
		return vip_price;
	}

	public void setVip_price(double vip_price) {
		this.vip_price = vip_price;
	}

	public int getSend_score() {
		return send_score;
	}

	public void setSend_score(int send_score) {
		this.send_score = send_score;
	}
	
}
