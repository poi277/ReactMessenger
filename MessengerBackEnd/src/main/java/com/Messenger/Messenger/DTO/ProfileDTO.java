package com.Messenger.Messenger.DTO;

public class ProfileDTO {
	private String photoURL;
	private String name;
	private String introduce;
	private long postCount;

	public ProfileDTO(String photoURL, String name, String introduce, long postCount) {
		super();
		this.photoURL = photoURL;
		this.name = name;
		this.introduce = introduce;
		this.postCount = postCount;
	}

	public String getPhotoURL() {
		return photoURL;
	}

	public void setPhotoURL(String photoURL) {
		this.photoURL = photoURL;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIntroduce() {
		return introduce;
	}
	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}
	public long getPostCount() {
		return postCount;
	}
	public void setPostCount(long postCount) {
		this.postCount = postCount;
	}


}
