package com.dingapp.biz.db.orm;


public class SelfTaskStatBean {
	private Integer historical_task_cnt;
	private Integer undone_task_cnt;
	private Integer auditing_task_cnt;
	private Integer audited_task_cnt;
	private Integer noaccept_task_cnt;
	private String check_in;
	private String check_in_url;
	private String bonus_doubled;
	private String affiliate_url;
	private String affiliate_url1;
	private String if_unread;
	public Integer getHistorical_task_cnt() {
		return historical_task_cnt;
	}
	public void setHistorical_task_cnt(Integer historical_task_cnt) {
		this.historical_task_cnt = historical_task_cnt;
	}
	public Integer getUndone_task_cnt() {
		return undone_task_cnt;
	}
	public void setUndone_task_cnt(Integer undone_task_cnt) {
		this.undone_task_cnt = undone_task_cnt;
	}
	public Integer getAuditing_task_cnt() {
		return auditing_task_cnt;
	}
	public void setAuditing_task_cnt(Integer auditing_task_cnt) {
		this.auditing_task_cnt = auditing_task_cnt;
	}
	public Integer getAudited_task_cnt() {
		return audited_task_cnt;
	}
	public void setAudited_task_cnt(Integer audited_task_cnt) {
		this.audited_task_cnt = audited_task_cnt;
	}
	public Integer getNoaccept_task_cnt() {
		return noaccept_task_cnt;
	}
	public void setNoaccept_task_cnt(Integer noaccept_task_cnt) {
		this.noaccept_task_cnt = noaccept_task_cnt;
	}
	public String getCheck_in() {
		return check_in;
	}
	public void setCheck_in(String check_in) {
		this.check_in = check_in;
	}
	public String getCheck_in_url() {
		return check_in_url;
	}
	public void setCheck_in_url(String check_in_url) {
		this.check_in_url = check_in_url;
	}
	public String getBonus_doubled() {
		return bonus_doubled;
	}
	public void setBonus_doubled(String bonus_doubled) {
		this.bonus_doubled = bonus_doubled;
	}
	public String getAffiliate_url() {
		return affiliate_url;
	}
	public void setAffiliate_url(String affiliate_url) {
		this.affiliate_url = affiliate_url;
	}
	public String getAffiliate_url1() {
		return affiliate_url1;
	}
	public void setAffiliate_url1(String affiliate_url1) {
		this.affiliate_url1 = affiliate_url1;
	}
	public String getIf_unread() {
		return if_unread;
	}
	public void setIf_unread(String if_unread) {
		this.if_unread = if_unread;
	}
}
