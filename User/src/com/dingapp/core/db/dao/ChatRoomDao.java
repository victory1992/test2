package com.dingapp.core.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.dingapp.core.app.Application;
import com.dingapp.core.db.CoreDbHelper;
import com.dingapp.core.db.orm.ChatRoom;
import com.dingapp.core.db.orm.Message;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ChatRoomDao extends CoreDao {

	@Override
	protected String getTableName() {
		// TODO Auto-generated method stub
		return "ChatRoom";
	}

	@Override
	protected String getPrimaryKey() {
		// TODO Auto-generated method stub
		return "roomId" + INTEGER_TYPE + PRIMARY_KEY;
	}

	@Override
	protected String[] getColumns() {
		String[] columns = new String[] { "memberCnt" + INTEGER_TYPE };
		return columns;
	}

	@Override
	protected String getPrimaryKeyName() {

		return "roomId";
	}

	@Override
	protected String getPrimaryKeyValue(OrmObject ormObj) {
		return String.valueOf(((ChatRoom) ormObj).getRoomId());
	}

	@Override
	protected OrmObject convertToOrmObj(Cursor cc) {
		long roomId = cc.getLong(cc.getColumnIndex("roomId"));
		int memberCnt = cc.getInt(cc.getColumnIndex("memberCnt"));

		ChatRoom chatRoom = new ChatRoom();
		chatRoom.setRoomId(roomId);
		chatRoom.setMemberCnt(memberCnt);
		return chatRoom;
	}

	@Override
	protected ContentValues convertToConstantValues(OrmObject ormObj) {
		ChatRoom ormChatroom = (ChatRoom) ormObj;
		ContentValues values = new ContentValues();
		values.put("roomId", ormChatroom.getRoomId());
		values.put("memberCnt", ormChatroom.getMemberCnt());
		return values;
	}

	/**
	 * 获取聊天室列表
	 */
	@SuppressWarnings("unchecked")
	public List<ChatRoom> getChatRoomList() {
		List<ChatRoom> roomList = new ArrayList<ChatRoom>();
		CoreDbHelper coreDbHelper = new CoreDbHelper(
				Application.getAppContext());
		SQLiteDatabase db = coreDbHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(getTableName(), null, null, null, null,
					null, null);
			if (cursor.moveToNext()) {
				ChatRoom chatRoom = (ChatRoom) convertToOrmObj(cursor);
				roomList.add(chatRoom);
			}
			return roomList;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
		return null;
	}

}
