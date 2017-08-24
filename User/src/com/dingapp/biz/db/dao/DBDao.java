package com.dingapp.biz.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBDao {
	private SQLiteDatabase db;

	public DBDao(Context context, String fileName) {
		db = new DBManager(fileName).openDatabase(context);
	}

	/**
	 * @param parent_id
	 *            查找所以的省份 父id为1
	 * @return
	 */
	public List<Province> findAllProvicen(String parent_id) {
		List<Province> list = new ArrayList<Province>();
		Province bean = null;
		Cursor cursor = db
				.rawQuery(
						"select region_name,region_id,level from Region where level = ?",
						new String[] { parent_id });
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {

				bean = new Province();
				bean.level = (cursor.getString(cursor.getColumnIndex("level")));
				bean.parent_id = (parent_id);
				bean.region_id = (cursor.getString(cursor
						.getColumnIndex("region_id")));
				bean.region_name = (cursor.getString(cursor
						.getColumnIndex("region_name")));
				;
				list.add(bean);

			}
			cursor.close();
			return list;
		}
		return null;
	}

	/**
	 * @param parent_id
	 *            查找所有的城市
	 * @return
	 */
	public List<City> findAllCity(String parent_id) {
		List<City> list = new ArrayList<City>();
		City bean = null;
		Cursor cursor = db
				.rawQuery(
						"select region_name,region_id,level from Region where parent_id = ?",
						new String[] { parent_id });
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				bean = new City();
				bean.parent_id = (parent_id);
				String region_id = cursor.getString(cursor
						.getColumnIndex("region_id"));
				String region_name = cursor.getString(cursor
						.getColumnIndex("region_name"));
				String level = cursor.getString(cursor.getColumnIndex("level"));
				bean.region_id = (region_id);
				bean.region_name = (region_name);
				bean.level = (level);
				list.add(bean);
			}
			cursor.close();
			return list;
		}
		return null;
	}

	/**
	 * @param parent_id
	 *            市的id 查找对应市所以的区域
	 * @return
	 */
	public List<Area> findAllArea(String parent_id) {
		List<Area> list = new ArrayList<Area>();
		Area bean = null;
		Cursor cursor = db
				.rawQuery(
						"select region_name,region_id,level from Region where parent_id = ?",
						new String[] { parent_id });
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				bean = new Area();
				bean.parent_id = parent_id;
				String region_id = cursor.getString(cursor
						.getColumnIndex("region_id"));
				String region_name = cursor.getString(cursor
						.getColumnIndex("region_name"));
				String level = cursor.getString(cursor.getColumnIndex("level"));
				bean.region_id = region_id;
				bean.region_name = region_name;
				bean.level = level;
				list.add(bean);
			}
			cursor.close();
			return list;
		}
		return null;
	}

	/**
	 * @param region_id
	 *            查询省份 城市 区域名
	 * @return
	 */
	public String findName(String region_id) {
		String name = null;
		Cursor cursor = db.rawQuery(
				"select region_name from Region where region_id = ?",
				new String[] { region_id });
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				name = cursor.getString(cursor.getColumnIndex("region_name"));
			}
			cursor.close();
			return name;
		}
		return null;
	}

	public String findName(String region_name,String level) {
		String name = null;
		Cursor cursor = db
				.rawQuery(
						"select region_id from Region where region_name = ? and level = ?",
						new String[] { region_name, level });
		if(cursor != null &&cursor.getCount()>0){
			while(cursor.moveToNext()){
				name = cursor.getString(cursor.getColumnIndex("region_id"));
			}
			cursor.close();
			return name;
		}
		return null;
	}

	public class Province {
		public String region_id;
		public String region_name;
		public String level;
		public String parent_id;
		public List<City> cityList;
	}

	public class City {
		public String region_id;
		public String region_name;
		public String level;
		public String parent_id;
		public List<Area> areaList;
	}

	public class Area {
		public String region_id;
		public String region_name;
		public String level;
		public String parent_id;
	}
}
