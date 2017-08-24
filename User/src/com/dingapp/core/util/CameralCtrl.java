package com.dingapp.core.util;

import java.io.File;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ImageView;

import com.dingapp.andriod.z20.R;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.imageloader.core.DisplayImageOptions;
import com.dingapp.imageloader.core.ImageLoader;
import com.dingapp.imageloader.core.imageware.ImageViewAware;

/**
 * 这个类封装了对摄像头的使用。 应用场景就是：使用摄像头拍照片，或者从相册中获取一张图片。
 * 
 * 这个类具有事务性。一次摄像头任务执行完毕之前，不会执行其他摄像头任务。
 * 
 * @author panda
 */
public class CameralCtrl {
	private static final int REQ_CAMERA = 0;
	public static final int REQ_ALBUM = 1;
	public static final int REQ_CROP = 2;

	private static int sWidth;
	private static int sHeight;
	private static boolean isDoingWork;
	private static BaseFragment sCallerFrg;
	private static String sDestFilePath;

	public static boolean start(BaseFragment frg, String destFilePath) {
		return start(frg, destFilePath, 1, 1);
	}

	public synchronized static boolean start(BaseFragment frg,
			String destFilePath, int width, int height) {
		if (!ThreadUtil.isMainThread()) {
			LoggerUtil
					.e(frg.getPagename(), "must start camera in main thread!");
			return false;
		}

		if (!UIUtil.isActValid(frg.getActivity())) {
			LoggerUtil.e(frg.getPagename(), "act is invalid");
			return false;
		}

		if (TextUtils.isEmpty(destFilePath)) {
			LoggerUtil.e(frg.getPagename(), "destFilePath must be valid");
			return false;
		}

		if (isDoingWork) {
			LoggerUtil.e(frg.getPagename(),
					"Cameral is doing work, can't do another work!");
			return false;
		}

		sCallerFrg = frg;
		sDestFilePath = destFilePath;
		isDoingWork = true;
		sWidth = width;
		sHeight = height;

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
				frg.getActivity());
		dialogBuilder.setTitle("选择图片");
		dialogBuilder.setItems(new String[] { "拍照", "从相册选取" },
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							openPhoto();
							break;
						case 1:
							openAlbum();
							break;
						}
						dialog.dismiss();
					}
				});
		dialogBuilder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				LoggerUtil.e(sCallerFrg.getPagename(),
						"user canceled Cameral job");
				finishCameralWork();
			}
		});
		dialogBuilder.create().show();
		return true;
	}

	public static boolean isOpenPhtoResult(BaseFragment frg, int requestCode) {
		if (frg != sCallerFrg) {
			LoggerUtil.e(frg.getPagename(),
					"CameralCtrl isn't doing work for you!");
			return false;
		}

		return requestCode == REQ_CAMERA;
	}

	public static void procOpenPhotoResult(BaseFragment frg, int resultCode,
			Intent data) {
		if (frg != sCallerFrg) {
			LoggerUtil.e(frg.getPagename(),
					"CameralCtrl isn't doing work for you!");
			return;
		}

		if (resultCode != android.app.Activity.RESULT_OK) {
			UIUtil.showToast(sCallerFrg.getActivity(), "拍照失败！");
			finishCameralWork();
			return;
		}

		File f = getTmpPortraitFile();
		if (f == null || !f.exists()) {
			UIUtil.showToast(sCallerFrg.getActivity(), "拍照失败！");
			finishCameralWork();
			return;
		}

		crop(Uri.fromFile(f));
	}

	public static boolean isOpenAlbumResult(BaseFragment frg, int requestCode) {
		if (frg != sCallerFrg) {
			LoggerUtil.e(frg.getPagename(),
					"CameralCtrl isn't doing work for you!");
			return false;
		}

		return requestCode == REQ_ALBUM;
	}

	public static void procOpenAlbumResult(BaseFragment frg, int resultCode,
			Intent data, ImageView iv, String defaultImgRes) {
		if (frg != sCallerFrg) {
			LoggerUtil.e(frg.getPagename(),
					"CameralCtrl isn't doing work for you!");
			return;
		}

		if (resultCode != android.app.Activity.RESULT_OK) {
			UIUtil.showToast(sCallerFrg.getActivity(), "访问相册失败！");
			finishCameralWork();
			return;
		}

		Uri uri = data.getData();
		if (uri == null) {
			UIUtil.showToast(sCallerFrg.getActivity(), "访问相册失败！");
			finishCameralWork();
			return;
		}

		crop(uri);
	}

	public static boolean isCropResult(BaseFragment frg, int requestCode) {
		if (frg != sCallerFrg) {
			LoggerUtil.e(frg.getPagename(),
					"CameralCtrl isn't doing work for you!");
			return false;
		}

		return requestCode == REQ_CROP;
	}

	public static void procCropResult(BaseFragment frg, int resultCode,
			Intent data, ImageView iv, String defaultImgRes) {
		if (frg != sCallerFrg) {
			LoggerUtil.e(frg.getPagename(),
					"CameralCtrl isn't doing work for you!");
			return;
		}

		if (resultCode != android.app.Activity.RESULT_OK) {
			UIUtil.showToast(sCallerFrg.getActivity(), "裁减失败！");
			finishCameralWork();
			return;
		}

		File f = getPortraitFile();
		if (f == null || !f.exists()) {
			UIUtil.showToast(sCallerFrg.getActivity(), "裁减失败！");
			finishCameralWork();
			return;
		}

		if (iv != null) {
			displayPortrait(Uri.fromFile(f).toString(), iv, defaultImgRes);
		}
		finishCameralWork();
	}

	private static void openPhoto() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File file = getTmpPortraitFile();
		if (file == null) {
			UIUtil.showToast(sCallerFrg.getActivity(), "拍照失败");
			finishCameralWork();
			return;
		}

		Uri uri = Uri.fromFile(file);
		if (file.exists()) {
			file.delete();
		}

		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		sCallerFrg.startActivityForResult(intent, REQ_CAMERA);
	}

	private static void openAlbum() {
		Intent openAlbumIntent = new Intent();
		openAlbumIntent.setAction(Intent.ACTION_PICK);
		openAlbumIntent.setType("image/*");
		sCallerFrg.startActivityForResult(openAlbumIntent, REQ_ALBUM);
	}

	private static void crop(Uri srcUri) {
		File file = getPortraitFile();
		if (file == null) {
			UIUtil.showToast(sCallerFrg.getActivity(), "裁减失败");
			finishCameralWork();
			return;
		}

		Uri destUri = Uri.fromFile(file);
		if (file.exists()) {
			file.delete();
		}

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(srcUri, "image/*");
		intent.putExtra("crop", true);
		intent.putExtra("scale", true);
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", sWidth);
		intent.putExtra("aspectY", sHeight);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 150 * sWidth);
		intent.putExtra("outputY", 150 * sHeight);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, destUri);
		sCallerFrg.startActivityForResult(intent, REQ_CROP);
	}

	// public static void refreashPortrait(File f, ImageView iv) {
	// displayPortrait(Uri.fromFile(f).toString(), iv);
	// }

	private static void displayPortrait(String url, ImageView iv,
			String defaultImgRes) {
		int defautImg = R.drawable.empty_photo;
		DisplayImageOptions.Builder options = new DisplayImageOptions.Builder();
		options.cacheInMemory(false);
		options.cacheOnDisk(false);
		options.showImageOnLoading(defautImg);
		options.showImageForEmptyUri(defautImg);
		options.showImageOnFail(defautImg);
		ImageViewAware viewAware = new ImageViewAware(iv);
		ImageLoader.getInstance().displayImage(url, viewAware, options.build());
	}

	// private static void displayPortrait(String url, ImageView iv_touxiang,
	// ImageLoadingListener listener) {
	// int defaultTouXiang =
	// ResourceParser.getDrawableResource("touxiang").intValue();
	// DisplayImageOptions.Builder options = new DisplayImageOptions.Builder();
	// options.showImageOnLoading(defaultTouXiang).showImageForEmptyUri(defaultTouXiang)
	// .showImageOnFail(defaultTouXiang);
	// ImageViewAware viewAware = new ImageViewAware(iv_touxiang);
	// ImageLoader.getInstance().displayImage(url, viewAware, options.build(),
	// listener);
	// }

	public static File getPortraitFile() {
		File f = null;
		try {
			String externalState = Environment.getExternalStorageState();
			if (TextUtils.equals(externalState, Environment.MEDIA_MOUNTED)) {
				File parentDir = sCallerFrg.getActivity().getExternalFilesDir(
						"cameral");
				f = new File(parentDir, sDestFilePath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return f;
	}

	private static File getTmpPortraitFile() {
		File f = null;
		String externalState = Environment.getExternalStorageState();
		if (TextUtils.equals(externalState, Environment.MEDIA_MOUNTED)) {
			File parentDir = sCallerFrg.getActivity().getExternalFilesDir(
					"cameral");
			f = new File(parentDir, sDestFilePath + ".tmp");
		}

		return f;
	}

	private static void finishCameralWork() {
		isDoingWork = false;
		sCallerFrg = null;
		sDestFilePath = null;
	}
}
