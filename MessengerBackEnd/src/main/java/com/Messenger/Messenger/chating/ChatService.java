package com.Messenger.Messenger.chating;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Messenger.Messenger.info.MessengerUser;
import com.Messenger.Messenger.repository.MessengerUserRepository;
import com.Messenger.Messenger.service.MessengerSessionService;

@Service
public class ChatService {

	@Autowired
	private ChatRoomRepository chatRoomRepository;

	@Autowired
	private ChatRoomMemberRepository chatRoomMemberRepository;

	@Autowired
	private ChatMessageRepository chatMessageRepository;

	@Autowired
	private MessengerUserRepository messengerUserRepository;
	@Autowired
	private MessengerSessionService messengerSessionService;

	// ===== 1:1 채팅 메시지 조회 =====
	public List<ChatMessageDTO> getOneToOneMessages(OneToOneMessageRequest request) {
	    MessengerUser sender = messengerUserRepository.findByUuid(request.getSenderuuid())
	            .orElseThrow(() -> new RuntimeException("Sender not found"));
	    MessengerUser receiver = messengerUserRepository.findByUuid(request.getReceiveruuid())
	            .orElseThrow(() -> new RuntimeException("Receiver not found"));
	    ChatRoom chatRoom = chatRoomRepository.findOneToOneRoom(sender.getId(), receiver.getId()).orElse(null);
	    if (chatRoom == null) {
			return List.of();
	    }
	    List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoom.getId());
	    return messages.stream()
	            .map(msg -> new ChatMessageDTO(
	                    msg.getId(),
	                    msg.getChatRoom().getId(),
	                    msg.getSender().getUuid(),
						msg.getSender().getName(),
	                    msg.getCreatedAt(),
						msg.getContent(), msg.getSender().getPhotoURL()
	            ))
	            .toList();
	}

	// ===== 1:1 채팅 메시지 전송 =====
	public ChatMessageDTO sendOneToOneMessage(OneToOneMessageRequest request) {
		MessengerUser sender = messengerUserRepository.findByUuid(request.getSenderuuid())
				.orElseThrow(() -> new RuntimeException("Sender not found"));
		MessengerUser receiver = messengerUserRepository.findByUuid(request.getReceiveruuid())
				.orElseThrow(() -> new RuntimeException("Receiver not found"));
		ChatRoom chatRoom = chatRoomRepository.findOneToOneRoom(sender.getId(), receiver.getId())
				.orElseGet(() -> createOneToOneChatRoom(sender, receiver));
		ChatMessage message = new ChatMessage();
		message.setChatRoom(chatRoom);
		message.setSender(sender);
		message.setContent(request.getContent());
		message = chatMessageRepository.save(message);
		return new ChatMessageDTO(message.getId(), chatRoom.getId(), sender.getUuid(), sender.getName(),
				message.getCreatedAt(), message.getContent(), sender.getPhotoURL());
	}


	private ChatRoom createOneToOneChatRoom(MessengerUser sender, MessengerUser receiver) {
		ChatRoom chatRoom = new ChatRoom();
		chatRoom.setIsOnetoOne(true);
		chatRoom = chatRoomRepository.save(chatRoom);

		// 멤버 등록
		ChatRoomMember member1 = new ChatRoomMember();
		member1.setChatRoom(chatRoom);
		member1.setUser(sender);
		chatRoomMemberRepository.save(member1);

		ChatRoomMember member2 = new ChatRoomMember();
		member2.setChatRoom(chatRoom);
		member2.setUser(receiver);
		chatRoomMemberRepository.save(member2);

		return chatRoom;
	}

	// ===== 그룹 채팅방 생성 =====
	public ChatRoom createGroupChatRoom(GroupChatRoomCreateRequest request) {
		if (request.getParticipantuuid() == null || request.getParticipantuuid().isEmpty()) {
			throw new RuntimeException("Participant IDs required for group chat");
		}

		ChatRoom chatRoom = new ChatRoom();
		chatRoom.setIsOnetoOne(false);
		chatRoom = chatRoomRepository.save(chatRoom);

		for (String useruuid : request.getParticipantuuid()) {
			MessengerUser user = messengerUserRepository.findByUuid(useruuid)
					.orElseThrow(() -> new RuntimeException("User not found"));
			ChatRoomMember member = new ChatRoomMember();
			member.setChatRoom(chatRoom);
			member.setUser(user);
			chatRoomMemberRepository.save(member);
		}

		return chatRoom;
	}

	// ===== 그룹 채팅 메시지 전송 =====
	public ChatMessage sendGroupMessage(GroupMessageRequest request) {
		ChatRoom chatRoom = chatRoomRepository.findById(request.getRoomId())
				.orElseThrow(() -> new RuntimeException("Chat room not found"));

		MessengerUser sender = messengerUserRepository.findByUuid(request.getSenderuuid())
				.orElseThrow(() -> new RuntimeException("Sender not found"));

		ChatMessage message = new ChatMessage();
		message.setChatRoom(chatRoom);
		message.setSender(sender);
		message.setContent(request.getContent());

		return chatMessageRepository.save(message);
	}

	@Transactional(readOnly = true)
	public ChatReciverInfoDTO getReceiverInfo(String reciveruuid) {
		ChatReciverInfoDTO ChatReciverInfoDTO = new ChatReciverInfoDTO();
		ChatReciverInfoDTO.setReceiveronline(messengerSessionService.isOnline(reciveruuid));
		ChatReciverInfoDTO.setReceiverName(messengerUserRepository.findByUuid(reciveruuid).get().getName());
		return ChatReciverInfoDTO;
	}
}



