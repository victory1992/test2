package com.dingapp.biz.db.orm;

import android.os.Parcel;
import android.text.TextUtils;

import com.dingapp.core.db.dao.OrmObject;

public class CacheData implements OrmObject{
	private String class_name;
	private String type_name;
	private String talk_id;
	private String act_id;
	private String json_content;
	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	public String getType_name() {
		return type_name;
	}

	public void setType_name(String type_name) {
		this.type_name = type_name;
	}

	public String getTalk_id() {
		return talk_id;
	}

	public void setTalk_id(String talk_id) {
		this.talk_id = talk_id;
	}

	public String getAct_id() {
		return act_id;
	}

	public void setAct_id(String act_id) {
		this.act_id = act_id;
	}

	public String getJson_content() {
		return json_content;
	}

	public void setJson_content(String json_content) {
		this.json_content = json_content;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		if(!TextUtils.isEmpty(class_name)){
			arg0.writeString(class_name);
		}else{
			arg0.writeString("");
		}
		if(!TextUtils.isEmpty(type_name)){
			arg0.writeString(type_name);
		}else{
			arg0.writeString("");
		}
		if(!TextUtils.isEmpty(talk_id)){
			arg0.writeString(talk_id);
		}else{
			arg0.writeString("");
		}
		if(!TextUtils.isEmpty(act_id)){
			arg0.writeString(act_id);
		}else{
			arg0.writeString("");
		}
		if(!TextUtils.isEmpty(json_content)){
			arg0.writeString(json_content);
		}else{
			arg0.writeString("");
		}
	}
	public static Creator<CacheData> CREATOR = new Creator<CacheData>() {
		
		@Override
		public CacheData[] newArray(int arg0) {
			return new CacheData[arg0];
		}
		
		@Override
		public CacheData createFromParcel(Parcel arg0) {
			CacheData bean = new CacheData();
			bean.class_name = arg0.readString();
			bean.type_name = arg0.readString();
			bean.talk_id = arg0.readString();
			bean.act_id = arg0.readString();
			bean.json_content = arg0.readString();
			return bean;
		}
	};
}
