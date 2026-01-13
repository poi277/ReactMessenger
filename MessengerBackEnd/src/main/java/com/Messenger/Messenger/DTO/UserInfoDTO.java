package com.Messenger.Messenger.DTO;

import com.Messenger.Messenger.info.MessengerUser;

public class UserInfoDTO {
	private String id;
	private String name;
	private String email;
	private String platform; // google, naver
	private String uuid;
	private String photoURL;
	private String introduce;

	public UserInfoDTO() {
	}

	public UserInfoDTO(MessengerUser User) {
		this.id = User.getId();
		this.name = User.getName();
		this.email = User.getEmail();
		this.platform = User.getPlatform();
		this.uuid = User.getUuid();
		this.photoURL = User.getPhotoURL();
		this.introduce = User.getIntroduce();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getPhotoURL() {
		return photoURL;
	}

	public void setPhotoURL(String photoURL) {
		this.photoURL = photoURL;
	}

	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

}
