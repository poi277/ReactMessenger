package com.Messenger.Messenger.DTO;

public class PhotoDTO {
	private Long id;
	private String photoUrl;

	public PhotoDTO() {
	}

	public PhotoDTO(Long id, String photoUrl) {
		this.id = id;
		this.photoUrl = photoUrl;
	}

	// getter & setter
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
}
