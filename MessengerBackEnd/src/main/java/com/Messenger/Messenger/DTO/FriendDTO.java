package com.Messenger.Messenger.DTO;

import com.Messenger.Messenger.Friend.Friend;

public class FriendDTO {
	private Long id;
	private String uuid;
	private String friendname;
	private String profilePhotoUrl;
	private boolean online; // 추가

	public FriendDTO(Friend fr, boolean online) {
		this.id = fr.getId();
		this.uuid = fr.getFriend().getUuid(); // 친구 UUID
		this.friendname = fr.getFriend().getName(); // 친구 이름
		this.profilePhotoUrl = fr.getFriend().getPhotoURL();
		this.online = online;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getFriendname() {
		return friendname;
	}

	public void setFriendname(String friendname) {
		this.friendname = friendname;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public String getProfilePhotoUrl() {
		return profilePhotoUrl;
	}

	public void setProfilePhotoUrl(String profilePhotoUrl) {
		this.profilePhotoUrl = profilePhotoUrl;
	}

}