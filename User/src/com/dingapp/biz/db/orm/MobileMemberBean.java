package com.dingapp.biz.db.orm;

import android.graphics.Bitmap;

public class MobileMemberBean {
	private String name;
	private String phone;
	private boolean checked;
	 /**
     * 昵称首字母
     */
	protected String initialLetter;
	/**
	 * 头像
	 * @return
	 */
	private Bitmap headerBitmap;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getInitialLetter() {
		return initialLetter;
	}
	public void setInitialLetter(String initialLetter) {
		this.initialLetter = initialLetter;
	}
	public Bitmap getHeaderBitmap() {
		return headerBitmap;
	}
	public void setHeaderBitmap(Bitmap headerBitmap) {
		this.headerBitmap = headerBitmap;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
}
