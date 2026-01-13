package com.Messenger.Messenger.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Messenger.Messenger.info.MessengerUser;

public interface MessengerUserRepository extends JpaRepository<MessengerUser, String> {
	Optional<MessengerUser> findByUuid(String uuid);
	Optional<MessengerUser> findByName(String name);

	Optional<MessengerUser> findByEmail(String email);
	boolean existsByUuid(String uuid);

	boolean existsByEmail(String email);

	List<MessengerUser> findByNameContainingIgnoreCase(String name);

	List<MessengerUser> findAllByEmail(String email);

}
