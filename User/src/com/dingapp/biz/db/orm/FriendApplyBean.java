package com.dingapp.biz.db.orm;

import java.util.List;


public class FriendApplyBean extends BaseBean {
	private List<DataEntity> data;
	public static class DataEntity{
		private int apply_id;
		private int member_id;
		private String member_nick_name;
		private String apply_reason;
		private DataPicEntity member_pic;
		private String examine_status;
		public int getApply_id() {
			return apply_id;
		}
		public void setApply_id(int apply_id) {
			this.apply_id = apply_id;
		}
		public int getMember_id() {
			return member_id;
		}
		public void setMember_id(int member_id) {
			this.member_id = member_id;
		}
		public String getMember_nick_name() {
			return member_nick_name;
		}
		public void setMember_nick_name(String member_nick_name) {
			this.member_nick_name = member_nick_name;
		}
		public String getApply_reason() {
			return apply_reason;
		}
		public void setApply_reason(String apply_reason) {
			this.apply_reason = apply_reason;
		}
		public DataPicEntity getMember_pic() {
			return member_pic;
		}
		public void setMember_pic(DataPicEntity member_pic) {
			this.member_pic = member_pic;
		}
		public String getExamine_status() {
			return examine_status;
		}
		public void setExamine_status(String examine_status) {
			this.examine_status = examine_status;
		}
		public static class DataPicEntity {
			private String origin_url;
			public String getOrigin_url() {
				return origin_url;
			}
			public void setOrigin_url(String origin_url) {
				this.origin_url = origin_url;
			}
			public String getMiniature_url() {
				return miniature_url;
			}
			public void setMiniature_url(String miniature_url) {
				this.miniature_url = miniature_url;
			}
			private String miniature_url;
		}
	}
	public List<DataEntity> getData() {
		return data;
	}
	public void setData(List<DataEntity> data) {
		this.data = data;
	}
}
