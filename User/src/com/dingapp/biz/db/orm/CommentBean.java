package com.dingapp.biz.db.orm;

import java.util.ArrayList;

public class CommentBean {
	private int comment_id;

	public int getComment_id() {
		return comment_id;
	}

	public void setComment_id(int comment_id) {
		this.comment_id = comment_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public ArrayList<GoodsPicBean> getComment_pics() {
		return comment_pics;
	}

	public void setComment_pics(ArrayList<GoodsPicBean> comment_pics) {
		this.comment_pics = comment_pics;
	}

	public int getOwner_id() {
		return owner_id;
	}

	public void setOwner_id(int owner_id) {
		this.owner_id = owner_id;
	}

	public String getOwner_nick_name() {
		return owner_nick_name;
	}

	public void setOwner_nick_name(String owner_nick_name) {
		this.owner_nick_name = owner_nick_name;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public GoodsPicBean getOwner_header() {
		return owner_header;
	}

	public void setOwner_header(GoodsPicBean owner_header) {
		this.owner_header = owner_header;
	}

	private String content;
	private int score;
	private ArrayList<GoodsPicBean> comment_pics;
	private int owner_id;
	private String owner_nick_name;
	private String create_time;
	private GoodsPicBean owner_header;
}