package com.Messenger.Messenger.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Messenger.Messenger.DTO.CommentDTO;
import com.Messenger.Messenger.info.Comment;
import com.Messenger.Messenger.info.MessagePost;
import com.Messenger.Messenger.info.MessengerUser;
import com.Messenger.Messenger.repository.CommentRepository;
import com.Messenger.Messenger.repository.MessagePostRepository;
import com.Messenger.Messenger.repository.MessengerUserRepository;

@Service
public class CommentService {

	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private MessagePostRepository postRepository;
	@Autowired
	private MessengerUserRepository userRepository;

	// 댓글/대댓글 추가
	@Transactional
	public CommentDTO addComment(CommentDTO dto, String userUuid) {
		MessagePost post = postRepository.findById(dto.getPostId()).orElseThrow(() -> new RuntimeException("게시글 없음"));
		MessengerUser user = userRepository.findByUuid(userUuid).orElseThrow(() -> new RuntimeException("유저 없음"));

		Comment parent = null;
		if (dto.getParentId() != null) {
			parent = commentRepository.findById(dto.getParentId()).orElseThrow(() -> new RuntimeException("부모 댓글 없음"));

			// 2단계 제한
			if (parent.getParent() != null) {
				throw new RuntimeException("대댓글의 대댓글은 허용되지 않습니다.");
			}
		}

		if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
			throw new RuntimeException("댓글 내용은 비워둘 수 없습니다.");
		}

		Comment comment = new Comment(dto.getContent().trim(), user, post, parent);
		Comment saved = commentRepository.save(comment);
		return CommentDTO.toDTO(saved);
	}

	// 특정 게시글 댓글 조회 (루트 댓글만)
	@Transactional(readOnly = true)
	public List<CommentDTO> getCommentsByPost(Long postId) {
		List<Comment> comments = commentRepository.findByMessagePostIdAndParentIsNullOrderByCreatedDateAsc(postId);
		return comments.stream().map(CommentDTO::toDTO).collect(Collectors.toList());
	}

	// 댓글 수정
	@Transactional
	public CommentDTO updateComment(Long commentId, CommentDTO dto, String userUuid) {
		MessengerUser user = userRepository.findByUuid(userUuid).orElseThrow(() -> new RuntimeException("사용자 없음"));

		Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("댓글 없음"));

		if (!comment.getMessengerUser().getId().equals(user.getId())) {
			throw new RuntimeException("본인 댓글만 수정할 수 있습니다.");
		}

		if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
			throw new RuntimeException("댓글 내용은 비워둘 수 없습니다.");
		}

		comment.setContent(dto.getContent().trim());
		Comment updated = commentRepository.save(comment);
		return CommentDTO.toDTO(updated);
	}

	@Transactional
	public void deleteComment(Long commentId) {
	    Comment comment = commentRepository.findById(commentId)
	            .orElseThrow(() -> new RuntimeException("댓글 없음"));

		if (comment.getChildren().isEmpty()) {
			// 자식이 없는 댓글 삭제
			commentRepository.delete(comment);

			// 부모 댓글에서 자식 리스트 제거
			Comment parent = comment.getParent();
			if (parent != null) {
				parent.getChildren().remove(comment);
				commentRepository.save(parent);

				// 부모가 content=null이고, 이제 자식이 없으면 재귀 삭제
				if (parent.getContent() == null && parent.getChildren().isEmpty()) {
					deleteComment(parent.getId());
				}
			}
		} else {
			// 자식이 있는 부모 댓글은 content=null 처리
			comment.setContent(null);
			commentRepository.save(comment);
	    }
	}

}
