package com.dingapp.core.db.orm;

import android.os.Parcel;
import android.text.TextUtils;

import com.dingapp.core.db.dao.OrmObject;

public class Member implements OrmObject {
	/** 表主键 */
	private Long memberId;

	/** 手机号 */
	private String loginName;

	/** sessionId */
	private String sessionId;

	private String checkCode;

	private String token;

	private String origin;

	private String headerProfile;
	private String nickName;
	private String real_name;
	private Integer score;
	private String wx_nick_name;
	private String birthday;
	private String otherString1;
	private String otherString2;
	private String otherString3;
	private String otherString4;
	private String otherString5;

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getCheckCode() {
		return checkCode;
	}

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getHeaderProfile() {
		return headerProfile;
	}

	public void setHeaderProfile(String headerProfile) {
		this.headerProfile = headerProfile;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getReal_name() {
		return real_name;
	}

	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getWx_nick_name() {
		return wx_nick_name;
	}

	public void setWx_nick_name(String wx_nick_name) {
		this.wx_nick_name = wx_nick_name;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getOtherString1() {
		return otherString1;
	}

	public void setOtherString1(String otherString1) {
		this.otherString1 = otherString1;
	}

	public String getOtherString2() {
		return otherString2;
	}

	public void setOtherString2(String otherString2) {
		this.otherString2 = otherString2;
	}

	public String getOtherString3() {
		return otherString3;
	}

	public void setOtherString3(String otherString3) {
		this.otherString3 = otherString3;
	}

	public String getOtherString4() {
		return otherString4;
	}

	public void setOtherString4(String otherString4) {
		this.otherString4 = otherString4;
	}

	public String getOtherString5() {
		return otherString5;
	}

	public void setOtherString5(String otherString5) {
		this.otherString5 = otherString5;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (getMemberId() != null) {
			dest.writeLong(getMemberId());
		} else {
			dest.writeLong(-1L);
		}
		if (getLoginName() != null) {
			dest.writeString(getLoginName());
		} else {
			dest.writeString("");
		}
		if (getSessionId() != null) {
			dest.writeString(getSessionId());
		} else {
			dest.writeString("");
		}
		if (checkCode != null) {
			dest.writeString(checkCode);
		} else {
			dest.writeString("");
		}
		if (token != null) {
			dest.writeString(token);
		} else {
			dest.writeString("");
		}
		if (origin != null) {
			dest.writeString(origin);
		} else {
			dest.writeString("");
		}
		if (headerProfile != null) {
			dest.writeString(headerProfile);
		} else {
			dest.writeString("");
		}
		if (nickName != null) {
			dest.writeString(nickName);
		} else {
			dest.writeString("");
		}
		if (!TextUtils.isEmpty(real_name)) {
			dest.writeString(real_name);
		} else {
			dest.writeString("");
		}
		if (score != null) {
			dest.writeInt(score);
		} else {
			dest.writeInt(0);
		}
		if (wx_nick_name != null) {
			dest.writeString(wx_nick_name);
		} else {
			dest.writeString("");
		}
		if (!TextUtils.isEmpty(birthday)) {
			dest.writeString(birthday);
		} else {
			dest.writeString("");
		}
		if (!TextUtils.isEmpty(otherString1)) {
			dest.writeString(otherString1);
		} else {
			dest.writeString("");
		}
		if (!TextUtils.isEmpty(otherString2)) {
			dest.writeString(otherString2);
		} else {
			dest.writeString("");
		}
		if (!TextUtils.isEmpty(otherString3)) {
			dest.writeString(otherString3);
		} else {
			dest.writeString("");
		}
		if (!TextUtils.isEmpty(otherString4)) {
			dest.writeString(otherString4);
		} else {
			dest.writeString("");
		}
		if (!TextUtils.isEmpty(otherString5)) {
			dest.writeString(otherString5);
		} else {
			dest.writeString("");
		}
	}

	public static final Creator<Member> CREATOR = new Creator<Member>() {

		@Override
		public Member createFromParcel(Parcel source) {
			Member member = new Member();
			member.setMemberId(source.readLong());
			member.setLoginName(source.readString());
			member.setSessionId(source.readString());
			member.setCheckCode(source.readString());
			member.setToken(source.readString());
			member.setOrigin(source.readString());
			member.setHeaderProfile(source.readString());
			member.setNickName(source.readString());
			member.setReal_name(source.readString());
			member.setScore(source.readInt());
			member.setWx_nick_name(source.readString());
			member.setBirthday(source.readString());
			member.setOtherString1(source.readString());
			member.setOtherString2(source.readString());
			member.setOtherString3(source.readString());
			member.setOtherString4(source.readString());
			member.setOtherString5(source.readString());
			return member;
		}

		@Override
		public Member[] newArray(int size) {
			return new Member[size];
		}
	};
}
