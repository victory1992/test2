package com.dingapp.biz.util;


public class ImageViewHWutils {
	public static String getWHImageVIew(int width, int height) {
		StringBuilder url = new StringBuilder();
		url.append("&w=");
		url.append(width*2);
		url.append("&h=");
		url.append(height*2);
		url.append("&session_id=");
		url.append("");
		return url.toString();
	}

}
