package com.dingapp.biz.db.orm;import java.util.List;public class ScoreGoodsDetail {	private int goods_id;	private String goods_name;	private String create_time;	private List<ImageBigAndMinBean> goods_pics;	private List<GoodsAttrsBean> goods_attrs;	private Double goods_price;	private int goods_sale_cnt;	private String goods_detail;	private int goods_number;	private double goods_weight;	private String share_url;	private int goods_score;	public int getGoods_id() {		return goods_id;	}	public void setGoods_id(int goods_id) {		this.goods_id = goods_id;	}	public String getGoods_name() {		return goods_name;	}	public void setGoods_name(String goods_name) {		this.goods_name = goods_name;	}	public String getCreate_time() {		return create_time;	}	public void setCreate_time(String create_time) {		this.create_time = create_time;	}	public List<ImageBigAndMinBean> getGoods_pics() {		return goods_pics;	}	public void setGoods_pics(List<ImageBigAndMinBean> goods_pics) {		this.goods_pics = goods_pics;	}	public List<GoodsAttrsBean> getGoods_attrs() {		return goods_attrs;	}	public void setGoods_attrs(List<GoodsAttrsBean> goods_attrs) {		this.goods_attrs = goods_attrs;	}	public Double getGoods_price() {		return goods_price;	}	public void setGoods_price(Double goods_price) {		this.goods_price = goods_price;	}	public int getGoods_sale_cnt() {		return goods_sale_cnt;	}	public void setGoods_sale_cnt(int goods_sale_cnt) {		this.goods_sale_cnt = goods_sale_cnt;	}	public String getGoods_detail() {		return goods_detail;	}	public void setGoods_detail(String goods_detail) {		this.goods_detail = goods_detail;	}	public int getGoods_number() {		return goods_number;	}	public void setGoods_number(int goods_number) {		this.goods_number = goods_number;	}	public double getGoods_weight() {		return goods_weight;	}	public void setGoods_weight(double goods_weight) {		this.goods_weight = goods_weight;	}	public String getShare_url() {		return share_url;	}	public void setShare_url(String share_url) {		this.share_url = share_url;	}	public int getGoods_score() {		return goods_score;	}	public void setGoods_score(int goods_score) {		this.goods_score = goods_score;	}}