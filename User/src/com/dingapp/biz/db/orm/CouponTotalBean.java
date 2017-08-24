package com.dingapp.biz.db.orm;

import java.util.List;

public class CouponTotalBean {
	private Integer available_cnt;
	private Integer used_cnt;
	private Integer expired_cnt;
	private List<CouponBean> coupons;
	
	public Integer getAvailable_cnt() {
		return available_cnt;
	}
	public void setAvailable_cnt(Integer available_cnt) {
		this.available_cnt = available_cnt;
	}
	public Integer getUsed_cnt() {
		return used_cnt;
	}
	public void setUsed_cnt(Integer used_cnt) {
		this.used_cnt = used_cnt;
	}
	public Integer getExpired_cnt() {
		return expired_cnt;
	}
	public void setExpired_cnt(Integer expired_cnt) {
		this.expired_cnt = expired_cnt;
	}
	public List<CouponBean> getCoupons() {
		return coupons;
	}
	public void setCoupons(List<CouponBean> coupons) {
		this.coupons = coupons;
	}
	
}
