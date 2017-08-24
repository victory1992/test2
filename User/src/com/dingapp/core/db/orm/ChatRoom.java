package com.dingapp.core.db.orm;

import android.os.Parcel;

import com.dingapp.core.db.dao.OrmObject;

public class ChatRoom implements OrmObject {

	private Long roomId;
	private Integer memberCnt;

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public int getMemberCnt() {
		return memberCnt;
	}

	public void setMemberCnt(int memberCnt) {
		this.memberCnt = memberCnt;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (roomId != null) {
			dest.writeLong(roomId);
		} else {
			dest.writeLong(-1L);
		}
		if (memberCnt != null) {
			dest.writeInt(memberCnt);
		} else {
			dest.writeInt(0);
		}
	}

	public static final Creator<ChatRoom> CREATOR = new Creator<ChatRoom>() {

		@Override
		public ChatRoom[] newArray(int size) {
			// TODO Auto-generated method stub
			return new ChatRoom[size];
		}

		@Override
		public ChatRoom createFromParcel(Parcel source) {
			ChatRoom chatRoom = new ChatRoom();
			chatRoom.roomId = source.readLong();
			chatRoom.memberCnt = source.readInt();
			return chatRoom;
		}
	};

}
