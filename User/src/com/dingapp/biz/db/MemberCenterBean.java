package com.dingapp.biz.db;

import com.dingapp.biz.db.orm.ImageBigAndMinBean;

public class MemberCenterBean {
	private String mobile;
	private String nick_name;
	private String birth_date;
	private String gender;
	private ImageBigAndMinBean header_profile;
	private int shipping;
	private int receipting;
	private int evaluating;
	private int finished;
	private int paying;
	private int salereturn;
	private int coupon_cnt;
	private int score;
	private int collect_cnt;
	private String is_vip;
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getNick_name() {
		return nick_name;
	}
	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}
	public String getBirth_date() {
		return birth_date;
	}
	public void setBirth_date(String birth_date) {
		this.birth_date = birth_date;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public ImageBigAndMinBean getHeader_profile() {
		return header_profile;
	}
	public void setHeader_profile(ImageBigAndMinBean header_profile) {
		this.header_profile = header_profile;
	}
	public int getShipping() {
		return shipping;
	}
	public void setShipping(int shipping) {
		this.shipping = shipping;
	}
	public int getReceipting() {
		return receipting;
	}
	public void setReceipting(int receipting) {
		this.receipting = receipting;
	}
	public int getEvaluating() {
		return evaluating;
	}
	public void setEvaluating(int evaluating) {
		this.evaluating = evaluating;
	}
	public int getFinished() {
		return finished;
	}
	public void setFinished(int finished) {
		this.finished = finished;
	}
	public int getPaying() {
		return paying;
	}
	public void setPaying(int paying) {
		this.paying = paying;
	}
	public int getSalereturn() {
		return salereturn;
	}
	public void setSalereturn(int salereturn) {
		this.salereturn = salereturn;
	}
	public int getCoupon_cnt() {
		return coupon_cnt;
	}
	public void setCoupon_cnt(int coupon_cnt) {
		this.coupon_cnt = coupon_cnt;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getCollect_cnt() {
		return collect_cnt;
	}
	public void setCollect_cnt(int collect_cnt) {
		this.collect_cnt = collect_cnt;
	}
	public String getIs_vip() {
		return is_vip;
	}
	public void setIs_vip(String is_vip) {
		this.is_vip = is_vip;
	}
	
}
