package com.dingapp.core.util;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.dingapp.core.app.Application;
import com.dingapp.imageloader.core.ImageLoader;

public class UIUtil {

	private static Toast t;

	public static boolean isActValid(Activity act) {
		if (act == null || act.isFinishing() || act.getWindow() == null) {
			return false;
		}

		return true;
	}

	public static Drawable ctorDrawable(Resources r, String drawableUrl) {
		Drawable dr = null;
		if (drawableUrl.startsWith("http://")) {
			// 这是网络资源，在LaunchActivity已经预下载了。
			File resFile = ImageLoader.getInstance().getDiskCache()
					.get(drawableUrl);
			dr = BitmapDrawable.createFromPath(resFile.getAbsolutePath());
		}

		if (dr == null) {
			// 这是本地资源
			dr = r.getDrawable(Application
					.getAppContext()
					.getResources()
					.getIdentifier(drawableUrl, "drawable",
							Application.getAppContext().getPackageName()));
		}

		return dr;
	}

	public static void hideKeyboard(Activity act) {
		if (act == null || act.isFinishing()) {
			return;
		}

		InputMethodManager imm = (InputMethodManager) act
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(act.getWindow().peekDecorView()
					.getWindowToken(), 0);
		}
	}

	public static void showToast(Activity act, String content) {
		try {
			if (t == null) {
				t = Toast.makeText(act, content, Toast.LENGTH_SHORT);
				t.setGravity(Gravity.CENTER, 0, 0);
			} else {
				t.setText(content);
			}
			t.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void showToast(Context context, String content) {
		try {
			if (t == null) {
				t = Toast.makeText(context, content, Toast.LENGTH_SHORT);
				t.setGravity(Gravity.CENTER, 0, 0);
			} else {
				t.setText(content);
			}

			t.show();
		} catch (Exception e) {
		}

	}
}
