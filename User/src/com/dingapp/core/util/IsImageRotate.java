package com.dingapp.core.util;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;

public class IsImageRotate {
	public static Bitmap getRotateBitmap(String filepath) {
		if (!TextUtils.isEmpty(filepath)) {
			int angle = getExifOrientation(filepath);

			if (angle != 0) {
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inSampleSize = 4;
				Bitmap photoViewBitmap = BitmapFactory.decodeFile(filepath,
						opts);
				Matrix matrix = new Matrix();
				matrix.postRotate(angle);
				photoViewBitmap = Bitmap.createBitmap(photoViewBitmap, 0, 0,
						photoViewBitmap.getWidth(),
						photoViewBitmap.getHeight(), matrix, true);
				return photoViewBitmap;
			}
		}
		return null;
	}

	/**
	 * 得到 图片旋转 的角度
	 * 
	 * @param filepath
	 * @return
	 */
	private static int getExifOrientation(String filepath) {
		int degree = 0;
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(filepath);
		} catch (IOException ex) {
		}
		if (exif != null) {
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, -1);
			if (orientation != -1) {
				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
				}
			}
		}
		return degree;
	}
}
