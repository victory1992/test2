package com.dingapp.biz.db.orm;

import java.util.List;

public class TaskDetailBean {
	private String favorited;
	private String share_url;
	private String share_url1;
	private String task_desc;
	private int task_id;
	private String task_audit_time;
	private String task_available_cnt;
	private Double task_charges;
	private String task_end_time;
	private ImageBigAndMinBean task_icon;
	private String task_name;
	private List<ImageBigAndMinBean>task_op_intros;
	private String task_op_url;
	private int task_owner_id;
	private String task_owner_name;
	private int task_participant_cnt;
	private List<TaskDetailChaildBean>task_requirement1;
	private String task_status;
	private int task_step_cnt;
	private int task_type;
	private List<JoinMembersBean> joiner_info;
	private String task_requirement;
	private Double has_give_money;
	private int task_available_op_time;
	private String memberUrl;
	private String audit_require_time;
	public String getFavorited() {
		return favorited;
	}
	public void setFavorited(String favorited) {
		this.favorited = favorited;
	}
	public String getShare_url() {
		return share_url;
	}
	public void setShare_url(String share_url) {
		this.share_url = share_url;
	}
	
	public String getShare_url1() {
		return share_url1;
	}
	public void setShare_url1(String share_url1) {
		this.share_url1 = share_url1;
	}
	public String getTask_desc() {
		return task_desc;
	}
	public void setTask_desc(String task_desc) {
		this.task_desc = task_desc;
	}
	public int getTask_id() {
		return task_id;
	}
	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}
	public String getTask_audit_time() {
		return task_audit_time;
	}
	public void setTask_audit_time(String task_audit_time) {
		this.task_audit_time = task_audit_time;
	}
	public String getTask_available_cnt() {
		return task_available_cnt;
	}
	public void setTask_available_cnt(String task_available_cnt) {
		this.task_available_cnt = task_available_cnt;
	}
	public Double getTask_charges() {
		return task_charges;
	}
	public void setTask_charges(Double task_charges) {
		this.task_charges = task_charges;
	}
	public String getTask_end_time() {
		return task_end_time;
	}
	public void setTask_end_time(String task_end_time) {
		this.task_end_time = task_end_time;
	}
	public ImageBigAndMinBean getTask_icon() {
		return task_icon;
	}
	public void setTask_icon(ImageBigAndMinBean task_icon) {
		this.task_icon = task_icon;
	}
	public String getTask_name() {
		return task_name;
	}
	public void setTask_name(String task_name) {
		this.task_name = task_name;
	}
	public List<ImageBigAndMinBean> getTask_op_intros() {
		return task_op_intros;
	}
	public void setTask_op_intros(List<ImageBigAndMinBean> task_op_intros) {
		this.task_op_intros = task_op_intros;
	}
	public String getTask_op_url() {
		return task_op_url;
	}
	public void setTask_op_url(String task_op_url) {
		this.task_op_url = task_op_url;
	}
	public int getTask_owner_id() {
		return task_owner_id;
	}
	public void setTask_owner_id(int task_owner_id) {
		this.task_owner_id = task_owner_id;
	}
	public String getTask_owner_name() {
		return task_owner_name;
	}
	public void setTask_owner_name(String task_owner_name) {
		this.task_owner_name = task_owner_name;
	}
	public int getTask_participant_cnt() {
		return task_participant_cnt;
	}
	public void setTask_participant_cnt(int task_participant_cnt) {
		this.task_participant_cnt = task_participant_cnt;
	}
	public List<TaskDetailChaildBean> getTask_requirement1() {
		return task_requirement1;
	}
	public void setTask_requirement1(List<TaskDetailChaildBean> task_requirement1) {
		this.task_requirement1 = task_requirement1;
	}
	public String getTask_status() {
		return task_status;
	}
	public void setTask_status(String task_status) {
		this.task_status = task_status;
	}
	public int getTask_step_cnt() {
		return task_step_cnt;
	}
	public void setTask_step_cnt(int task_step_cnt) {
		this.task_step_cnt = task_step_cnt;
	}
	public int getTask_type() {
		return task_type;
	}
	public void setTask_type(int task_type) {
		this.task_type = task_type;
	}
	public List<JoinMembersBean> getJoiner_info() {
		return joiner_info;
	}
	public void setJoiner_info(List<JoinMembersBean> joiner_info) {
		this.joiner_info = joiner_info;
	}
	public String getTask_requirement() {
		return task_requirement;
	}
	public void setTask_requirement(String task_requirement) {
		this.task_requirement = task_requirement;
	}
	public Double getHas_give_money() {
		return has_give_money;
	}
	public void setHas_give_money(Double has_give_money) {
		this.has_give_money = has_give_money;
	}
	public int getTask_available_op_time() {
		return task_available_op_time;
	}
	public void setTask_available_op_time(int task_available_op_time) {
		this.task_available_op_time = task_available_op_time;
	}
	public String getMemberUrl() {
		return memberUrl;
	}
	public void setMemberUrl(String memberUrl) {
		this.memberUrl = memberUrl;
	}
	public String getAudit_require_time() {
		return audit_require_time;
	}
	public void setAudit_require_time(String audit_require_time) {
		this.audit_require_time = audit_require_time;
	}
	
}
