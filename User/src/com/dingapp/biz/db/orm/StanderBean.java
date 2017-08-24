package com.dingapp.biz.db.orm;

public class StanderBean extends BaseBean {
	private DataEntity data;
	public DataEntity getData() {
		return data;
	}
	public void setData(DataEntity data) {
		this.data = data;
	}
	public static class DataEntity{
		private String suc;

		public String getSuc() {
			return suc;
		}

		public void setSuc(String suc) {
			this.suc = suc;
		}
	}
}
