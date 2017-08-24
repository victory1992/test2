package com.dingapp.biz.db.orm;

import android.os.Parcel;
import android.text.TextUtils;

import com.dingapp.core.db.dao.OrmObject;

public class TaskStepIntro implements OrmObject {
	private String content;
	private String demo_img;
	private Integer need_input;
	private Integer input_id;
	private Integer input_type;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDemo_img() {
		return demo_img;
	}

	public void setDemo_img(String demo_img) {
		this.demo_img = demo_img;
	}

	public int getNeed_input() {
		return need_input;
	}

	public void setNeed_input(int need_input) {
		this.need_input = need_input;
	}

	public int getInput_id() {
		return input_id;
	}

	public void setInput_id(int input_id) {
		this.input_id = input_id;
	}

	public int getInput_type() {
		return input_type;
	}

	public void setInput_type(int input_type) {
		this.input_type = input_type;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (!TextUtils.isEmpty(content)) {
			dest.writeString(content);
		} else {
			dest.writeString("");
		}
		if (!TextUtils.isEmpty(demo_img)) {
			dest.writeString(demo_img);
		} else {
			dest.writeString("");
		}
		if (need_input != null) {
			dest.writeInt(need_input.intValue());
		} else {
			dest.writeInt(0);
		}
		if (input_id != null) {
			dest.writeInt(input_id.intValue());
		} else {
			dest.writeInt(0);
		}
		if (input_type != null) {
			dest.writeInt(input_type.intValue());
		} else {
			dest.writeInt(0);
		}

	}

	public static final Creator<TaskStepIntro> CREATOR = new Creator<TaskStepIntro>() {

		@Override
		public TaskStepIntro[] newArray(int size) {
			// TODO Auto-generated method stub
			return new TaskStepIntro[size];
		}

		@Override
		public TaskStepIntro createFromParcel(Parcel source) {
			TaskStepIntro bean = new TaskStepIntro();
			bean.content = source.readString();
			bean.demo_img = source.readString();
			bean.need_input = source.readInt();
			bean.input_id = source.readInt();
			bean.input_type = source.readInt();
			return bean;
		}
	};

}
