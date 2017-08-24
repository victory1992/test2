package com.dingapp.biz.db.orm;

import java.util.List;

public class HomeBean {
	private List<GoodsFocuPicsBean> focu_pics;
	private List<TypeInfoBean> type_info;
	private ImageBigAndMinBean adv_img;
	private List<FullScreenAdvImgBean> extend_adv_info;
	private List<GoodsBean> goods;
	public List<GoodsFocuPicsBean> getFocu_pics() {
		return focu_pics;
	}
	public void setFocu_pics(List<GoodsFocuPicsBean> focu_pics) {
		this.focu_pics = focu_pics;
	}
	public List<TypeInfoBean> getType_info() {
		return type_info;
	}
	public void setType_info(List<TypeInfoBean> type_info) {
		this.type_info = type_info;
	}
	public ImageBigAndMinBean getAdv_img() {
		return adv_img;
	}
	public void setAdv_img(ImageBigAndMinBean adv_img) {
		this.adv_img = adv_img;
	}
	public List<FullScreenAdvImgBean> getExtend_adv_info() {
		return extend_adv_info;
	}
	public void setExtend_adv_info(List<FullScreenAdvImgBean> extend_adv_info) {
		this.extend_adv_info = extend_adv_info;
	}
	public List<GoodsBean> getGoods() {
		return goods;
	}
	public void setGoods(List<GoodsBean> goods) {
		this.goods = goods;
	}
	
}
