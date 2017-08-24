package com.dingapp.biz.db.orm;

public class BBSTypeListBean {
	private int talk_type_id;
	private String talk_type_name;
	private ImageBigAndMinBean talk_type_img;

	public int getTalk_type_id() {
		return talk_type_id;
	}

	public void setTalk_type_id(int talk_type_id) {
		this.talk_type_id = talk_type_id;
	}

	public String getTalk_type_name() {
		return talk_type_name;
	}

	public void setTalk_type_name(String talk_type_name) {
		this.talk_type_name = talk_type_name;
	}

	public ImageBigAndMinBean getTalk_type_img() {
		return talk_type_img;
	}

	public void setTalk_type_img(ImageBigAndMinBean talk_type_img) {
		this.talk_type_img = talk_type_img;
	}

}
