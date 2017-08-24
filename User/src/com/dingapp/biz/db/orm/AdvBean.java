package com.dingapp.biz.db.orm;

public class AdvBean {
	private int ad_id;
	private int ad_type;
	private String target_jmp_url;
	private ImageBigAndMinBean img;

	public int getAd_id() {
		return ad_id;
	}

	public void setAd_id(int ad_id) {
		this.ad_id = ad_id;
	}

	public int getAd_type() {
		return ad_type;
	}

	public void setAd_type(int ad_type) {
		this.ad_type = ad_type;
	}

	public String getTarget_jmp_url() {
		return target_jmp_url;
	}

	public void setTarget_jmp_url(String target_jmp_url) {
		this.target_jmp_url = target_jmp_url;
	}

	public ImageBigAndMinBean getImg() {
		return img;
	}

	public void setImg(ImageBigAndMinBean img) {
		this.img = img;
	}
}
