package com.dingapp.core.db.orm;

import android.os.Parcel;

import com.dingapp.core.db.dao.OrmObject;

public class App implements OrmObject {

	/** 表的主键， 自增 */
	private Long appId;

	/** 版本号, versionCode 是整型。 */
	private Integer version;

	/** 安装包的下载地址 */
	private String apkUrl;

	/** 是否允许下载, 为1表示允许下载，为0表示不升级 */
	private Integer accept;

	/** 下载状态，参考UpgradeStatus */
	private String status;

	/** 安装包在本机的存储地址 */
	private String localPath;

	public Long getKey() {
		return appId;
	}

	public void setKey(Long id) {
		this.appId = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getApkUrl() {
		return apkUrl;
	}

	public void setApkUrl(String apkUrl) {
		this.apkUrl = apkUrl;
	}

	public Integer getAccept() {
		return accept;
	}

	public void setAccept(Integer accept) {
		this.accept = accept;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (appId != null) {
			dest.writeLong(appId);
		} else {
			dest.writeLong(-1L);
		}
		if (version != null) {
			dest.writeInt(version);
		} else {
			dest.writeInt(-1);
		}
		if (apkUrl != null) {
			dest.writeString(apkUrl);
		} else {
			dest.writeString("");
		}
		if (accept != null) {
			dest.writeInt(accept);
		} else {
			dest.writeInt(1);
		}
		if (status != null) {
			dest.writeString(status);
		} else {
			dest.writeString("");
		}
		if (localPath != null) {
			dest.writeString(localPath);
		} else {
			dest.writeString("");
		}
	}

	public static final Creator<App> CREATOR = new Creator<App>() {

		@Override
		public App createFromParcel(Parcel source) {
			App app = new App();
			app.appId = source.readLong();
			app.version = source.readInt();
			app.apkUrl = source.readString();
			app.accept = source.readInt();
			app.status = source.readString();
			app.localPath = source.readString();
			return app;
		}

		@Override
		public App[] newArray(int size) {
			return new App[size];
		}
	};
}
