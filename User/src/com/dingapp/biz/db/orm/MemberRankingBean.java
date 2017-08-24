package com.dingapp.biz.db.orm;

public class MemberRankingBean {
	private Integer pos;
	private String header_img_url;
	private String nick_name;
	private Double money;
	private String join_time;
	private int member_id;
	public Integer getPos() {
		return pos;
	}
	public void setPos(Integer pos) {
		this.pos = pos;
	}
	public String getHeader_img_url() {
		return header_img_url;
	}
	public void setHeader_img_url(String header_img_url) {
		this.header_img_url = header_img_url;
	}
	public String getNick_name() {
		return nick_name;
	}
	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}
	public Double getMoney() {
		return money;
	}
	public void setMoney(Double money) {
		this.money = money;
	}
	public String getJoin_time() {
		return join_time;
	}
	public void setJoin_time(String join_time) {
		this.join_time = join_time;
	}
	public int getMember_id() {
		return member_id;
	}
	public void setMember_id(int member_id) {
		this.member_id = member_id;
	}
	
}
