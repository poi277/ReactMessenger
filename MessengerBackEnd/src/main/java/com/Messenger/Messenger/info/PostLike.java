package com.Messenger.Messenger.info;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class PostLike {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "post_id")
	private MessagePost messagePost;

	@ManyToOne
	@JoinColumn(name = "user_uuid")
	private MessengerUser user;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MessagePost getMessagePost() {
		return messagePost;
	}

	public void setMessagePost(MessagePost messagePost) {
		this.messagePost = messagePost;
	}

	public MessengerUser getUser() {
		return user;
	}

	public void setUser(MessengerUser user) {
		this.user = user;
	}

}
