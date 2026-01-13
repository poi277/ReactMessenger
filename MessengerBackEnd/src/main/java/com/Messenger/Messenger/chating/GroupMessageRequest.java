package com.Messenger.Messenger.chating;

public class GroupMessageRequest {
	private Long roomId;
	private String senderuuid;
	private String content;

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public String getSenderuuid() {
		return senderuuid;
	}

	public void setSenderuuid(String senderuuid) {
		this.senderuuid = senderuuid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}


}