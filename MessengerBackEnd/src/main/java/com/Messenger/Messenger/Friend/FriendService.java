package com.Messenger.Messenger.Friend;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Messenger.Messenger.DTO.FriendDTO;
import com.Messenger.Messenger.info.MessengerUser;
import com.Messenger.Messenger.repository.MessengerUserRepository;
import com.Messenger.Messenger.service.MessengerSessionService;

import jakarta.transaction.Transactional;

@Service
public class FriendService {

	private final FriendRequestRepository friendRequestRepository;
	private final FriendRepository friendRepository;
	private final MessengerUserRepository userRepository; // 사용자 조회용 추가
	@Autowired
	private MessengerSessionService messengerSessionService;

	public FriendService(FriendRequestRepository friendRequestRepository, FriendRepository friendRepository,
			MessengerUserRepository userRepository) {
		this.friendRequestRepository = friendRequestRepository;
		this.friendRepository = friendRepository;
		this.userRepository = userRepository;
	}

	public void sendFriendRequest(String senderUuid, String receiverUuid) {
		MessengerUser sender = userRepository.findByUuid(senderUuid)
				.orElseThrow(() -> new IllegalArgumentException("보내는 사용자 없음"));
		MessengerUser receiver = userRepository.findByUuid(receiverUuid)
				.orElseThrow(() -> new IllegalArgumentException("받는 사용자 없음"));

		if (friendRequestRepository.existsBySenderAndReceiverAndStatus(sender, receiver,
				FriendRequest.Status.PENDING)) {
			throw new IllegalStateException("이미 요청 중입니다.");
		}

		FriendRequest request = new FriendRequest();
		request.setSender(sender);
		request.setReceiver(receiver);
		request.setStatus(FriendRequest.Status.PENDING);
		friendRequestRepository.save(request);
	}

	public List<FriendRequest> getPendingRequests(String receiverUuid) {
		MessengerUser receiver = userRepository.findByUuid(receiverUuid)
				.orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
		return friendRequestRepository.findByReceiverAndStatus(receiver, FriendRequest.Status.PENDING);
	}

	public void acceptFriendRequest(Long requestId) {
		FriendRequest request = friendRequestRepository.findById(requestId)
				.orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다."));
		Friend friend1 = new Friend();
		friend1.setUser(request.getSender());
		friend1.setFriend(request.getReceiver());
		friendRepository.save(friend1);
		Friend friend2 = new Friend();
		friend2.setUser(request.getReceiver());
		friend2.setFriend(request.getSender());
		friendRepository.save(friend2);
		friendRequestRepository.delete(request);
	}

	public void rejectFriendRequest(Long requestId) {
		FriendRequest request = friendRequestRepository.findById(requestId)
				.orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다."));
		friendRequestRepository.delete(request);
	}

	public List<FriendDTO> getFriendsList(String userUuid) {
		MessengerUser user = userRepository.findByUuid(userUuid)
				.orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

		return user.getFriends().stream()
				.map(f -> new FriendDTO(f, messengerSessionService.isOnline(f.getFriend().getUuid())))
				.collect(Collectors.toList());
	}
	@Transactional
	public void removeFriend(String userUuid, String friendUuid) {
		MessengerUser user = userRepository.findByUuid(userUuid)
				.orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
		MessengerUser friend = userRepository.findByUuid(friendUuid)
				.orElseThrow(() -> new IllegalArgumentException("친구 사용자 없음"));

		friendRepository.deleteByUserAndFriend(user, friend);
		friendRepository.deleteByUserAndFriend(friend, user);
	}
}

