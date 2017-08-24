package com.dingapp.core.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dingapp.biz.db.dao.CacheDao;
import com.dingapp.core.db.dao.AbstractDingAppDao;
import com.dingapp.core.db.dao.MemberDao;
import com.dingapp.core.util.LoggerUtil;

public class CoreDbHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "dingapp_core.db";

	public CoreDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase db) {
		LoggerUtil.i("DB", "onCreate");
		AbstractDingAppDao memberDao = new MemberDao();
		db.execSQL(memberDao.createTblStat());
		createNew(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		AbstractDingAppDao memberDao = new MemberDao();
//		db.execSQL(memberDao.dropTblStat());
//		AbstractDingAppDao configureDao = new ConfigureDao();
//		db.execSQL(configureDao.dropTblStat());
//		AbstractDingAppDao caccheDao = new CacheDao();
//		db.execSQL(caccheDao.dropTblStat());
//		onCreate(db);
		if(newVersion == 2){
			createNew(db);
		}
		
	}
	private void createNew(SQLiteDatabase db){
		AbstractDingAppDao caccheDao = new CacheDao();
		db.execSQL(caccheDao.createTblStat());
	}
}