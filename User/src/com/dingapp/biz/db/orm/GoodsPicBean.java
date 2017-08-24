package com.dingapp.biz.db.orm;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kimchou on 2016/1/17.
 */
public class GoodsPicBean implements Parcelable {
    public String getDetail_url() {
        return detail_url;
    }

    public void setDetail_url(String detail_url) {
        this.detail_url = detail_url;
    }

    private String detail_url;
    private String miniature_url;

    public String getMiniature_url() {
        return miniature_url;
    }

    public void setMiniature_url(String miniature_url) {
        this.miniature_url = miniature_url;
    }

    public GoodsPicBean() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.detail_url);
        dest.writeString(this.miniature_url);
    }

    protected GoodsPicBean(Parcel in) {
        this.detail_url = in.readString();
        this.miniature_url = in.readString();
    }

    public static final Creator<GoodsPicBean> CREATOR = new Creator<GoodsPicBean>() {
        public GoodsPicBean createFromParcel(Parcel source) {
            return new GoodsPicBean(source);
        }

        public GoodsPicBean[] newArray(int size) {
            return new GoodsPicBean[size];
        }
    };
}
