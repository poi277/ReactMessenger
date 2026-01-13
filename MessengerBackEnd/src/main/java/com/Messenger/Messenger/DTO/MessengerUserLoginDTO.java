package com.Messenger.Messenger.DTO;

import com.Messenger.Messenger.info.MessengerUser;

public class MessengerUserLoginDTO {
	private String id;
	private String password;

	public MessengerUserLoginDTO() {
		// 기본 생성자 (public)
	}

	// 필요하면 User -> UserLoginDTO 변환 생성자도 추가 가능
	public MessengerUserLoginDTO(MessengerUser user) {
		this.id = user.getId();
		this.password = user.getPassword();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}

