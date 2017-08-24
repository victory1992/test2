package com.dingapp.biz.db.orm;

import java.util.List;

public class CommentOrderBean {
	private String goods_pic;
	private String goods_title;
	private int score;
	private int goods_id;
	private String content;
	private List<String> native_pic_url;
	private List<String> net_pic_url;
	public String getGoods_pic() {
		return goods_pic;
	}
	public void setGoods_pic(String goods_pic) {
		this.goods_pic = goods_pic;
	}
	public String getGoods_title() {
		return goods_title;
	}
	public void setGoods_title(String goods_title) {
		this.goods_title = goods_title;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(int goods_id) {
		this.goods_id = goods_id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<String> getNative_pic_url() {
		return native_pic_url;
	}
	public void setNative_pic_url(List<String> native_pic_url) {
		this.native_pic_url = native_pic_url;
	}
	public List<String> getNet_pic_url() {
		return net_pic_url;
	}
	public void setNet_pic_url(List<String> net_pic_url) {
		this.net_pic_url = net_pic_url;
	}
	
}
