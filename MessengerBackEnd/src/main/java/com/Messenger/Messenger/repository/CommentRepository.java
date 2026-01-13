package com.Messenger.Messenger.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.Messenger.Messenger.info.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByMessagePost_IdAndParentIsNull(Long postId);

	List<Comment> findByMessagePostIdAndParentIsNullOrderByCreatedDateAsc(Long postId);

	@Query("SELECT COUNT(c) FROM Comment c WHERE c.messagePost.id = :postId")
	int countByMessagePostId(@Param("postId") Long postId);
}