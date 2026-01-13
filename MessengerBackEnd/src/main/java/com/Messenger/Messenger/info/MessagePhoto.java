package com.Messenger.Messenger.info;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class MessagePhoto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String photoUrl; // 사진 저장 위치 URL

	@ManyToOne
	@JoinColumn(name = "post_id")
	private MessagePost messagePost;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public MessagePost getMessagePost() {
		return messagePost;
	}

	public void setMessagePost(MessagePost messagePost) {
		this.messagePost = messagePost;
	}

}
