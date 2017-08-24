package com.dingapp.biz.db.orm;

import java.util.List;

public class RecommendFriendBean extends BaseBean {
	private List<DataEntity> data;

	public List<DataEntity> getData() {
		return data;
	}

	public void setData(List<DataEntity> data) {
		this.data = data;
	}

	public static class DataEntity {
		private String member_nick_name;
		private String member_gender;
		private String star;
		private String areas;
		private int member_id;
		private int age;
		private DataPicEntity member_pic;

		public String getMember_nick_name() {
			return member_nick_name;
		}

		public void setMember_nick_name(String member_nick_name) {
			this.member_nick_name = member_nick_name;
		}

		public String getMember_gender() {
			return member_gender;
		}

		public void setMember_gender(String member_gender) {
			this.member_gender = member_gender;
		}

		public String getStar() {
			return star;
		}

		public void setStar(String star) {
			this.star = star;
		}

		public String getAreas() {
			return areas;
		}

		public void setAreas(String areas) {
			this.areas = areas;
		}

		public int getMember_id() {
			return member_id;
		}

		public void setMember_id(int member_id) {
			this.member_id = member_id;
		}

		public DataPicEntity getMember_pic() {
			return member_pic;
		}

		public void setMember_pic(DataPicEntity member_pic) {
			this.member_pic = member_pic;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
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
