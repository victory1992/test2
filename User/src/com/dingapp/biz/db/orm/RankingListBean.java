package com.dingapp.biz.db.orm;

import java.util.List;

public class RankingListBean {
	private Integer tips;
	private Integer self_pos;
	private List<MemberRankingBean> rank_list;
	public Integer getTips() {
		return tips;
	}
	public void setTips(Integer tips) {
		this.tips = tips;
	}
	public Integer getSelf_pos() {
		return self_pos;
	}
	public void setSelf_pos(Integer self_pos) {
		this.self_pos = self_pos;
	}
	public List<MemberRankingBean> getRank_list() {
		return rank_list;
	}
	public void setRank_list(List<MemberRankingBean> rank_list) {
		this.rank_list = rank_list;
	}
	
}
