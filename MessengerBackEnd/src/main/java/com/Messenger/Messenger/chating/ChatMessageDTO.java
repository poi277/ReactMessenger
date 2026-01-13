package com.Messenger.Messenger.chating;

import java.time.LocalDateTime;

public class ChatMessageDTO {

	private Long id; // 메시지 ID
	private Long chatRoomId; // 채팅방 ID
	private String senderuuid; // 발신자 ID
	private String senderName; // 발신자 이름 (선택)
	private LocalDateTime createdAt;
	private String content; // 메시지 내용
	private String profilePhotoURL;

	public ChatMessageDTO() {
	}


	public ChatMessageDTO(Long id, Long chatRoomId, String senderuuid, String senderName, LocalDateTime createdAt,
			String content, String profilePhotoURL) {
		super();
		this.id = id;
		this.chatRoomId = chatRoomId;
		this.senderuuid = senderuuid;
		this.senderName = senderName;
		this.createdAt = createdAt;
		this.content = content;
		this.profilePhotoURL = profilePhotoURL;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getChatRoomId() {
		return chatRoomId;
	}

	public void setChatRoomId(Long chatRoomId) {
		this.chatRoomId = chatRoomId;
	}

	public String getSenderuuid() {
		return senderuuid;
	}

	public void setSenderuuid(String senderuuid) {
		this.senderuuid = senderuuid;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getProfilePhotoURL() {
		return profilePhotoURL;
	}

	public void setProfilePhotoURL(String profilePhotoURL) {
		this.profilePhotoURL = profilePhotoURL;
	}

}
