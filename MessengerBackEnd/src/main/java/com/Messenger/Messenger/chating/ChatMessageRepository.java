package com.Messenger.Messenger.chating;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

	// 특정 채팅방의 메시지 목록 조회
	List<ChatMessage> findByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId);
}
