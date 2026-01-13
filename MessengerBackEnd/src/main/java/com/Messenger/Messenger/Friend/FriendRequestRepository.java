package com.Messenger.Messenger.Friend;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Messenger.Messenger.info.MessengerUser;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
	boolean existsBySenderAndReceiverAndStatus(MessengerUser sender, MessengerUser receiver,
			FriendRequest.Status status);

	List<FriendRequest> findByReceiverAndStatus(MessengerUser receiver, FriendRequest.Status status);

	@Override
	Optional<FriendRequest> findById(Long id);
}
