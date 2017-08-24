package com.dingapp.biz.db.orm;

public class BBSTopChildBean {
	private int member_id;
	private ImageBigAndMinBean member_header;
	private String describe;
	public int getMember_id() {
		return member_id;
	}
	public void setMember_id(int member_id) {
		this.member_id = member_id;
	}
	public ImageBigAndMinBean getMember_header() {
		return member_header;
	}
	public void setMember_header(ImageBigAndMinBean member_header) {
		this.member_header = member_header;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	
}
