package com.dingapp.biz.db.orm;

import java.io.Serializable;

public class AlipayConfBean implements Serializable{
	private String param;
	private String checkcode;
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	public String getCheckcode() {
		return checkcode;
	}
	public void setCheckcode(String checkcode) {
		this.checkcode = checkcode;
	}
	
}
