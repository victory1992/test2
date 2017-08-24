package com.dingapp.core.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.dingapp.core.app.Application;
import com.dingapp.core.db.CoreDbHelper;
import com.dingapp.core.db.dao.OrmObject;
import com.dingapp.core.db.orm.Member;

public class MemberDao extends CoreDao {

	@Override
	protected String getTableName() {
		return "Member";
	}

	@Override
	protected String getPrimaryKey() {
		return "memberId" + INTEGER_TYPE + PRIMARY_KEY;
	}

	@Override
	protected String[] getColumns() {
		String[] columns = new String[] { "login" + TEXT_TYPE,
				"session" + TEXT_TYPE, "checkCode" + TEXT_TYPE,
				"token" + TEXT_TYPE, "origin" + TEXT_TYPE,
				"headerProfile" + TEXT_TYPE, "nickName" + TEXT_TYPE,
				"real_name" + TEXT_TYPE, "score" + INTEGER_TYPE,
				"wx_nick_name" + TEXT_TYPE, "birthday" + TEXT_TYPE,
				"otherString1" + TEXT_TYPE, "otherString2" + TEXT_TYPE,
				"otherString3" + TEXT_TYPE, "otherString4" + TEXT_TYPE,
				"otherString5" + TEXT_TYPE };
		return columns;
	}

	public Long add(Member member) {
		// 本地只有一个会员
		deleteAll();
		return super.add(member);
	}

	/**
	 * 获取登录的会员信息。这里只包含核心信息，包括：主键id，登录名，会员session。
	 * 
	 * @return 会员信息。如果为null，表示没有登录。
	 */
	public Member get() {
		CoreDbHelper coreHelper = new CoreDbHelper(Application.getAppContext());
		SQLiteDatabase db = coreHelper.getReadableDatabase();
		try {
			Cursor cc = db.query(getTableName(), null, null, null, null, null,
					null);
			if (cc.moveToNext()) {
				return (Member) convertToOrmObj(cc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}

		return null;
	}

	/**
	 * 清除登录信息
	 */
	public void deleteAll() {
		CoreDbHelper coreHelper = new CoreDbHelper(Application.getAppContext());
		SQLiteDatabase db = coreHelper.getWritableDatabase();
		try {
			db.delete(getTableName(), null, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

	@Override
	public String getPrimaryKeyName() {
		return "memberId";
	}

	@Override
	public String getPrimaryKeyValue(OrmObject ormObj) {
		return String.valueOf(((Member) ormObj).getMemberId());
	}

	@Override
	protected OrmObject convertToOrmObj(Cursor cc) {
		long memberId = cc.getLong(cc.getColumnIndex("memberId"));
		String login = cc.getString(cc.getColumnIndex("login"));
		String sessionId = cc.getString(cc.getColumnIndex("session"));
		String checkCode = cc.getString(cc.getColumnIndex("checkCode"));
		String token = cc.getString(cc.getColumnIndex("token"));
		String origin = cc.getString(cc.getColumnIndex("origin"));
		String headerProfile = cc.getString(cc.getColumnIndex("headerProfile"));
		String nickName = cc.getString(cc.getColumnIndex("nickName"));
		String real_name = cc.getString(cc.getColumnIndex("real_name"));
		String wx_nick_name = cc.getString(cc.getColumnIndex("wx_nick_name"));
		int score = cc.getInt(cc.getColumnIndex("score"));
		String birthday = cc.getString(cc.getColumnIndex("birthday"));
		String otherString1 = cc.getString(cc.getColumnIndex("otherString1"));
		String otherString2 = cc.getString(cc.getColumnIndex("otherString2"));
		String otherString3 = cc.getString(cc.getColumnIndex("otherString3"));
		String otherString4 = cc.getString(cc.getColumnIndex("otherString4"));
		String otherString5 = cc.getString(cc.getColumnIndex("otherString5"));
		cc.close();
		Member ormMember = new Member();
		ormMember.setMemberId(memberId);
		ormMember.setLoginName(login);
		ormMember.setSessionId(sessionId);
		ormMember.setCheckCode(checkCode);
		ormMember.setToken(token);
		ormMember.setOrigin(origin);
		ormMember.setHeaderProfile(headerProfile);
		ormMember.setNickName(nickName);
		ormMember.setReal_name(real_name);
		ormMember.setScore(score);
		ormMember.setWx_nick_name(wx_nick_name);
		ormMember.setBirthday(birthday);
		ormMember.setOtherString1(otherString1);
		ormMember.setOtherString2(otherString2);
		ormMember.setOtherString3(otherString3);
		ormMember.setOtherString4(otherString4);
		ormMember.setOtherString5(otherString5);
		return ormMember;
	}

	@Override
	protected ContentValues convertToConstantValues(OrmObject ormObj) {
		Member ormMember = (Member) ormObj;
		ContentValues values = new ContentValues();
		values.put("memberId", ormMember.getMemberId());
		values.put("login", ormMember.getLoginName());
		values.put("session", ormMember.getSessionId());
		values.put("checkCode", ormMember.getCheckCode());
		values.put("token", ormMember.getToken());
		values.put("origin", ormMember.getOrigin());
		values.put("headerProfile", ormMember.getHeaderProfile());
		values.put("nickName", ormMember.getNickName());
		values.put("real_name", ormMember.getReal_name());
		values.put("score", ormMember.getScore());
		values.put("wx_nick_name", ormMember.getWx_nick_name());
		values.put("birthday", ormMember.getBirthday());
		values.put("otherString1", ormMember.getOtherString1());
		values.put("otherString2", ormMember.getOtherString2());
		values.put("otherString3", ormMember.getOtherString3());
		values.put("otherString4", ormMember.getOtherString4());
		values.put("otherString5", ormMember.getOtherString5());
		return values;
	}
}
