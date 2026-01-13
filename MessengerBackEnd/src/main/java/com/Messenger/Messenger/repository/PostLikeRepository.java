package com.Messenger.Messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Messenger.Messenger.info.MessagePost;
import com.Messenger.Messenger.info.MessengerUser;
import com.Messenger.Messenger.info.PostLike;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

	// messagePost 필드명을 기준으로 메서드 작성
	void deleteByMessagePostAndUser(MessagePost messagePost, MessengerUser user);

	boolean existsByMessagePostAndUser(MessagePost messagePost, MessengerUser user);

	long countByMessagePost(MessagePost post);
}
