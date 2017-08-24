package com.dingapp.biz.db.orm;

public class NewsBean {
	private String news_content;
	private String task_category_name;
	private String target_jmp_url;

	public String getNews_content() {
		return news_content;
	}

	public void setNews_content(String news_content) {
		this.news_content = news_content;
	}

	public String getTask_category_name() {
		return task_category_name;
	}

	public void setTask_category_name(String task_category_name) {
		this.task_category_name = task_category_name;
	}

	public String getTarget_jmp_url() {
		return target_jmp_url;
	}

	public void setTarget_jmp_url(String target_jmp_url) {
		this.target_jmp_url = target_jmp_url;
	}

}
