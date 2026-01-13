package com.Messenger.Messenger.chating;

import com.Messenger.Messenger.info.MessengerUser;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "chat_room_member")
public class ChatRoomMember {
	public ChatRoomMember() {
	}

	public ChatRoomMember(ChatRoom chatRoom, MessengerUser user) {
		super();
		this.chatRoom = chatRoom;
		this.user = user;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "chat_room_id", nullable = false)
	private ChatRoom chatRoom;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private MessengerUser user;

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

	public MessengerUser getUser() {
		return user;
	}

	public void setUser(MessengerUser user) {
		this.user = user;
	}

}
