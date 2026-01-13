package com.Messenger.Messenger.DTO;

import com.Messenger.Messenger.info.MessagePhoto;

public class MessagePhotoDTO {
	private Long id;
	private String photoUrl;
	private Long postId;

	public MessagePhotoDTO(MessagePhoto photo) {
		this.id = photo.getId();
		this.photoUrl = photo.getPhotoUrl();
		this.postId = photo.getMessagePost() != null ? photo.getMessagePost().getId() : null;
	}

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

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

}
