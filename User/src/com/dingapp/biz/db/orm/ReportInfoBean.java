package com.dingapp.biz.db.orm;

import java.util.List;

public class ReportInfoBean {
	private String report_nick_name;
	private int praise_cnt;
	private String report_content;
	private int report_id;
	private ImageBigAndMinBean member_header;
	private String create_time;
	private int member_id;
	private String if_praise;
	private List<ImageBigAndMinBean> comment_pics;
	public String getReport_nick_name() {
		return report_nick_name;
	}
	public void setReport_nick_name(String report_nick_name) {
		this.report_nick_name = report_nick_name;
	}
	public int getPraise_cnt() {
		return praise_cnt;
	}
	public void setPraise_cnt(int praise_cnt) {
		this.praise_cnt = praise_cnt;
	}
	public String getReport_content() {
		return report_content;
	}
	public void setReport_content(String report_content) {
		this.report_content = report_content;
	}
	public int getReport_id() {
		return report_id;
	}
	public void setReport_id(int report_id) {
		this.report_id = report_id;
	}
	public ImageBigAndMinBean getMember_header() {
		return member_header;
	}
	public void setMember_header(ImageBigAndMinBean member_header) {
		this.member_header = member_header;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public int getMember_id() {
		return member_id;
	}
	public void setMember_id(int member_id) {
		this.member_id = member_id;
	}
	public String getIf_praise() {
		return if_praise;
	}
	public void setIf_praise(String if_praise) {
		this.if_praise = if_praise;
	}
	public List<ImageBigAndMinBean> getComment_pics() {
		return comment_pics;
	}
	public void setComment_pics(List<ImageBigAndMinBean> comment_pics) {
		this.comment_pics = comment_pics;
	}
	
}
