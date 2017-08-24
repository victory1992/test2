package com.dingapp.biz.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dingapp.biz.db.orm.ConfigureBean;
import com.dingapp.core.app.Application;
import com.dingapp.core.db.CoreDbHelper;
import com.dingapp.core.db.dao.CoreDao;
import com.dingapp.core.db.dao.OrmObject;

public class ConfigureDao extends CoreDao {

	@Override
	protected String getTableName() {
		return "configure";
	}

	@Override
	protected String getPrimaryKey() {
		return "urlName";
	}

	@Override
	protected String[] getColumns() {
		String[] columns = new String[] {
				// "urlName" + TEXT_TYPE,
				"pageName" + TEXT_TYPE, "pageType" + TEXT_TYPE };
		return columns;
	}

	@Override
	protected String getPrimaryKeyName() {
		// TODO Auto-generated method stub
		return "configure";
	}

	@Override
	protected String getPrimaryKeyValue(OrmObject ormObj) {
		// TODO Auto-generated method stub
		return ((ConfigureBean) ormObj).getUrlName();
	}

	@Override
	protected OrmObject convertToOrmObj(Cursor cc) {
		String urlName = cc.getString(cc.getColumnIndex("urlName"));
		String pageName = cc.getString(cc.getColumnIndex("pageName"));
		String pageType = cc.getString(cc.getColumnIndex("pageType"));
		ConfigureBean bean = new ConfigureBean();
		bean.setUrlName(urlName);
		bean.setPageName(pageName);
		bean.setPageType(pageType);
		return bean;
	}

	@Override
	protected ContentValues convertToConstantValues(OrmObject ormObj) {
		ConfigureBean bean = (ConfigureBean) ormObj;
		ContentValues values = new ContentValues();
		values.put("urlName", bean.getUrlName());
		values.put("pageName", bean.getPageName());
		values.put("pageType", bean.getPageType());
		return values;
	}

	public ConfigureBean get(String urlName) {
		CoreDbHelper coreHelper = new CoreDbHelper(Application.getAppContext());
		SQLiteDatabase db = coreHelper.getReadableDatabase();
		try {
			Cursor cc = db.query(getTableName(), null, "pageName=?",
					new String[] { urlName }, null, null, null);
			if (cc.moveToNext()) {
				return (ConfigureBean) convertToOrmObj(cc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}

		return null;
	}

	public int getCount() {
		SQLiteOpenHelper dbHelper = getDbHelper();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			Cursor cc = db.query(getTableName(),
					new String[] { "count(*) as count" }, null, null, null,
					null, null);

			if (cc.moveToNext()) {
				return cc.getInt(cc.getColumnIndex("count"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
		return 0;
	}

	public void deleteAll() {
		SQLiteOpenHelper dbHelper = getDbHelper();
		super.deleteAll(dbHelper);

	}
}
