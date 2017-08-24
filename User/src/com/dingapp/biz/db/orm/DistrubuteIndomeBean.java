package com.dingapp.biz.db.orm;

public class DistrubuteIndomeBean {
	private int team_cnt;
	private int order_cnt;
	private Double total_money;
	private Double available_money;
	private Double money_limit;
	private String can_raise_cash;
	private String share_url;
	public int getTeam_cnt() {
		return team_cnt;
	}
	public void setTeam_cnt(int team_cnt) {
		this.team_cnt = team_cnt;
	}
	public int getOrder_cnt() {
		return order_cnt;
	}
	public void setOrder_cnt(int order_cnt) {
		this.order_cnt = order_cnt;
	}
	public Double getTotal_money() {
		return total_money;
	}
	public void setTotal_money(Double total_money) {
		this.total_money = total_money;
	}
	public Double getAvailable_money() {
		return available_money;
	}
	public void setAvailable_money(Double available_money) {
		this.available_money = available_money;
	}
	public Double getMoney_limit() {
		return money_limit;
	}
	public void setMoney_limit(Double money_limit) {
		this.money_limit = money_limit;
	}
	public String getCan_raise_cash() {
		return can_raise_cash;
	}
	public void setCan_raise_cash(String can_raise_cash) {
		this.can_raise_cash = can_raise_cash;
	}
	public String getShare_url() {
		return share_url;
	}
	public void setShare_url(String share_url) {
		this.share_url = share_url;
	}
	
}
