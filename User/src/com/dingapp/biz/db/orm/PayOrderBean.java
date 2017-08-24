package com.dingapp.biz.db.orm;

import java.io.Serializable;

public class PayOrderBean implements Serializable{
	private AlipayConfBean alipay_conf;
	private WxConfBean wx_conf;
	private Long task_id;
	public AlipayConfBean getAlipay_conf() {
		return alipay_conf;
	}
	public void setAlipay_conf(AlipayConfBean alipay_conf) {
		this.alipay_conf = alipay_conf;
	}
	public WxConfBean getWx_conf() {
		return wx_conf;
	}
	public void setWx_conf(WxConfBean wx_conf) {
		this.wx_conf = wx_conf;
	}
	public Long getTask_id() {
		return task_id;
	}
	public void setTask_id(Long task_id) {
		this.task_id = task_id;
	}
	
}
