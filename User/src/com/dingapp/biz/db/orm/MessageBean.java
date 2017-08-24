package com.dingapp.biz.db.orm;

public class MessageBean {
	private Integer type;
	private Integer msg_id;
	private String title;
	private String content;
	private String jmp_url;
	private String create_time;
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getMsg_id() {
		return msg_id;
	}
	public void setMsg_id(Integer msg_id) {
		this.msg_id = msg_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getJmp_url() {
		return jmp_url;
	}
	public void setJmp_url(String jmp_url) {
		this.jmp_url = jmp_url;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	
}
