package com.dingapp.biz.db.orm;

import java.util.List;

public class BBSTopBean {
	private int prize_cnt;
	private int join_cnt;
	private List<BBSTopChildBean> dynamic_info;
	public int getPrize_cnt() {
		return prize_cnt;
	}
	public void setPrize_cnt(int prize_cnt) {
		this.prize_cnt = prize_cnt;
	}
	public int getJoin_cnt() {
		return join_cnt;
	}
	public void setJoin_cnt(int join_cnt) {
		this.join_cnt = join_cnt;
	}
	public List<BBSTopChildBean> getDynamic_info() {
		return dynamic_info;
	}
	public void setDynamic_info(List<BBSTopChildBean> dynamic_info) {
		this.dynamic_info = dynamic_info;
	}
	
}
