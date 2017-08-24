package com.dingapp.biz.db.orm;

public class MoneyRecordItemBean {
	private String channel;
	private String money;
	private String status;
	private String withdraw_time;
	private String extra_msg;
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getWithdraw_time() {
		return withdraw_time;
	}
	public void setWithdraw_time(String withdraw_time) {
		this.withdraw_time = withdraw_time;
	}
	public String getExtra_msg() {
		return extra_msg;
	}
	public void setExtra_msg(String extra_msg) {
		this.extra_msg = extra_msg;
	}
	
}
