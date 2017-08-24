package com.dingapp.biz.db.orm;

public class PraiseInfoBean {
	private String praise_member_nick_name;
	private int praise_member_id;
	private ImageBigAndMinBean praise_member_header;
	public String getPraise_member_nick_name() {
		return praise_member_nick_name;
	}
	public void setPraise_member_nick_name(String praise_member_nick_name) {
		this.praise_member_nick_name = praise_member_nick_name;
	}
	public int getPraise_member_id() {
		return praise_member_id;
	}
	public void setPraise_member_id(int praise_member_id) {
		this.praise_member_id = praise_member_id;
	}
	public ImageBigAndMinBean getPraise_member_header() {
		return praise_member_header;
	}
	public void setPraise_member_header(ImageBigAndMinBean praise_member_header) {
		this.praise_member_header = praise_member_header;
	}
	
}
