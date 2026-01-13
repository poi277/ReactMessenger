package com.Messenger.Messenger.Friend;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Messenger.Messenger.info.MessengerUser;

public interface FriendRepository extends JpaRepository<Friend, Long> {
	List<Friend> findByUser(MessengerUser user);

	void deleteByUserAndFriend(MessengerUser user, MessengerUser friend);

	boolean existsByUserUuidAndFriendUuid(String userUuid, String friendUuid);


}
