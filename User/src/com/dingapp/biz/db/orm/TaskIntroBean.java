package com.dingapp.biz.db.orm;

public class TaskIntroBean {
	private int task_id;
	private String task_name;
	private int task_available_cnt;
	private String task_category_name;
	private Double task_charges;
	private ImageBigAndMinBean task_icon;
	private String target_jmp_url;
	private Integer task_total_cnt;
	private String task_status;
	public int getTask_id() {
		return task_id;
	}

	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}

	public String getTask_name() {
		return task_name;
	}

	public void setTask_name(String task_name) {
		this.task_name = task_name;
	}

	public int getTask_available_cnt() {
		return task_available_cnt;
	}

	public void setTask_available_cnt(int task_available_cnt) {
		this.task_available_cnt = task_available_cnt;
	}

	public String getTask_category_name() {
		return task_category_name;
	}

	public void setTask_category_name(String task_category_name) {
		this.task_category_name = task_category_name;
	}

	public Double getTask_charges() {
		return task_charges;
	}

	public void setTask_charges(Double task_charges) {
		this.task_charges = task_charges;
	}

	public ImageBigAndMinBean getTask_icon() {
		return task_icon;
	}

	public void setTask_icon(ImageBigAndMinBean task_icon) {
		this.task_icon = task_icon;
	}

	public String getTarget_jmp_url() {
		return target_jmp_url;
	}

	public void setTarget_jmp_url(String target_jmp_url) {
		this.target_jmp_url = target_jmp_url;
	}

	public Integer getTask_total_cnt() {
		return task_total_cnt;
	}

	public void setTask_total_cnt(Integer task_total_cnt) {
		this.task_total_cnt = task_total_cnt;
	}

	public String getTask_status() {
		return task_status;
	}

	public void setTask_status(String task_status) {
		this.task_status = task_status;
	}
	
}
