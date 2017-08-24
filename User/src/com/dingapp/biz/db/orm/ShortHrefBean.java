package com.dingapp.biz.db.orm;

public class ShortHrefBean extends BaseBean {
	private DataEntity data;
	public static class DataEntity{
		private String short_href;
		public String getShort_href() {
			return short_href;
		}
		public void setShort_href(String short_href) {
			this.short_href = short_href;
		}
		public String getDetail() {
			return detail;
		}
		public void setDetail(String detail) {
			this.detail = detail;
		}
		private String detail;
	}
	public DataEntity getData() {
		return data;
	}
	public void setData(DataEntity data) {
		this.data = data;
	}
}
