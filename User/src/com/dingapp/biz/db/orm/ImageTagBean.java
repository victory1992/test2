package com.dingapp.biz.db.orm;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 图片imageView携带的tag;
 * 
 * @author
 * 
 */
public class ImageTagBean implements Serializable {
	/**
	 * 当前图片的位置
	 */
	private int position;

	/**
	 * 所有图片名称集合
	 */
	private ArrayList<?> imgList;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public ArrayList<?> getImgList() {
		return imgList;
	}

	public void setImgList(ArrayList<?> imgList) {
		this.imgList = imgList;
	}

}
