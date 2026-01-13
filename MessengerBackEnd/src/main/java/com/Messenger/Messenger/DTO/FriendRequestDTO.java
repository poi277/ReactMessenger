package com.Messenger.Messenger.DTO;

import com.Messenger.Messenger.Friend.FriendRequest;

public class FriendRequestDTO {
	private Long id;
	private String senderName;
	private String profilePhotoUrl;

	public FriendRequestDTO(FriendRequest fr) {
		this.id = fr.getId();
		this.senderName = fr.getSender().getName();
		this.profilePhotoUrl = fr.getSender().getPhotoURL();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getProfilePhotoUrl() {
		return profilePhotoUrl;
	}

	public void setProfilePhotoUrl(String profilePhotoUrl) {
		this.profilePhotoUrl = profilePhotoUrl;
	}

}
