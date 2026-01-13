package com.Messenger.Messenger.DTO;

import java.util.List;
import java.util.stream.Collectors;

import com.Messenger.Messenger.info.MessengerUser;

public class UserNameDTO {

	private String uuid;
	private String name;
	private String photoURL;

	public UserNameDTO(String uuid, String name, String photoURL) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.photoURL = photoURL;
	}

	public static UserNameDTO toDTO(MessengerUser user) {
		return new UserNameDTO(user.getUuid(), user.getName(), user.getPhotoURL());
	}

	public static List<UserNameDTO> toDTOList(List<MessengerUser> users) {
		return users.stream().map(UserNameDTO::toDTO).collect(Collectors.toList());
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhotoURL() {
		return photoURL;
	}

	public void setPhotoURL(String photoURL) {
		this.photoURL = photoURL;
	}

}
