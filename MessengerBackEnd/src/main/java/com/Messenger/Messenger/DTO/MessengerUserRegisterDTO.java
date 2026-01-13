package com.Messenger.Messenger.DTO;

public class MessengerUserRegisterDTO {
	private String id;
	private String password;
	private String confirmPassword;
	private String name;
	private String email;

	public MessengerUserRegisterDTO(String id, String password, String confirmPassword, String name, String email) {
		super();
		this.id = id;
		this.password = password;
		this.confirmPassword = confirmPassword;
		this.name = name;
		this.email = email;
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

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
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


}
