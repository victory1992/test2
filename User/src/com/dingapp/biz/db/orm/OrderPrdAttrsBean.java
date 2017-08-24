package com.dingapp.biz.db.orm;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class OrderPrdAttrsBean implements Parcelable {
	private String attr_name;
	private String attr_value;
	private int attr_id;

	public String getAttr_name() {
		return attr_name;
	}

	public void setAttr_name(String attr_name) {
		this.attr_name = attr_name;
	}

	public String getAttr_value() {
		return attr_value;
	}

	public void setAttr_value(String attr_value) {
		this.attr_value = attr_value;
	}

	public int getAttr_id() {
		return attr_id;
	}

	public void setAttr_id(int attr_id) {
		this.attr_id = attr_id;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		if (!TextUtils.isEmpty(this.attr_name)) {
			dest.writeString(this.attr_name);
		} else {
			dest.writeString("");
		}
		if (!TextUtils.isEmpty(this.attr_value)) {
			dest.writeString(this.attr_value);
		} else {
			dest.writeString("");
		}
		if (this.attr_id != 0) {
			dest.writeInt(this.attr_id);
		} else {
			dest.writeInt(0);
		}

	}

	public static final Creator<OrderPrdAttrsBean> CREATOR = new Creator<OrderPrdAttrsBean>() {

		@Override
		public OrderPrdAttrsBean[] newArray(int arg0) {
			return new OrderPrdAttrsBean[arg0];
		}

		@Override
		public OrderPrdAttrsBean createFromParcel(Parcel dest) {
			OrderPrdAttrsBean bean = new OrderPrdAttrsBean();
			bean.setAttr_name(dest.readString());
			bean.setAttr_value(dest.readString());
			bean.setAttr_id(dest.readInt());
			return bean;
		}
	};
}
