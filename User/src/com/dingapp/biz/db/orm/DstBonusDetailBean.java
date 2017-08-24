package com.dingapp.biz.db.orm;
/**
 * 会员分成详情
 * @author king
 *
 */
public class DstBonusDetailBean {
	private String create_time;
	private String desc;
	private Double bonus;
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public Double getBonus() {
		return bonus;
	}
	public void setBonus(Double bonus) {
		this.bonus = bonus;
	}
	
}
