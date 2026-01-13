package com.Messenger.Messenger.chating;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.Messenger.Messenger.info.MessengerUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_message")
public class ChatMessage {
	ChatMessage() {

	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Long 자동 증가
	private Long id;

	@ManyToOne
	@JoinColumn(name = "chat_room_id", nullable = false)
	private ChatRoom chatRoom;

	@ManyToOne
	@JoinColumn(name = "sender_id", nullable = false)
	private MessengerUser sender;

	@Column(nullable = false, updatable = false)
	@CreationTimestamp // Hibernate가 insert 시 자동으로 시간 기록
	private LocalDateTime createdAt;

	@Column(nullable = false) // content는 null 안됨
	private String content;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ChatRoom getChatRoom() {
		return chatRoom;
	}

	public void setChatRoom(ChatRoom chatRoom) {
		this.chatRoom = chatRoom;
	}

	public MessengerUser getSender() {
		return sender;
	}

	public void setSender(MessengerUser sender) {
		this.sender = sender;
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

}


