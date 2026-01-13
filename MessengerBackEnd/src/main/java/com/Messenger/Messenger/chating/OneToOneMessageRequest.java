package com.Messenger.Messenger.chating;

public class OneToOneMessageRequest {
	private String senderuuid;
	private String receiveruuid; // 상대방 ID
	private String content;

	public String getSenderuuid() {
		return senderuuid;
	}

	public void setSenderuuid(String senderuuid) {
		this.senderuuid = senderuuid;
	}

	public String getReceiveruuid() {
		return receiveruuid;
	}

	public void setReceiveruuid(String receiveruuid) {
		this.receiveruuid = receiveruuid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}



}