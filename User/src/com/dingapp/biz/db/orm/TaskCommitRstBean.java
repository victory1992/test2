package com.dingapp.biz.db.orm;

public class TaskCommitRstBean {
	private String suc;
	private int prize_available;
	private String prize_url;
	private Double share_base_prize;
	private int share_delta_duration;
	private int share_delta_view_cnt;
	private Double share_delta_prize;
	private Double download_prize;
	public String getSuc() {
		return suc;
	}

	public void setSuc(String suc) {
		this.suc = suc;
	}

	public int getPrize_available() {
		return prize_available;
	}

	public void setPrize_available(int prize_available) {
		this.prize_available = prize_available;
	}

	public String getPrize_url() {
		return prize_url;
	}

	public void setPrize_url(String prize_url) {
		this.prize_url = prize_url;
	}

	public Double getShare_base_prize() {
		return share_base_prize;
	}

	public void setShare_base_prize(Double share_base_prize) {
		this.share_base_prize = share_base_prize;
	}

	public int getShare_delta_duration() {
		return share_delta_duration;
	}

	public void setShare_delta_duration(int share_delta_duration) {
		this.share_delta_duration = share_delta_duration;
	}

	public int getShare_delta_view_cnt() {
		return share_delta_view_cnt;
	}

	public void setShare_delta_view_cnt(int share_delta_view_cnt) {
		this.share_delta_view_cnt = share_delta_view_cnt;
	}

	public Double getShare_delta_prize() {
		return share_delta_prize;
	}

	public void setShare_delta_prize(Double share_delta_prize) {
		this.share_delta_prize = share_delta_prize;
	}

	public Double getDownload_prize() {
		return download_prize;
	}

	public void setDownload_prize(Double download_prize) {
		this.download_prize = download_prize;
	}
	
}
