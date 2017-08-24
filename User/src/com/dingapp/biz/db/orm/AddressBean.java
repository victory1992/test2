package com.dingapp.biz.db.orm;


import android.os.Parcel;
import android.os.Parcelable;

public class AddressBean implements Parcelable {
	private long address_id;
	private String province_name;
	private String city_name;
	private String county_name;
	private String address;
	private String receiver_name;
	private String receiver_mobile;
	private String default_tag;

	public long getAddress_id() {
		return address_id;
	}

	public void setAddress_id(long address_id) {
		this.address_id = address_id;
	}

	public String getProvince_name() {
		return province_name;
	}

	public void setProvince_name(String province_name) {
		this.province_name = province_name;
	}

	public String getDefault_tag() {
		return default_tag;
	}

	public void setDefault_tag(String default_tag) {
		this.default_tag = default_tag;
	}

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}

	public String getCounty_name() {
		return county_name;
	}

	public void setCounty_name(String county_name) {
		this.county_name = county_name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getReceiver_name() {
		return receiver_name;
	}

	public void setReceiver_name(String receiver_name) {
		this.receiver_name = receiver_name;
	}

	public String getReceiver_mobile() {
		return receiver_mobile;
	}

	public void setReceiver_mobile(String receiver_mobile) {
		this.receiver_mobile = receiver_mobile;
	}

	public AddressBean() {
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.address_id);
		dest.writeString(this.province_name);
		dest.writeString(this.city_name);
		dest.writeString(this.county_name);
		dest.writeString(this.address);
		dest.writeString(this.receiver_name);
		dest.writeString(this.receiver_mobile);
		dest.writeString(this.default_tag);
	}

	protected AddressBean(Parcel in) {
		this.address_id = in.readLong();
		this.province_name = in.readString();
		this.city_name = in.readString();
		this.county_name = in.readString();
		this.address = in.readString();
		this.receiver_name = in.readString();
		this.receiver_mobile = in.readString();
		this.default_tag = in.readString();
	}

	public static final Creator<AddressBean> CREATOR = new Creator<AddressBean>() {
		public AddressBean createFromParcel(Parcel source) {
			return new AddressBean(source);
		}

		public AddressBean[] newArray(int size) {
			return new AddressBean[size];
		}
	};
}
