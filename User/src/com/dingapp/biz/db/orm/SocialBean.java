package com.dingapp.biz.db.orm;import java.util.List;public class SocialBean extends BaseBean {	private List<DataEntity> data;	public static class DataEntity{		private int talk_id;		private String create_time;		private String comment_detail;		private String talk_detail;		private int talk_member_id;				public int getTalk_id() {			return talk_id;		}		public void setTalk_id(int talk_id) {			this.talk_id = talk_id;		}		public int getMember_id() {			return member_id;		}		public void setMember_id(int member_id) {			this.member_id = member_id;		}		public String getNick_name() {			return nick_name;		}		public void setNick_name(String nick_name) {			this.nick_name = nick_name;		}		public PicEntity getHeader_pic() {			return header_pic;		}		public void setHeader_pic(PicEntity header_pic) {			this.header_pic = header_pic;		}		public String getIf_comment() {			return if_comment;		}		public void setIf_comment(String if_comment) {			this.if_comment = if_comment;		}		public String getIf_praise() {			return if_praise;		}		public void setIf_praise(String if_praise) {			this.if_praise = if_praise;		}		public String getComment_praise() {			return comment_praise;		}		public void setComment_praise(String comment_praise) {			this.comment_praise = comment_praise;		}		public PicEntity getTalk_pic() {			return talk_pic;		}		public void setTalk_pic(PicEntity talk_pic) {			this.talk_pic = talk_pic;		}		public String getCreate_time() {			return create_time;		}		public void setCreate_time(String create_time) {			this.create_time = create_time;		}		public String getComment_detail() {			return comment_detail;		}		public void setComment_detail(String comment_detail) {			this.comment_detail = comment_detail;		}		public String getTalk_detail() {			return talk_detail;		}		public void setTalk_detail(String talk_detail) {			this.talk_detail = talk_detail;		}		public int getTalk_member_id() {			return talk_member_id;		}		public void setTalk_member_id(int talk_member_id) {			this.talk_member_id = talk_member_id;		}		private int member_id;		private String nick_name;		private PicEntity header_pic;		private String if_comment;		private String if_praise;		private String comment_praise;		private PicEntity talk_pic; 		public static class PicEntity{			private String origin_url;			public String getOrigin_url() {				return origin_url;			}			public void setOrigin_url(String origin_url) {				this.origin_url = origin_url;			}			public String getMiniature_url() {				return miniature_url;			}			public void setMiniature_url(String miniature_url) {				this.miniature_url = miniature_url;			}			private String miniature_url;		}	}	public List<DataEntity> getData() {		return data;	}	public void setData(List<DataEntity> data) {		this.data = data;	}}