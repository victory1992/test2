package com.dingapp.biz.util;

import com.dingapp.core.util.Constants;

import android.graphics.Point;

public class FocusPicUtil {
	private static Point sInforFocusPicWH;

	public static Point getFocusPictureWH() {
		if (sInforFocusPicWH == null) {
			/* 资讯类焦点图的宽高比是2:1 */
			int width = Constants.getScreenWidth();
			int height = width / 2;
			sInforFocusPicWH = new Point(width, height);
		}

		return sInforFocusPicWH;
	}
}
