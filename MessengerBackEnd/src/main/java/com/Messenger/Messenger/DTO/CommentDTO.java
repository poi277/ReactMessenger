package com.Messenger.Messenger.DTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.Messenger.Messenger.info.Comment;

public class CommentDTO {
	private Long id;
	private Long postId;
	private String content;
	private String author; // messengerUser's UUID
	private String name;
	private LocalDateTime createdDate;
	private Long parentId;
	private List<CommentDTO> children = new ArrayList<>();

	CommentDTO() {

	}
	// Full constructor
	public CommentDTO(Long id, String content, String author, String name, LocalDateTime createdDate, Long parentId,
			Long postId, List<CommentDTO> children) {
		this.id = id;
		this.content = content;
		this.author = author;
		this.name = name;
		this.createdDate = createdDate;
		this.parentId = parentId;
		this.postId = postId;
		this.children = children != null ? children : new ArrayList<>();
	}

	// Constructor without children for simpler use
	public CommentDTO(Long id, String content, String author, String name, LocalDateTime createdDate, Long parentId,
			Long postId) {
		this(id, content, author, name, createdDate, parentId, postId, new ArrayList<>());
	}

	public static CommentDTO toDTO(Comment comment) {
		if (comment == null)
			return null;

		CommentDTO dto = new CommentDTO(comment.getId(), comment.getContent(), comment.getMessengerUser().getUuid(),
				comment.getMessengerUser().getName(), // Name
				comment.getCreatedDate(),
				comment.getParent() != null ? comment.getParent().getId() : null,
				comment.getMessagePost().getId());

		for (Comment child : comment.getChildren()) {
			dto.getChildren().add(toDTO(child));
		}
		return dto;
	}
	public Long getId() {
		return id;
	}

	public Long getPostId() {
		return postId;
	}

	public String getContent() {
		return content;
	}

	public String getAuthor() {
		return author;
	}

	public String getName() {
		return name;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public Long getParentId() {
		return parentId;
	}

	public List<CommentDTO> getChildren() {
		return children;
	}
}
