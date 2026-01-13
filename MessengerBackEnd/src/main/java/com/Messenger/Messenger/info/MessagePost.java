package com.Messenger.Messenger.info;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class MessagePost {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String title;
	private String context;
	private LocalDateTime createdDate; // 작성시간 필드 추가
	private int likeCount; // 좋아요 수

	@Enumerated(EnumType.STRING) // enum을 문자열로 저장
	PostRange postRange;

	public enum PostRange {
		publicpost, friendpost, privatepost
	}

	@ManyToOne
	@JoinColumn(name = "user_id") // FK 컬럼명
	private MessengerUser messengerUser;

	@OneToMany(mappedBy = "messagePost", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MessagePhoto> photos = new ArrayList<>();
	@OneToMany(mappedBy = "messagePost", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> comments = new ArrayList<>();
	@OneToMany(mappedBy = "messagePost", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PostLike> likes = new ArrayList<>();

	public MessagePost() {

	}

	public MessagePost(long id, String title, String context, LocalDateTime createdDate, int likeCount,
			MessengerUser messengerUser, List<MessagePhoto> photos) {
		super();
		this.id = id;
		this.title = title;
		this.context = context;
		this.createdDate = createdDate;
		this.likeCount = likeCount;
		this.messengerUser = messengerUser;
		this.photos = photos;
	}


	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public int getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

	public MessengerUser getMessengerUser() {
		return messengerUser;
	}

	public void setMessengerUser(MessengerUser messengerUser) {
		this.messengerUser = messengerUser;
	}

	public List<MessagePhoto> getPhotos() {
		return photos;
	}

	public void setPhotos(List<MessagePhoto> photos) {
		this.photos = photos;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public List<PostLike> getLikes() {
		return likes;
	}

	public void setLikes(List<PostLike> likes) {
		this.likes = likes;
	}

	public PostRange getPostRange() {
		return postRange;
	}

	public void setPostRange(PostRange postRange) {
		this.postRange = postRange;
	}


}
