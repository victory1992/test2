package com.dingapp.biz.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dingapp.biz.db.orm.CacheData;
import com.dingapp.core.app.Application;
import com.dingapp.core.db.CoreDbHelper;
import com.dingapp.core.db.dao.CoreDao;
import com.dingapp.core.db.dao.OrmObject;

public class CacheDao extends CoreDao {

	@Override
	protected String getTableName() {
		return "cache_data";
	}

	@Override
	protected String getPrimaryKey() {
		return "class_name";
	}

	@Override
	protected String[] getColumns() {
		String[] columns = new String[] { "type_name" + TEXT_TYPE,
				"talk_id" + TEXT_TYPE, "act_id" + TEXT_TYPE,
				"json_content" + TEXT_TYPE };
		return columns;
	}

	@Override
	protected String getPrimaryKeyName() {
		return "class_name";
	}

	@Override
	protected String getPrimaryKeyValue(OrmObject ormObj) {
		return ((CacheData) ormObj).getClass_name();
	}
	@Override
	public Long add(OrmObject proj) {
		delete(proj);
		return super.add(proj);
	}
	@Override
	protected OrmObject convertToOrmObj(Cursor cc) {
		String class_name = cc.getString(cc.getColumnIndex("class_name"));
		String type_name = cc.getString(cc.getColumnIndex("type_name"));
		String talk_id = cc.getString(cc.getColumnIndex("talk_id"));
		String act_id = cc.getString(cc.getColumnIndex("act_id"));
		String json_content = cc.getString(cc.getColumnIndex("json_content"));
		CacheData bean = new CacheData();
		bean.setClass_name(class_name);
		bean.setType_name(type_name);
		bean.setTalk_id(talk_id);
		bean.setAct_id(act_id);
		bean.setJson_content(json_content);
		cc.close();
		return bean;
	}

	@Override
	protected ContentValues convertToConstantValues(OrmObject ormObj) {
		ContentValues values = new ContentValues();
		CacheData bean = (CacheData) ormObj;
		values.put("class_name", bean.getClass_name());
		values.put("type_name", bean.getType_name());
		values.put("talk_id", bean.getTalk_id());
		values.put("act_id", bean.getAct_id());
		values.put("json_content", bean.getJson_content());
		return values;
	}

	// 获取首页数据
	public CacheData getHomeDataByClassName(String class_name) {
		CoreDbHelper coreHelper = new CoreDbHelper(Application.getAppContext());
		SQLiteDatabase db = coreHelper.getReadableDatabase();
		try {
			Cursor cc = db.query(getTableName(), null, "class_name=?",
					new String[] { class_name }, null, null, null);
			if (cc.moveToNext()) {
				return (CacheData) convertToOrmObj(cc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
		return null;
	}

	public void deleteAll() {
		SQLiteOpenHelper dbHelper = getDbHelper();
		super.deleteAll(dbHelper);

	}

}
