package com.dingapp.biz.db.orm;

public class MemberBean extends BaseBean {
	private DataEntity data;

	public DataEntity getData() {
		return data;
	}

	public void setData(DataEntity data) {
		this.data = data;
	}

	public static class DataEntity {
		private DataPicEntity header_pic;
		private int member_id;
		private String mobile;
		private String nick_name;
		private String mark_name;
		private String gender;
		private String is_friend;
		private String region;
		private String signature;
		private int friend_id;
		private String share_url;

		public DataPicEntity getHeader_pic() {
			return header_pic;
		}

		public void setHeader_pic(DataPicEntity header_pic) {
			this.header_pic = header_pic;
		}

		public int getMember_id() {
			return member_id;
		}

		public void setMember_id(int member_id) {
			this.member_id = member_id;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getNick_name() {
			return nick_name;
		}

		public void setNick_name(String nick_name) {
			this.nick_name = nick_name;
		}

		public String getMark_name() {
			return mark_name;
		}

		public void setMark_name(String mark_name) {
			this.mark_name = mark_name;
		}

		public String getGender() {
			return gender;
		}

		public void setGender(String gender) {
			this.gender = gender;
		}

		public String getIs_friend() {
			return is_friend;
		}

		public void setIs_friend(String is_friend) {
			this.is_friend = is_friend;
		}

		public String getRegion() {
			return region;
		}

		public void setRegion(String region) {
			this.region = region;
		}

		public String getSignature() {
			return signature;
		}

		public void setSignature(String signature) {
			this.signature = signature;
		}

		public int getFriend_id() {
			return friend_id;
		}

		public void setFriend_id(int friend_id) {
			this.friend_id = friend_id;
		}

		public String getShare_url() {
			return share_url;
		}

		public void setShare_url(String share_url) {
			this.share_url = share_url;
		}

		public static class DataPicEntity {
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
}
