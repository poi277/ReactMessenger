package com.Messenger.Messenger.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Messenger.Messenger.info.MessagePost;

public interface MessagePostRepository extends JpaRepository<MessagePost, Long> {
	List<MessagePost> findByMessengerUser_Uuid(String uuid);

	Optional<MessagePost> findFirstByMessengerUser_UuidOrderByCreatedDateDesc(String uuid);

	long countByMessengerUser_Uuid(String uuid);

}
