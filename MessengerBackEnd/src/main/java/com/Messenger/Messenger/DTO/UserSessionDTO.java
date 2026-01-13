package com.Messenger.Messenger.DTO;

import java.io.Serializable;

public class UserSessionDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String uuid;
	private String name;
	private String photoURL;

	public UserSessionDTO(String uuid, String name, String photoURL) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.photoURL = photoURL;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
