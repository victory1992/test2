package com.dingapp.core.db.dao;

import com.dingapp.core.app.Application;
import com.dingapp.core.db.CoreDbHelper;

import android.database.sqlite.SQLiteOpenHelper;

public abstract class CoreDao extends AbstractDingAppDao {
	@Override
	protected SQLiteOpenHelper getDbHelper() {
		return new CoreDbHelper(Application.getAppContext());
	}
}
