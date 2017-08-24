package com.dingapp.biz.db.orm;

import java.util.List;

public class TaskDownloadStepDetailBean {
	private List<TaskStepIntro> intro_list;
	private String btn_title;
	private Integer step_no;
	private String share_title;
	private String share_desc;
	private String share_url;
	private String share_icon;
	private String share_url1;
	public List<TaskStepIntro> getIntro_list() {
		return intro_list;
	}

	public void setIntro_list(List<TaskStepIntro> intro_list) {
		this.intro_list = intro_list;
	}

	public String getBtn_title() {
		return btn_title;
	}

	public void setBtn_title(String btn_title) {
		this.btn_title = btn_title;
	}

	public Integer getStep_no() {
		return step_no;
	}

	public void setStep_no(Integer step_no) {
		this.step_no = step_no;
	}

	public String getShare_title() {
		return share_title;
	}

	public void setShare_title(String share_title) {
		this.share_title = share_title;
	}

	public String getShare_desc() {
		return share_desc;
	}

	public void setShare_desc(String share_desc) {
		this.share_desc = share_desc;
	}

	public String getShare_url() {
		return share_url;
	}

	public void setShare_url(String share_url) {
		this.share_url = share_url;
	}

	public String getShare_icon() {
		return share_icon;
	}

	public void setShare_icon(String share_icon) {
		this.share_icon = share_icon;
	}

	public String getShare_url1() {
		return share_url1;
	}

	public void setShare_url1(String share_url1) {
		this.share_url1 = share_url1;
	}
	
}
