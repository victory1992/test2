package com.dingapp.biz.db.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBManager {

	private String filePath = null;
	private String pathStr = "data/data/com.dingapp.andriod.z20";
	private String fileName = null;
	SQLiteDatabase database;

	public DBManager(String fileName) {
		this.fileName = fileName;
		this.filePath = pathStr + "/" + fileName;
	}

	public SQLiteDatabase openDatabase(Context context) {
		System.out.println("filePath:" + filePath);
		File jhPath = new File(filePath);
		if (jhPath.exists()) {
			Log.i("test", "数据库存在");
			return SQLiteDatabase.openOrCreateDatabase(jhPath, null);
		} else {
			File path = new File(pathStr);
			Log.i("test", "pathStr=" + path);
			if (path.mkdirs()) {
				Log.i("test", "数据库创建成功");
			} else {
				Log.i("test", "数据库创建失败");
			}
			;
			try {
				AssetManager am = context.getAssets();
				InputStream is = am.open(fileName);
				FileOutputStream fos = new FileOutputStream(jhPath);
				Log.i("test", "fos=" + fos);
				Log.i("test", "jhPath=" + jhPath);
				byte[] buffer = new byte[1024];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.flush();
				fos.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			return openDatabase(context);
		}
	}
}
