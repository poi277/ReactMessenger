package com.Messenger.Messenger.chating;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
	// 특정 채팅방의 멤버 조회 등 추가 메소드 가능
}