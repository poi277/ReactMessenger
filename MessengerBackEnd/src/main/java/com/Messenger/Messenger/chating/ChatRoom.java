package com.Messenger.Messenger.chating;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_room")
public class ChatRoom {
	ChatRoom() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // DB에서 자동 증가
	@OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ChatRoomMember> members = new ArrayList<>();
	@OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ChatMessage> chatMessages = new ArrayList<>();

	private boolean isOnetoOne;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<ChatRoomMember> getMembers() {
		return members;
	}

	public void setMembers(List<ChatRoomMember> members) {
		this.members = members;
	}

	public boolean isOnetoOne() {
		return isOnetoOne;
	}

	public void setIsOnetoOne(boolean isOnetoOne) {
		this.isOnetoOne = isOnetoOne;
	}

	public List<ChatMessage> getChatMessages() {
		return chatMessages;
	}

	public void setChatMessages(List<ChatMessage> chatMessages) {
		this.chatMessages = chatMessages;
	}

	public void setOnetoOne(boolean isOnetoOne) {
		this.isOnetoOne = isOnetoOne;
	}

}



