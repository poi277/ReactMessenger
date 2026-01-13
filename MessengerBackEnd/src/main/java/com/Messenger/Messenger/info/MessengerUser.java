package com.Messenger.Messenger.info;

import java.util.ArrayList;
import java.util.List;

import com.Messenger.Messenger.Friend.Friend;
import com.Messenger.Messenger.Friend.FriendRequest;
import com.Messenger.Messenger.chating.ChatRoomMember;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class MessengerUser {
	@Id
	private String id;
	private String password;
	private String name;
	private String email;
	private String platform; // google, naver
	@Column(unique = true, nullable = false)
	private String uuid;
	private String photoURL;
	private String introduce = "";
	@OneToMany(mappedBy = "messengerUser", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MessagePost> posts = new ArrayList<>();
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Friend> friends = new ArrayList<>();
	@OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FriendRequest> receivedRequests = new ArrayList<>();
	@OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FriendRequest> sentRequests = new ArrayList<>();
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PostLike> likes = new ArrayList<>();
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ChatRoomMember> chatRooms = new ArrayList<>();

	public MessengerUser() {

	}

	public MessengerUser(String id, String password, String name, String uuid, String email, String platform) {
		this.id = id;
		this.password = password;
		this.name = name;
		this.uuid = uuid;
		this.email = email;
		this.platform = platform;
		this.photoURL = null;
		this.posts = new ArrayList<>();
		this.friends = new ArrayList<>();
		this.receivedRequests = new ArrayList<>();
		this.sentRequests = new ArrayList<>();
	}

	public MessengerUser(String id, String password, String name, String email, String platform, String uuid,
			List<MessagePost> posts, List<Friend> friends, List<FriendRequest> receivedRequests,
			List<FriendRequest> sentRequests) {
		super();
		this.id = id;
		this.password = password;
		this.name = name;
		this.email = email;
		this.platform = platform;
		this.uuid = uuid;
		this.photoURL = null;
		this.posts = posts;
		this.friends = friends;
		this.receivedRequests = receivedRequests;
		this.sentRequests = sentRequests;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public List<MessagePost> getPosts() {
		return posts;
	}

	public void setPosts(List<MessagePost> posts) {
		this.posts = posts;
	}

	public List<Friend> getFriends() {
		return friends;
	}

	public void setFriends(List<Friend> friends) {
		this.friends = friends;
	}

	public List<FriendRequest> getReceivedRequests() {
		return receivedRequests;
	}

	public void setReceivedRequests(List<FriendRequest> receivedRequests) {
		this.receivedRequests = receivedRequests;
	}

	public List<FriendRequest> getSentRequests() {
		return sentRequests;
	}

	public void setSentRequests(List<FriendRequest> sentRequests) {
		this.sentRequests = sentRequests;
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

	public String getPhotoURL() {
		return photoURL;
	}

	public void setPhotoURL(String photoURL) {
		this.photoURL = photoURL;
	}

	public List<PostLike> getLikes() {
		return likes;
	}

	public void setLikes(List<PostLike> likes) {
		this.likes = likes;
	}

	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}


}
