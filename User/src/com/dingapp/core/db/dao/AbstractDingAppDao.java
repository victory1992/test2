package com.dingapp.core.db.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class AbstractDingAppDao implements Dao {

	protected abstract String getTableName();

	protected abstract String getPrimaryKey();

	protected abstract String[] getColumns();

	protected abstract String getPrimaryKeyName();

	protected abstract String getPrimaryKeyValue(OrmObject ormObj);

	protected abstract OrmObject convertToOrmObj(Cursor cc);

	protected abstract ContentValues convertToConstantValues(OrmObject ormObj);

	protected abstract SQLiteOpenHelper getDbHelper();

	/**
	 * 创建一条sql语句，用于创建表。
	 * 
	 * @return sql语句。
	 */
	public String createTblStat() {
		StringBuilder sb = new StringBuilder();
		sb.append(CREATE_TABLE);
		sb.append(getTableName());
		sb.append("(");
		sb.append(getPrimaryKey());
		for (String column : getColumns()) {
			sb.append(COMMA_SEP);
			sb.append(column);
		}
		sb.append(CREATE_TABLE_TAILER);
		return sb.toString();
	}

	/**
	 * 创建一条sql语句，用于删除表。
	 * 
	 * @return sql语句。
	 */
	public String dropTblStat() {
		return DROP_TABLE + getTableName();
	}

	/**
	 * 把OrmObject插入到数据库中。
	 * 
	 * @param proj
	 *            待添加到数据库的数据
	 * @return 新增记录的主键Id， 返回-1表示插入数据失败。
	 */
	public Long add(OrmObject proj) {
		SQLiteOpenHelper dbHelper = getDbHelper();
		ContentValues values = convertToConstantValues(proj);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			return db.insert(getTableName(), null, values);
		} catch (Exception e) {
			e.printStackTrace();
			return -1L;
		} finally {
			db.close();
		}
	}

	/**
	 * 更新数据库中的值ormObject
	 * 
	 * @param 待更新的值ormObj
	 */
	public void update(OrmObject ormObj) {
		SQLiteOpenHelper dbHelper = getDbHelper();
		ContentValues values = convertToConstantValues(ormObj);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String condition = getPrimaryKeyName() + "=?";
		String value = getPrimaryKeyValue(ormObj);
		try {
			db.update(getTableName(), values, condition, new String[] { value });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

	/**
	 * 查询数据库
	 * 
	 * @param id
	 *            表主键
	 * @return 返回查询结果
	 */
	public OrmObject getById(Object id) {
		SQLiteOpenHelper dbHelper = getDbHelper();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String condition = getPrimaryKeyName() + "=?";
		try {
			Cursor cc = db.query(getTableName(), null, condition,
					new String[] { String.valueOf(id) }, null, null, null);
			if (cc.moveToNext() && cc.isFirst() && cc.isLast()) {
				return convertToOrmObj(cc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
		return null;
	}

	/**
	 * 获取主键值大于startId的记录数量
	 * 
	 * @param startId
	 *            基线id
	 * @return 记录数量
	 */
	public int getCountBiggerThan(long startId) {
		SQLiteOpenHelper dbHelper = getDbHelper();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			Cursor cc = db.query(getTableName(),
					new String[] { "count(*) as count" }, getPrimaryKeyName()
							+ ">=?", new String[] { String.valueOf(startId) },
					null, null, null);

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

	/**
	 * 返回主键值小于或者等于startId的记录数量
	 * 
	 * @param startId
	 *            基线id
	 * @return 记录数量
	 */
	public int getCountNotBiggerThan(long startId) {
		SQLiteOpenHelper dbHelper = getDbHelper();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			Cursor cc = db.query(getTableName(),
					new String[] { "count(*) as count" }, getPrimaryKeyName()
							+ "<?", new String[] { String.valueOf(startId) },
					null, null, null);

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

	/**
	 * 获取数据库中，所有记录中最大主键值。
	 * 
	 * @return 主键值。
	 */
	public long getMaxId() {
		SQLiteOpenHelper dbHelper = getDbHelper();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			Cursor cc = db.query(getTableName(), new String[] { "max("
					+ getPrimaryKeyName() + ") as maxId" }, null, null, null,
					null, null);

			if (cc.moveToNext()) {
				return cc.getInt(cc.getColumnIndex("maxId"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
		return -1;
	}

	/**
	 * 获取最新的length条数据。
	 * 
	 * @param length
	 *            数据条数
	 * @return 数据库记录列表
	 */
	public List<OrmObject> getNewestLength(int length) {
		SQLiteOpenHelper dbHelper = getDbHelper();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			Cursor cc = db.query(getTableName(), null, null, null, null, null,
					getPrimaryKeyName() + " desc", String.valueOf(length));

			List<OrmObject> ormObjects = new ArrayList<OrmObject>();
			while (cc.moveToNext()) {
				ormObjects.add(convertToOrmObj(cc));
			}
			return ormObjects;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
		return null;
	}

	/**
	 * 获取主键值比startId大的length条数据
	 * 
	 * @param startId
	 *            基线id
	 * @param length
	 *            数据数量
	 * @return 记录列表
	 */
	public List<OrmObject> getBiggerThan(long startId, int length) {
		SQLiteOpenHelper dbHelper = getDbHelper();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			Cursor cc = db.query(getTableName(), null, getPrimaryKeyName()
					+ ">?", new String[] { String.valueOf(startId) }, null,
					null, getPrimaryKeyName() + " asc", String.valueOf(length));

			List<OrmObject> ormObjects = new ArrayList<OrmObject>();
			while (cc.moveToNext()) {
				ormObjects.add(convertToOrmObj(cc));
			}
			Collections.reverse(ormObjects);
			return ormObjects;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
		return null;
	}

	/**
	 * 获取主键值小于或者等于startId的length条数据。
	 * 
	 * @param startId
	 *            基线id
	 * @param length
	 *            数据条数
	 * @return 数据列表
	 */
	public List<OrmObject> getNotBiggerThan(long startId, int length) {
		SQLiteOpenHelper dbHelper = getDbHelper();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			Cursor cc = db
					.query(getTableName(), null, getPrimaryKeyName() + "<=?",
							new String[] { String.valueOf(startId) }, null,
							null, getPrimaryKeyName() + " desc",
							String.valueOf(length));

			List<OrmObject> ormObjects = new ArrayList<OrmObject>();
			while (cc.moveToNext()) {
				ormObjects.add(convertToOrmObj(cc));
			}
			return ormObjects;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
		return null;
	}

	/**
	 * 获取与member有关的，最新的length条数据。
	 * 
	 * @param memberId
	 *            会员Member表的主键值
	 * @param length
	 *            数据条数
	 * @return 数据记录列表
	 */
	public List<OrmObject> getMineNewestLength(Long memberId, int length) {
		SQLiteOpenHelper dbHelper = getDbHelper();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String condition = "ownerId=?";
		String[] conditionValue = new String[] { String.valueOf(memberId) };
		try {
			Cursor cc = db.query(getTableName(), null, condition,
					conditionValue, null, null, getPrimaryKeyName() + " desc",
					String.valueOf(length));

			List<OrmObject> ormObjects = new ArrayList<OrmObject>();
			while (cc.moveToNext()) {
				ormObjects.add(convertToOrmObj(cc));
			}
			return ormObjects;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
		return null;
	}

	/**
	 * 返回主键值大于startId的且与memberId有关的记录数量
	 * 
	 * @param memberId
	 *            会员id值，Member表的主键值
	 * @param startId
	 *            基线id
	 * @return 记录数量
	 */
	public int getMineCountBiggerThan(Long memberId, long startId) {
		SQLiteOpenHelper dbHelper = getDbHelper();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String condition = getPrimaryKeyName() + ">? and " + "ownerId=?";
		String[] conditionValue = new String[] { String.valueOf(startId),
				String.valueOf(memberId) };
		try {
			Cursor cc = db.query(getTableName(),
					new String[] { "count(*) as count" }, condition,
					conditionValue, null, null, null);

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

	/**
	 * 返回主键值小于或者等于startId的，且与memberId有关的记录数量。
	 * 
	 * @param memberId
	 *            会员id值，Member表的主键。
	 * @param startId
	 *            基线id
	 * @return 记录数量
	 */
	public int getMineCountNotBiggerThan(Long memberId, long startId) {
		SQLiteOpenHelper dbHelper = getDbHelper();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String condition = getPrimaryKeyName() + "<=? and " + "ownerId=?";
		String[] conditionValue = new String[] { String.valueOf(startId),
				String.valueOf(memberId) };
		try {
			Cursor cc = db.query(getTableName(),
					new String[] { "count(*) as count" }, condition,
					conditionValue, null, null, null);

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

	/**
	 * 获取与memberId有关的，主键值大于startId的最新length条数据。
	 * 
	 * @param memberId
	 *            会员表主键值
	 * @param startId
	 *            基准id
	 * @param length
	 *            数据长度
	 * @return 记录列表
	 */
	public List<OrmObject> getMineBiggerThan(long memberId, long startId,
			int length) {
		SQLiteOpenHelper dbHelper = getDbHelper();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String condition = getPrimaryKeyName() + ">? and " + "ownerId=?";
		String[] conditionValue = new String[] { String.valueOf(startId),
				String.valueOf(memberId) };
		try {
			Cursor cc = db.query(getTableName(), null, condition,
					conditionValue, null, null, getPrimaryKeyName() + " asc",
					String.valueOf(length));

			List<OrmObject> ormObjects = new ArrayList<OrmObject>();
			while (cc.moveToNext()) {
				ormObjects.add(convertToOrmObj(cc));
			}
			Collections.reverse(ormObjects);
			return ormObjects;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
		return null;
	}

	/**
	 * 获取与memberId有关的，主键值小于或者等于startId的最新length条数据。
	 * 
	 * @param memberId
	 *            会员表主键值
	 * @param startId
	 *            基准id
	 * @param length
	 *            数据长度
	 * @return 记录列表
	 */
	public List<OrmObject> getMineNotBiggerThan(long memberId, long startId,
			int length) {
		SQLiteOpenHelper dbHelper = getDbHelper();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String condition = getPrimaryKeyName() + "<=? and " + "ownerId=?";
		String[] conditionValue = new String[] { String.valueOf(startId),
				String.valueOf(memberId) };
		try {
			Cursor cc = db.query(getTableName(), null, condition,
					conditionValue, null, null, getPrimaryKeyName() + " desc",
					String.valueOf(length));

			List<OrmObject> ormObjects = new ArrayList<OrmObject>();
			while (cc.moveToNext()) {
				ormObjects.add(convertToOrmObj(cc));
			}
			return ormObjects;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
		return null;
	}

	/**
	 * 删除记录ormObj
	 * 
	 * @param ormObj
	 *            等待删除的记录
	 */
	public void delete(OrmObject ormObj) {
		SQLiteOpenHelper dbHelper = getDbHelper();
		SQLiteDatabase writeDb = dbHelper.getWritableDatabase();
		try {
			writeDb.delete(getTableName(), getPrimaryKeyName() + "=?",
					new String[] { getPrimaryKeyValue(ormObj) });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			writeDb.close();
		}
	}

	public List<OrmObject> getAll(SQLiteOpenHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			Cursor cc = db.query(getTableName(), null, null, null, null, null,
					getPrimaryKeyName() + " desc");

			List<OrmObject> ormObjects = new ArrayList<OrmObject>();
			while (cc.moveToNext()) {
				ormObjects.add(convertToOrmObj(cc));
			}
			return ormObjects;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
		return null;
	}

	public void deleteAll(SQLiteOpenHelper dbHelper) {
		SQLiteDatabase writeDb = dbHelper.getWritableDatabase();
		try {
			writeDb.delete(getTableName(), null, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			writeDb.close();
		}
	}
}
