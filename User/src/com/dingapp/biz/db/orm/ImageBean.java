package com.dingapp.biz.db.orm;

import java.io.Serializable;

/**
 * 向服务器发送图片的bean
 * 
 * @author
 * 
 */
public class ImageBean implements Serializable {
	private String name;// 接口名称
	private String fileName;// 文件名称
	private String type;// 图片类型
	private byte[] data;// 图像数据

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getType() {
		String str = "image/png";
		if (type.endsWith("png")) {
			str = "image/png";
		} else if (type.endsWith("jpeg")) {
			str = "image/jpeg";
		} else if (type.endsWith("jpg")) {
			str = "image/jpeg";
		}
		return str;
	}

	public void setType(String type) {
		this.type = type;
	}

}
