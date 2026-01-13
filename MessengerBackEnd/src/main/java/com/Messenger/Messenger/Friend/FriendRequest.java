package com.Messenger.Messenger.Friend;

import com.Messenger.Messenger.info.MessengerUser;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
@Entity
@Table(name = "friend_requests")
public class FriendRequest {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_uuid", referencedColumnName = "uuid", nullable = false)
	private MessengerUser sender;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_uuid", referencedColumnName = "uuid", nullable = false)
	private MessengerUser receiver;

	@Enumerated(EnumType.STRING)
	private Status status;

	public enum Status {
		PENDING, ACCEPTED, REJECTED
	}

	public FriendRequest() {
	}

	public FriendRequest(MessengerUser sender, MessengerUser receiver, Status status) {
		this.sender = sender;
		this.receiver = receiver;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MessengerUser getSender() {
		return sender;
	}

	public void setSender(MessengerUser sender) {
		this.sender = sender;
	}

	public MessengerUser getReceiver() {
		return receiver;
	}

	public void setReceiver(MessengerUser receiver) {
		this.receiver = receiver;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
