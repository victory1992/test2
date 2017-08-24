package com.dingapp.biz.db.orm;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class CartListBean implements Parcelable {
	private int cart_id;
	private int goods_id;
	private int cnt;
	private String goods_title;
	private Double goods_price;
	private double goods_weight;
	private List<OrderPrdAttrsBean> goods_attrs;
	private ImageBigAndMinBean goods_img;
	private String is_select;
	private int goods_cnt;
	private Double vip_price;
	private String is_vip;
	private int send_score;
	public int getCart_id() {
		return cart_id;
	}

	public void setCart_id(int cart_id) {
		this.cart_id = cart_id;
	}

	public int getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(int goods_id) {
		this.goods_id = goods_id;
	}

	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}

	public String getGoods_title() {
		return goods_title;
	}

	public void setGoods_title(String goods_title) {
		this.goods_title = goods_title;
	}

	public Double getGoods_price() {
		return goods_price;
	}

	public void setGoods_price(Double goods_price) {
		this.goods_price = goods_price;
	}

	public double getGoods_weight() {
		return goods_weight;
	}

	public void setGoods_weight(double goods_weight) {
		this.goods_weight = goods_weight;
	}

	public List<OrderPrdAttrsBean> getGoods_attrs() {
		return goods_attrs;
	}

	public void setGoods_attrs(List<OrderPrdAttrsBean> goods_attrs) {
		this.goods_attrs = goods_attrs;
	}

	public ImageBigAndMinBean getGoods_img() {
		return goods_img;
	}

	public void setGoods_img(ImageBigAndMinBean goods_img) {
		this.goods_img = goods_img;
	}

	public String getIs_select() {
		return is_select;
	}

	public void setIs_select(String is_select) {
		this.is_select = is_select;
	}
	
	public int getGoods_cnt() {
		return goods_cnt;
	}

	public void setGoods_cnt(int goods_cnt) {
		this.goods_cnt = goods_cnt;
	}
	
	 
	public Double getVip_price() {
		return vip_price;
	}

	public void setVip_price(Double vip_price) {
		this.vip_price = vip_price;
	}

	public String getIs_vip() {
		return is_vip;
	}

	public void setIs_vip(String is_vip) {
		this.is_vip = is_vip;
	}
	
	public int getSend_score() {
		return send_score;
	}

	public void setSend_score(int send_score) {
		this.send_score = send_score;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		if (this.cart_id != 0) {
			dest.writeInt(this.cart_id);
		} else {
			dest.writeInt(0);
		}
		if (this.goods_id != 0) {
			dest.writeInt(this.goods_id);
		} else {
			dest.writeInt(0);
		}
		if (this.cnt != 0) {
			dest.writeInt(this.cnt);
		} else {
			dest.writeInt(0);
		}
		if (!TextUtils.isEmpty(this.goods_title)) {
			dest.writeString(this.goods_title);
		} else {
			dest.writeString("");
		}
		if (this.goods_price != null) {
			dest.writeDouble(this.goods_price);
		} else {
			dest.writeDouble(0.0);
		}
		if (this.goods_weight != 0) {
			dest.writeDouble(this.goods_weight);
		} else {
			dest.writeDouble(0);
		}
		if (this.goods_attrs != null&&goods_attrs.size()>0) {
			dest.writeInt(goods_attrs.size());
			dest.writeList(this.goods_attrs);
		} else {
			dest.writeInt(0);
		}
		if (this.goods_img != null) {
			dest.writeParcelable(this.goods_img, arg1);
		}else{
			dest.writeParcelable(null, arg1);
		}
		if(!TextUtils.isEmpty(this.is_select)){
			dest.writeString(this.is_select);
		}else{
			dest.writeString("false");
		}
		
		if (this.goods_cnt != 0) {
			dest.writeInt(this.goods_cnt);
		} else {
			dest.writeInt(0);
		}
		if (this.vip_price != null) {
			dest.writeDouble(this.vip_price);
		} else {
			dest.writeDouble(0);
		}
		if(!TextUtils.isEmpty(this.is_vip)){
			dest.writeString(this.is_vip);
		}else{
			dest.writeString("false");
		}
		if (this.send_score != 0) {
			dest.writeInt(this.send_score);
		} else {
			dest.writeInt(0);
		}
	}

	public static final Creator<CartListBean> CREATOR = new Creator<CartListBean>() {

		@Override
		public CartListBean[] newArray(int arg0) {
			return new CartListBean[arg0];
		}

		@Override
		public CartListBean createFromParcel(Parcel dest) {
			CartListBean bean = new CartListBean();
			bean.setCart_id(dest.readInt());
			bean.setGoods_id(dest.readInt());
			bean.setCnt(dest.readInt());
			bean.setGoods_title(dest.readString());
			bean.setGoods_price(dest.readDouble());
			bean.setGoods_weight(dest.readDouble());
			int cnt = dest.readInt();
			if(cnt>0){
				bean.goods_attrs = new ArrayList<OrderPrdAttrsBean>();
				dest.readList(bean.goods_attrs, getClass().getClassLoader());
			}
			bean.goods_img = dest.readParcelable(ImageBigAndMinBean.class
					.getClassLoader());
			bean.setIs_select(dest.readString());
			bean.setGoods_cnt(dest.readInt());
			bean.setVip_price(dest.readDouble());
			bean.setIs_vip(dest.readString());
			bean.setSend_score(dest.readInt());
			return bean;
		}
	};

}
