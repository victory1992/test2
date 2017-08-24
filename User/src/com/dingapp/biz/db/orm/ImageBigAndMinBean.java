package com.dingapp.biz.db.orm;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class ImageBigAndMinBean implements Parcelable {
	private String detail_url;
	private String miniature_url;

	public String getDetail_url() {
		return detail_url;
	}

	public void setDetail_url(String detail_url) {
		this.detail_url = detail_url;
	}

	public String getMiniature_url() {
		return miniature_url;
	}

	public void setMiniature_url(String miniature_url) {
		this.miniature_url = miniature_url;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		if (!TextUtils.isEmpty(this.detail_url)) {
			dest.writeString(detail_url);
		} else {
			dest.writeString("");
		}
		if (!TextUtils.isEmpty(this.miniature_url)) {
			dest.writeString(miniature_url);
		} else {
			dest.writeString("");
		}
	}

	public static final Creator<ImageBigAndMinBean> CREATOR = new Creator<ImageBigAndMinBean>() {

		@Override
		public ImageBigAndMinBean[] newArray(int arg0) {
			return new ImageBigAndMinBean[arg0];
		}

		@Override
		public ImageBigAndMinBean createFromParcel(Parcel dest) {
			ImageBigAndMinBean bean = new ImageBigAndMinBean();
			bean.setDetail_url(dest.readString());
			bean.setMiniature_url(dest.readString());
			return bean;
		}
	};
}
