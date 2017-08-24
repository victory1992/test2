package com.dingapp.biz.db.orm;

import android.os.Parcel;
import android.text.TextUtils;

import com.dingapp.core.db.dao.OrmObject;

public class ConfigureBean implements OrmObject {
	private String urlName;
	private String pageName;
	private String pageType;

	public String getUrlName() {
		return urlName;
	}

	public void setUrlName(String urlName) {
		this.urlName = urlName;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		if (!TextUtils.isEmpty(urlName)) {
			arg0.writeString(urlName);
		} else {
			arg0.writeString("");
		}
		if (!TextUtils.isEmpty(pageName)) {
			arg0.writeString(pageName);
		} else {
			arg0.writeString("");
		}
		if (!TextUtils.isEmpty(pageType)) {
			arg0.writeString(pageType);
		} else {
			arg0.writeString("");
		}
	}

	public static final Creator<ConfigureBean> CREATOR = new Creator<ConfigureBean>() {

		@Override
		public ConfigureBean[] newArray(int size) {
			return new ConfigureBean[size];
		}

		@Override
		public ConfigureBean createFromParcel(Parcel source) {
			ConfigureBean bean = new ConfigureBean();
			bean.urlName = source.readString();
			bean.pageName = source.readString();
			bean.pageType = source.readString();
			return bean;
		}
	};

}
