package com.Messenger.Messenger.DTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.Messenger.Messenger.info.MessagePost;
import com.Messenger.Messenger.info.MessagePost.PostRange;

public class MessagePostDTO {

	private long id;
	private String profilePhotoUrl;
	private String title;
	private String context;
	private LocalDateTime createdDate;
	private int likeCount;
	private String userUuid;
	private String name;
	private List<PhotoDTO> photos; // 기존 photoUrls 대신
	private boolean likes; // 기존 photoUrls 대신
	private int commentCount;
	PostRange postRange;

	public MessagePostDTO() {

	}

	public MessagePostDTO(long id, String title, String context, LocalDateTime createdDate,
			int likeCount,
			String userUuid, String name, List<PhotoDTO> photos, boolean likes, String profilePhotoUrl
			, PostRange postRange) {
		super();
		this.id = id;
		this.title = title;
		this.context = context;
		this.createdDate = createdDate;
		this.likeCount = likeCount;
		this.userUuid = userUuid;
		this.name = name;
		this.photos = photos;
		this.likes = likes;
		this.profilePhotoUrl = profilePhotoUrl;
		this.postRange = postRange;
	}

	public MessagePostDTO(long id, String title, String context, LocalDateTime createdDate, int likeCount,
			String userUuid, String name, List<PhotoDTO> photos, boolean likes, String profilePhotoUrl,
			PostRange postRange, int commentCount) {
		super();
		this.id = id;
		this.title = title;
		this.context = context;
		this.createdDate = createdDate;
		this.likeCount = likeCount;
		this.userUuid = userUuid;
		this.name = name;
		this.photos = photos;
		this.likes = likes;
		this.profilePhotoUrl = profilePhotoUrl;
		this.postRange = postRange;
		this.commentCount = commentCount;
	}

	public static MessagePostDTO toDTO(MessagePost post, String currentUserUuid, String profilePhotoUrl,
			int commentCount) {
		List<PhotoDTO> photos = post.getPhotos().stream().map(photo -> new PhotoDTO(photo.getId(), photo.getPhotoUrl()))
				.collect(Collectors.toList());

		boolean liked = post.getLikes() != null
				&& post.getLikes().stream().anyMatch(like -> like.getUser().getUuid().equals(currentUserUuid));

		return new MessagePostDTO(post.getId(), post.getTitle(), post.getContext(), post.getCreatedDate(),
				post.getLikes().size(), post.getMessengerUser() != null ? post.getMessengerUser().getUuid() : null,
				post.getMessengerUser() != null ? post.getMessengerUser().getName() : null, photos, liked,
				profilePhotoUrl, post.getPostRange(), commentCount // 여기서 전달
		);
	}

	public static MessagePostDTO toDTO(MessagePost post, String currentUserUuid, String profilePhotoUrl
			) {
		List<PhotoDTO> photos = post.getPhotos().stream().map(photo -> new PhotoDTO(photo.getId(), photo.getPhotoUrl()))
				.collect(Collectors.toList());

		boolean liked = post.getLikes() != null
				&& post.getLikes().stream().anyMatch(like -> like.getUser().getUuid().equals(currentUserUuid));

		return new MessagePostDTO(post.getId(), post.getTitle(), post.getContext(), post.getCreatedDate(),
				post.getLikes().size(), post.getMessengerUser() != null ? post.getMessengerUser().getUuid() : null,
				post.getMessengerUser() != null ? post.getMessengerUser().getName() : null, photos, liked,
				profilePhotoUrl, post.getPostRange() // 여기서 전달
		);
	}

	public static List<MessagePostDTO> toDTOList(List<MessagePost> posts, String currentUserUuid,
			String profilePhotoUrl, int commentCount) {
		return posts.stream().map(post -> toDTO(post, currentUserUuid, profilePhotoUrl, commentCount))
				.collect(Collectors.toList());
	}

	public static List<MessagePostDTO> toDTOList(List<MessagePost> posts, String currentUserUuid,
			String profilePhotoUrl) {
		return posts.stream().map(post -> toDTO(post, currentUserUuid, profilePhotoUrl)).collect(Collectors.toList());
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


	public LocalDateTime getCreatedDate() {
		return createdDate;
	}


	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}


	public int getLikeCount() {
		return likeCount;
	}


	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}


	public String getUserUuid() {
		return userUuid;
	}


	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	public List<PhotoDTO> getPhotos() {
		return photos;
	}

	public void setPhotos(List<PhotoDTO> photos) {
		this.photos = photos;
	}

	public boolean isLikes() {
		return likes;
	}

	public void setLikes(boolean likes) {
		this.likes = likes;
	}

	public String getProfilePhotoUrl() {
		return profilePhotoUrl;
	}

	public void setProfilePhotoUrl(String profilePhotoUrl) {
		this.profilePhotoUrl = profilePhotoUrl;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public PostRange getPostRange() {
		return postRange;
	}

	public void setPostRange(PostRange postRange) {
		this.postRange = postRange;
	}


}
