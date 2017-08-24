package com.dingapp.core.db.orm;

import android.os.Parcel;
import android.text.TextUtils;

import com.dingapp.core.db.dao.OrmObject;

public class Message implements OrmObject {
	private Long messageId;
	private Long roomId;
	private Integer readed;

	/**
	 * 如果是发送给群组的消息，那么destination就是"ToGroup".
	 * 如果是发送给个人的消息，那么destination就是"ToPerson".
	 */
	private String destionation;

	/**
	 * 如果是文字类型的消息，那么type就是"TEXT", 如果是文字类型的消息，那么type就是"VOICE",
	 * 如果是文字类型的消息，那么type就是"IMG";
	 */
	private String type;

	/**
	 * 如果是文子消息，那么content就是消息内容。 如果是语音消息，那么content就是语音文件存储的路径。
	 * 如果是图片消息，那么图片就是url或者文件存储的路经。
	 */
	private String content;

	/**
	 * 消息的作者
	 */
	private String imOwnerMessageId;
	/**
	 * 作者的昵称
	 */
	private String ownerNickName;
	/**
	 * 作者的头像url
	 */
	private String ownerHeaderProfile;

	/**
	 * 消息目标的id
	 */
	private String imTargetMessageId;
	/**
	 * 消息目标的昵称
	 */
	private String imTargetNickName;
	/**
	 * 消息目标的头像url
	 */
	private String imTargetHeaderProfile;
	/**
	 * 消息发送结果 如果success为1，表示发送成功。 如果success为0，表示发送失败。
	 */
	private Integer success;

	private String createTime;

	public Long getMessageId() {
		return messageId;
	}

	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public Integer getReaded() {
		return readed;
	}

	public void setReaded(Integer readed) {
		this.readed = readed;
	}

	public String getDestionation() {
		return destionation;
	}

	public void setDestionation(String destionation) {
		this.destionation = destionation;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImOwnerMessageId() {
		return imOwnerMessageId;
	}

	public void setImOwnerMessageId(String imOwnerMessageId) {
		this.imOwnerMessageId = imOwnerMessageId;
	}

	public String getOwnerNickName() {
		return ownerNickName;
	}

	public void setOwnerNickName(String ownerNickName) {
		this.ownerNickName = ownerNickName;
	}

	public String getOwnerHeaderProfile() {
		return ownerHeaderProfile;
	}

	public void setOwnerHeaderProfile(String ownerHeaderProfile) {
		this.ownerHeaderProfile = ownerHeaderProfile;
	}

	public String getImTargetMessageId() {
		return imTargetMessageId;
	}

	public void setImTargetMessageId(String imTargetMessageId) {
		this.imTargetMessageId = imTargetMessageId;
	}

	public String getImTargetNickName() {
		return imTargetNickName;
	}

	public void setImTargetNickName(String imTargetNickName) {
		this.imTargetNickName = imTargetNickName;
	}

	public String getImTargetHeaderProfile() {
		return imTargetHeaderProfile;
	}

	public void setImTargetHeaderProfile(String imTargetHeaderProfile) {
		this.imTargetHeaderProfile = imTargetHeaderProfile;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public Integer getSuccess() {
		return success;
	}

	public void setSuccess(Integer success) {
		this.success = success;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (messageId != null) {
			dest.writeLong(messageId);
		} else {
			dest.writeLong(-1L);
		}
		if (roomId != null) {
			dest.writeLong(roomId);
		} else {
			dest.writeLong(-1L);
		}
		if (!TextUtils.isEmpty(destionation)) {
			dest.writeString(destionation);
		} else {
			dest.writeString("");
		}
		if (!TextUtils.isEmpty(type)) {
			dest.writeString(type);
		} else {
			dest.writeString("");
		}
		if (!TextUtils.isEmpty(content)) {
			dest.writeString(content);
		} else {
			dest.writeString("");
		}
		if (!TextUtils.isEmpty(imOwnerMessageId)) {
			dest.writeString(imOwnerMessageId);
		} else {
			dest.writeString("");
		}
		if (!TextUtils.isEmpty(ownerNickName)) {
			dest.writeString(ownerNickName);
		} else {
			dest.writeString("");
		}
		if (!TextUtils.isEmpty(ownerHeaderProfile)) {
			dest.writeString(ownerHeaderProfile);
		} else {
			dest.writeString("");
		}
		if (!TextUtils.isEmpty(imTargetMessageId)) {
			dest.writeString(imTargetMessageId);
		} else {
			dest.writeString("");
		}
		if (!TextUtils.isEmpty(imTargetNickName)) {
			dest.writeString(imTargetNickName);
		} else {
			dest.writeString("");
		}
		if (!TextUtils.isEmpty(imTargetHeaderProfile)) {
			dest.writeString(imTargetHeaderProfile);
		} else {
			dest.writeString("");
		}
		if (!TextUtils.isEmpty(createTime)) {
			dest.writeString(createTime);
		} else {
			dest.writeString("");
		}
		if (readed != null) {
			dest.writeInt(readed);
		} else {
			dest.writeInt(-1);
		}
		if (success != null) {
			dest.writeInt(success);
		} else {
			dest.writeInt(0);
		}
	}

	public static final Creator<Message> CREATOR = new Creator<Message>() {

		@Override
		public Message createFromParcel(Parcel source) {
			Message message = new Message();

			message.messageId = source.readLong();
			message.roomId = source.readLong();
			message.destionation = source.readString();
			message.type = source.readString();
			message.content = source.readString();
			message.imOwnerMessageId = source.readString();
			message.ownerNickName = source.readString();
			message.ownerHeaderProfile = source.readString();
			message.imTargetMessageId = source.readString();
			message.imTargetNickName = source.readString();
			message.imTargetHeaderProfile = source.readString();
			message.createTime = source.readString();
			message.readed = source.readInt();
			message.success = source.readInt();
			return message;
		}

		@Override
		public Message[] newArray(int size) {
			return new Message[size];
		}
	};
}
