package com.dingapp.biz.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;

import com.dingapp.andriod.z20.R;
import com.dingapp.imageloader.core.DisplayImageOptions;
import com.dingapp.imageloader.core.assist.ImageScaleType;
import com.dingapp.imageloader.core.display.BitmapDisplayer;
import com.dingapp.imageloader.core.display.RoundedBitmapDisplayer;
import com.dingapp.imageloader.core.display.SimpleBitmapDisplayer;

public class ImageUtils {

	/**
	 * 获得图片 旋转角度
	 * 
	 * @param filepath
	 * @return
	 */
	public static int getDigree(String filepath) {
		int digree = 0;
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(filepath);
			if (exif != null) {
				// 读取图片中相机方向信�?
				int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_UNDEFINED);
				// 计算旋转角度
				switch (ori) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					digree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					digree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					digree = 270;
					break;
				default:
					digree = 0;
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			exif = null;
		}
		return digree;
	}

	/**
	 * 裁剪图片
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap cutBmp(Bitmap bmp, int nw, int nh) {
		Bitmap result;
		int w = bmp.getWidth();// 输入长方形宽
		int h = bmp.getHeight();// 输入长方形高
		if (w > nw && h > nh) {
			// 长宽均大于nw
			result = Bitmap.createBitmap(bmp, (w - nw) / 2, (h - nh) / 2, nw,
					nh);
		} else {
			result = Bitmap.createBitmap(bmp, 0, 0, w, h);
		}
		bmp.recycle();
		return result;
	}

	/**
	 * 把Bitmap转Byte
	 * 
	 */
	public static byte[] Bitmap2Bytes(Bitmap bm, Bitmap.CompressFormat format) {
		if (bm == null) {
			return new byte[0];
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(format, 100, baos);
		byte[] data = baos.toByteArray();
		try {
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	public static Bitmap decodeSampledBitmapFromResource(String filePath,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		int width = options.outWidth;
		int height = options.outHeight;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int halfHeight = height;
			final int halfWidth = width;
			while ((halfHeight / inSampleSize) > reqHeight
					|| (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;

	}

	public static int calculateInSampleSize(int currWidth, int currHeight,
			int reqWidth, int reqHeight) {
		int width = currWidth;
		int height = currHeight;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int halfHeight = height;
			final int halfWidth = width;
			while ((halfHeight / inSampleSize) > reqHeight
					|| (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}

	public static byte[] getCompressBitmap(Bitmap bm,
			Bitmap.CompressFormat format) {
		byte[] smallData = null;
		try {
			int inSampleSize = 1;
			byte[] data = Bitmap2Bytes(bm, format);
			float length = data.length;

			while (length > 16 * 1024) {
				inSampleSize *= 2;
				length /= 4;
			}
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = false;
			options.inSampleSize = inSampleSize;
			Bitmap small = BitmapFactory.decodeByteArray(data, 0, data.length,
					options);
			smallData = Bitmap2Bytes(small, format);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return smallData;
	}

	/**
	 * 获取ImageLoader的加载参�?
	 * 
	 * @return
	 */
	public static DisplayImageOptions getImageOptions() {
		DisplayImageOptions options = null;
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		builder.showImageOnFail(R.drawable.empty_photo);
		builder.showImageOnLoading(R.drawable.empty_photo);
		builder.showImageForEmptyUri(R.drawable.empty_photo);
		builder.displayer(new SimpleBitmapDisplayer());
		builder.cacheInMemory(true);
		builder.cacheOnDisk(true);
		builder.considerExifParams(true);
		options = builder.build();
		return options;
	}

	/**
	 * 获取ImageLoader的加载参�?
	 * 
	 * @param id
	 *            默认图片id
	 * @return
	 */
	public static DisplayImageOptions getImageOptions(int id) {
		DisplayImageOptions options = null;
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		builder.showImageOnFail(id);
		builder.showImageOnLoading(id);
		builder.showImageForEmptyUri(id);
		builder.displayer(new SimpleBitmapDisplayer());
		builder.cacheInMemory(true);
		builder.cacheOnDisk(true);
		builder.considerExifParams(true);
		options = builder.build();
		return options;
	}

	/**
	 * 获取ImageLoader的加载参�?
	 * 
	 * @return
	 */
	public static DisplayImageOptions getImageRoundOptions() {
		DisplayImageOptions options = null;
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		builder.showImageOnFail(R.drawable.empty_photo);
		builder.showImageOnLoading(R.drawable.empty_photo);
		builder.showImageForEmptyUri(R.drawable.empty_photo);
		builder.displayer((BitmapDisplayer) new RoundedBitmapDisplayer(15));
		builder.cacheInMemory(true);
		builder.cacheOnDisk(true);
		builder.considerExifParams(true);
		options = builder.build();
		return options;
	}

	/**
	 * 获取本机扩展卡上的图�?
	 * 
	 * @return
	 */
	public static DisplayImageOptions getImageDiskOptions() {
		DisplayImageOptions options = null;
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		builder.showImageOnFail(R.drawable.empty_photo);
		builder.showImageOnLoading(R.drawable.empty_photo);
		builder.showImageForEmptyUri(R.drawable.empty_photo);
		builder.displayer(new SimpleBitmapDisplayer());
		builder.cacheInMemory(false);
		builder.cacheOnDisk(false);
		builder.imageScaleType(ImageScaleType.IN_SAMPLE_INT);
		builder.considerExifParams(true);
		options = builder.build();
		return options;
	}

}
