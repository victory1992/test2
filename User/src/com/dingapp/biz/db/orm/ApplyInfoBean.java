package com.dingapp.biz.db.orm;

public class ApplyInfoBean extends BaseBean {
	private DataEntity data;
	public static class DataEntity{
		private String apply_url;
		private int info_cnt;
		public int getInfo_cnt() {
			return info_cnt;
		}
		public void setInfo_cnt(int info_cnt) {
			this.info_cnt = info_cnt;
		}
		public String getDetail() {
			return detail;
		}
		public void setDetail(String detail) {
			this.detail = detail;
		}
		public String getNick_name() {
			return nick_name;
		}
		public void setNick_name(String nick_name) {
			this.nick_name = nick_name;
		}
		public String getCreate_time() {
			return create_time;
		}
		public void setCreate_time(String create_time) {
			this.create_time = create_time;
		}
		public String getApply_url() {
			return apply_url;
		}
		public void setApply_url(String apply_url) {
			this.apply_url = apply_url;
		}
		private String detail;
		private String nick_name;
		private String create_time;
	}
	public DataEntity getData() {
		return data;
	}
	public void setData(DataEntity data) {
		this.data = data;
	}
	
}
