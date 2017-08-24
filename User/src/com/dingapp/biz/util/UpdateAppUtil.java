package com.dingapp.biz.util;

import java.util.HashMap;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.net.StringPostRequest;
import com.dingapp.biz.pay.LogUtils;

public class UpdateAppUtil {
	private Context context;
	private RequestQueue mQueue;
	private boolean isNeedUpdate = true;
	/**
	 * 更细app提示对话框
	 */
	private static String download_url;
	private static String download_appname = "汉固达商城";
	private Listener<String> listener = new Listener<String>() {
		@Override
		public void onResponse(String response) {
			LogUtils.d("pb", "response : " + response);
			parseData(response);
		}
	};

	private static ErrorListener errorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
		}
	};

	public UpdateAppUtil(Context context, RequestQueue mQueue) {
		this.context = context;
		this.mQueue = mQueue;
	}

	public void requestData(Context context, boolean isShow) {
		this.context = context;
		this.isNeedUpdate = isShow;
		String url = AppConstants.BaseUrl + AppConstants.newset_app;
		HashMap<String, String> postMap = new HashMap<String, String>();
		postMap.put("platform", AppConstants.PLATFORM);
		StringPostRequest request = new StringPostRequest(postMap, url,
				listener, errorListener);
		mQueue.add(request);
	}

	public boolean isNeedUpdate() {
		return isNeedUpdate;
	}

	/**
	 * 解析数据
	 * 
	 * @param json
	 *            接口请求回来的json数据
	 * 
	 */
	private void parseData(String json) {
		try {
			JSONObject jsonF = new JSONObject(json);
			JSONObject jsonObj = jsonF.getJSONObject("data");
			String statusCode = jsonF.getString("statusCode");
			if (statusCode.equals("200")) {
				if (jsonObj.has("version")) {
					int version_code = jsonObj.getInt("version");
					download_url = jsonObj.getString("download_url");
					String update_msg = jsonObj.getString("update_msg");
					int curr_code = getVersionCode(context);
					if (version_code > curr_code) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								context);
						builder.setMessage(update_msg);
						builder.setTitle("升级提示!");
						builder.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										Intent intent = new Intent(context,
												UpdateService.class);
										intent.setPackage(context
												.getPackageName());
										intent.setAction("com.dingapp.z20.update.UpdateService");
										context.startService(intent);
									}
								});
						builder.create().show();
						LogUtils.d("pb", "打开更新对话框");
					} else {
						if (isNeedUpdate) {
							Toast.makeText(context, "当前为最新版本",
									Toast.LENGTH_LONG).show();
						}
					}

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getDownLoadUrl() {
		return download_url;
	}

	public static void setDownloadUrl(String url) {
		download_url = url;
	}

	public static String getDownLoadAppName() {
		return download_appname;
	}

	public static void setDownLoadAppName(String app_name) {
		download_appname = app_name;
	}

	/**
	 * 获取应用版本号
	 * 
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			int versionCode = info.versionCode;
			return versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Integer.MAX_VALUE;
	}
}
