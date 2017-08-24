package com.dingapp.biz.db.orm;

public class ExpressListBean {
	private int express_id;
	private String express_name;
	private Double inner_price;
	private Double other_per_price;
	public int getExpress_id() {
		return express_id;
	}
	public void setExpress_id(int express_id) {
		this.express_id = express_id;
	}
	public String getExpress_name() {
		return express_name;
	}
	public void setExpress_name(String express_name) {
		this.express_name = express_name;
	}
	public Double getInner_price() {
		return inner_price;
	}
	public void setInner_price(Double inner_price) {
		this.inner_price = inner_price;
	}
	public Double getOther_per_price() {
		return other_per_price;
	}
	public void setOther_per_price(Double other_per_price) {
		this.other_per_price = other_per_price;
	}
	
}
