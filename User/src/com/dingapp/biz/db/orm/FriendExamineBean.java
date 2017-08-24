package com.dingapp.biz.db.orm;

import java.util.List;

public class FriendExamineBean extends BaseBean{
	private List<DateEntity> data;
	public static class DateEntity{
		private int apply_id;
		private int member_id;
		private String member_nick_name;
		private String member_mark_name;
		private DatePicEntity member_pic;
		private String apply_reason;
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
		public String getMember_mark_name() {
			return member_mark_name;
		}
		public void setMember_mark_name(String member_mark_name) {
			this.member_mark_name = member_mark_name;
		}
		public DatePicEntity getMember_pic() {
			return member_pic;
		}
		public void setMember_pic(DatePicEntity member_pic) {
			this.member_pic = member_pic;
		}
		public String getApply_reason() {
			return apply_reason;
		}
		public void setApply_reason(String apply_reason) {
			this.apply_reason = apply_reason;
		}
		public static class DatePicEntity {
			private String origin_url;
			private String miniature_url;
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
		}
	}
	public List<DateEntity> getData() {
		return data;
	}
	public void setData(List<DateEntity> data) {
		this.data = data;
	}
}
