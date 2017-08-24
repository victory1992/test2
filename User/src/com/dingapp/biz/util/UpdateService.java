package com.dingapp.biz.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.pay.LogUtils;

/**
 * 下载更新服务
 * 
 * @author
 * 
 */
public class UpdateService extends Service {

	private static final int NOTIFICATION_ID = Integer.MAX_VALUE;

	private NotificationManager manager;

	private NotificationCompat.Builder builder;

	/**
	 * 接收程序退出广播
	 */
	private EixtRecivier receiver;

	@Override
	public void onCreate() {
		super.onCreate();
		init();
		receiver = new EixtRecivier();
		IntentFilter filter = new IntentFilter();
		filter.addAction(AppConstants.EXIT_ACTION);
		registerReceiver(receiver, filter);
		downloadFile();
	}

	// 通知栏跳转Intent
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, 0, 0);
	}

	private void init() {
		manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	/**
	 * 下载文件
	 */
	private void downloadFile() {
		LogUtils.d("pb", "开始下载");
		builder = new NotificationCompat.Builder(this);
		builder.setSmallIcon(R.drawable.share_logo);
		builder.setTicker("showProgressBar");
		builder.setOngoing(true);
		builder.setContentTitle("汉固达商城");
		builder.setContentText("正在下载");
		manager.notify(NOTIFICATION_ID, builder.build());
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String downloadUrl = UpdateAppUtil.getDownLoadUrl();
					downloadUpdateFile(downloadUrl);
				} catch (Exception e) {
					mHandler.sendEmptyMessage(0);
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 获取下载放置app的路径
	 * 
	 * @return
	 */
	private File getAppFile() {
		String path = Environment.getExternalStorageDirectory() + "/data/"
				+ this.getPackageName() + "/app/z20.apk";
		File file = new File(path);
		File parentFile = file.getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		return file;
	}

	// 下载
	/**
	 * 
	 * @param downloadUrl
	 *            下载地址
	 * @param builder
	 *            创建notification的builder
	 * @throws Exception
	 */
	@SuppressLint("WorldReadableFiles")
	public void downloadUpdateFile(String downloadUrl) throws Exception {
		long totalSize = 0;
		long updateTotalSize = 0;
		HttpURLConnection httpConnection = null;
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			URL url = new URL(downloadUrl);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setConnectTimeout(10000);
			httpConnection.setReadTimeout(20000);
			updateTotalSize = httpConnection.getContentLength();
			if (httpConnection.getResponseCode() / 100 != 2) {
				throw new Exception("download fail!");
			}
			is = httpConnection.getInputStream();
			fos = new FileOutputStream(getAppFile());
			long currTime = 0;
			byte buffer[] = new byte[4096];
			int readsize = 0;
			while ((readsize = is.read(buffer)) > 0) {
				fos.write(buffer, 0, readsize);
				totalSize += readsize;
				int progress = (int) ((totalSize * 100.0) / updateTotalSize);
				long tempTime = System.currentTimeMillis();
				if (tempTime - currTime > 1000) {
					Message msg = Message.obtain();
					msg.what = 1;
					msg.arg1 = progress;
					mHandler.sendMessage(msg);
					currTime = tempTime;
				}
			}
			mHandler.sendEmptyMessage(2);

		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
			if (is != null) {
				is.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				LogUtils.d("pb", "下载失败");
				Toast.makeText(getApplicationContext(), "下载失败",
						Toast.LENGTH_LONG).show();
				builder.setContentText("下载失败");
				builder.setProgress(0, 0, false);
				builder.setOngoing(false);
				manager.notify(NOTIFICATION_ID, builder.build());

				// TODO 下载失败杀死服务
				stopSelf();
				break;
			case 1:
				int progress = msg.arg1;
				builder.setProgress(100, progress, false);
				builder.setContentInfo(progress + "%");
				manager.notify(NOTIFICATION_ID, builder.build());
				break;
			case 2:
				builder.setContentText("下载完成，点击安装");
				Toast.makeText(getApplicationContext(), "下载完成,请去顶部菜单栏中安装",
						Toast.LENGTH_LONG).show();
				builder.setProgress(0, 0, false);
				builder.setContentInfo("100%");
				builder.setOngoing(false);
				builder.setAutoCancel(true);
				Intent in = new Intent(Intent.ACTION_VIEW);
				in.setDataAndType(Uri.fromFile(getAppFile()),
						"application/vnd.android.package-archive");
				in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				PendingIntent pi = PendingIntent.getActivity(
						UpdateService.this, 0, in, PendingIntent.FLAG_ONE_SHOT);
				builder.setContentIntent(pi);
				manager.notify(NOTIFICATION_ID, builder.build());
				// TODO 下载成功杀死服务
				stopSelf();
				break;
			}
		}

	};

	/**
	 * 接收程序退出的广播接收者
	 * 
	 * @author pb
	 * 
	 */
	private class EixtRecivier extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			stopSelf();
		}

	}

	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
