package com.dingapp.biz.db.orm;

public class WxAuthBean {
	private String open_id;
	private String union_id;
	private String wx_nick_name;
	private Double reminder_cash;
	public String getOpen_id() {
		return open_id;
	}
	public void setOpen_id(String open_id) {
		this.open_id = open_id;
	}
	public String getUnion_id() {
		return union_id;
	}
	public void setUnion_id(String union_id) {
		this.union_id = union_id;
	}
	public String getWx_nick_name() {
		return wx_nick_name;
	}
	public void setWx_nick_name(String wx_nick_name) {
		this.wx_nick_name = wx_nick_name;
	}
	public Double getReminder_cash() {
		return reminder_cash;
	}
	public void setReminder_cash(Double reminder_cash) {
		this.reminder_cash = reminder_cash;
	}
	
}
