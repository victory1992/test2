package com.dingapp.biz.db.orm;

import java.util.List;

public class ItemTeamMemberBean {
	private Integer dst_id;
	private Integer level;
	private String header_img_url;
	private String nick_name;
	private Double dst_money;
	private String join_time;
	private List<ItemTeamMemberBean> dst_members;
	private int member_id;

	public Integer getDst_id() {
		return dst_id;
	}

	public void setDst_id(Integer dst_id) {
		this.dst_id = dst_id;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
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

	public Double getDst_money() {
		return dst_money;
	}

	public void setDst_money(Double dst_money) {
		this.dst_money = dst_money;
	}

	public String getJoin_time() {
		return join_time;
	}

	public void setJoin_time(String join_time) {
		this.join_time = join_time;
	}

	public List<ItemTeamMemberBean> getDst_members() {
		return dst_members;
	}

	public void setDst_members(List<ItemTeamMemberBean> dst_members) {
		this.dst_members = dst_members;
	}

	public int getMember_id() {
		return member_id;
	}

	public void setMember_id(int member_id) {
		this.member_id = member_id;
	}

}
