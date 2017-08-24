package com.dingapp.biz.db.orm;

import java.util.List;

public class TalkListBean {
	private int talk_id;
	private int talk_type_id;
	private String talk_title;
	private String talk_content;
	private List<ImageBigAndMinBean>talk_pics;
	private String if_recommand;
	private String if_hot;
	private int member_id;
	private String member_nick_name;
	private ImageBigAndMinBean member_header;
	private String create_time;
	private int comment_cnt;
	private int praise_cnt;
	private String address;
	private String talk_type_name;
	private String if_praise;
	public int getTalk_id() {
		return talk_id;
	}
	public void setTalk_id(int talk_id) {
		this.talk_id = talk_id;
	}
	public int getTalk_type_id() {
		return talk_type_id;
	}
	public void setTalk_type_id(int talk_type_id) {
		this.talk_type_id = talk_type_id;
	}
	public String getTalk_title() {
		return talk_title;
	}
	public void setTalk_title(String talk_title) {
		this.talk_title = talk_title;
	}
	public String getTalk_content() {
		return talk_content;
	}
	public void setTalk_content(String talk_content) {
		this.talk_content = talk_content;
	}
	public List<ImageBigAndMinBean> getTalk_pics() {
		return talk_pics;
	}
	public void setTalk_pics(List<ImageBigAndMinBean> talk_pics) {
		this.talk_pics = talk_pics;
	}
	public String getIf_recommand() {
		return if_recommand;
	}
	public void setIf_recommand(String if_recommand) {
		this.if_recommand = if_recommand;
	}
	public String getIf_hot() {
		return if_hot;
	}
	public void setIf_hot(String if_hot) {
		this.if_hot = if_hot;
	}
	public int getMember_id() {
		return member_id;
	}
	public void setMember_id(int member_id) {
		this.member_id = member_id;
	}
	public String getMember_nick_name() {
		return member_nick_name;
	}
	public void setMember_nick_name(String member_nick_name) {
		this.member_nick_name = member_nick_name;
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
	public int getComment_cnt() {
		return comment_cnt;
	}
	public void setComment_cnt(int comment_cnt) {
		this.comment_cnt = comment_cnt;
	}
	public int getPraise_cnt() {
		return praise_cnt;
	}
	public void setPraise_cnt(int praise_cnt) {
		this.praise_cnt = praise_cnt;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTalk_type_name() {
		return talk_type_name;
	}
	public void setTalk_type_name(String talk_type_name) {
		this.talk_type_name = talk_type_name;
	}
	public String getIf_praise() {
		return if_praise;
	}
	public void setIf_praise(String if_praise) {
		this.if_praise = if_praise;
	}
}
