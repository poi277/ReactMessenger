package com.Messenger.Messenger.chating;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

	// 1:1 채팅방 찾기용 (두 사람의 UUID 기준)
	Optional<ChatRoom> findByMembersUserUuidContainingAndMembersUserUuidContaining(String user1Uuid, String user2Uuid);

	@Query("SELECT r FROM ChatRoom r " + "JOIN ChatRoomMember m1 ON r.id = m1.chatRoom.id "
			+ "JOIN ChatRoomMember m2 ON r.id = m2.chatRoom.id "
			+ "WHERE m1.user.id = :userId1 AND m2.user.id = :userId2 " + "AND SIZE(r.members) = 2")
	Optional<ChatRoom> findOneToOneRoom(String userId1, String userId2);

}