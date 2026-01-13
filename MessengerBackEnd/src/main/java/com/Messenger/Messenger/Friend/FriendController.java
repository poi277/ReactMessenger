package com.Messenger.Messenger.Friend;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.Messenger.Messenger.DTO.FriendDTO;
import com.Messenger.Messenger.DTO.FriendRequestDTO;
import com.Messenger.Messenger.DTO.UserSessionDTO;
import com.Messenger.Messenger.chating.ChatRoom;
import com.Messenger.Messenger.chating.ChatRoomRepository;
import com.Messenger.Messenger.info.MessengerUser;
import com.Messenger.Messenger.repository.MessengerUserRepository;

import jakarta.servlet.http.HttpSession;


@RestController
@RequestMapping("/friend")
public class FriendController {

	private final FriendService friendService;
	private final FriendRepository friendRepository;
	private final MessengerUserRepository messengerUserRepository;
	@Autowired
	private ChatRoomRepository chatRoomRepository;
	

	FriendController(FriendService friendService, MessengerUserRepository messengerUserRepository
			,FriendRepository friendRepository) {
		this.friendService = friendService;
		this.messengerUserRepository = messengerUserRepository;
		this.friendRepository =friendRepository;
	}

	@PostMapping("/sendrequest/{receiveruuid}")
	public ResponseEntity<String> sendRequest(@PathVariable String receiveruuid) {
		String senderUuid = currentSesseionUserUuid(); // UUID 사용
		friendService.sendFriendRequest(senderUuid, receiveruuid);
		return ResponseEntity.ok("친구 요청을 보냈습니다.");
	}

	@GetMapping("/requestslist")
	public ResponseEntity<List<FriendRequestDTO>> getFriendRequests() {
		String userUuid = currentSesseionUserUuid();
		List<FriendRequestDTO> dtos = friendService.getPendingRequests(userUuid).stream().map(FriendRequestDTO::new)
				.toList();
		return ResponseEntity.ok(dtos);
	}

	@PostMapping("/accept/{requestId}")
	public ResponseEntity<String> acceptRequest(@PathVariable Long requestId) {
		friendService.acceptFriendRequest(requestId);
		return ResponseEntity.ok("친구 요청을 수락했습니다.");
	}

	@PostMapping("/reject/{requestId}")
	public ResponseEntity<String> rejectRequest(@PathVariable Long requestId) {
		friendService.rejectFriendRequest(requestId);
		return ResponseEntity.ok("친구 요청을 거절했습니다.");
	}

	@GetMapping("/list")
	public ResponseEntity<List<FriendDTO>> getFriendsList() {
		String userUuid = currentSesseionUserUuid(); // id 대신 uuid 사용
		return ResponseEntity.ok(friendService.getFriendsList(userUuid));
	}

	@DeleteMapping("/remove/{friendUuid}")
	public ResponseEntity<String> removeFriend(@PathVariable String friendUuid) {
		String userUuid = currentSesseionUserUuid(); // id 대신 uuid 사용
		friendService.removeFriend(userUuid, friendUuid);

		MessengerUser sender = messengerUserRepository.findByUuid(userUuid)
				.orElseThrow(() -> new RuntimeException("Sender not found"));
		MessengerUser receiver = messengerUserRepository.findByUuid(friendUuid)
				.orElseThrow(() -> new RuntimeException("Receiver not found"));
		ChatRoom chatRoom = chatRoomRepository.findOneToOneRoom(sender.getId(), receiver.getId()).orElse(null);
		if (chatRoom != null) {
		chatRoomRepository.deleteById(chatRoom.getId());
		}
		return ResponseEntity.ok("친구를 삭제했습니다.");
	}

	private String currentSesseionUserUuid() {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = attr.getRequest().getSession(false);
		if (session == null)
			throw new IllegalStateException("세션이 없습니다.");
		UserSessionDTO userSession = (UserSessionDTO) session.getAttribute("USER");
		if (userSession == null)
			throw new IllegalStateException("로그인 정보가 없습니다.");

		return userSession.getUuid();
	}
}
